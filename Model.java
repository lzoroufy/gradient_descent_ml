/*
 * Model class that is responsible for creating, fitting, and testing the model
 * Luke Zoroufy
 * 10/30/2020
 */
import java.util.ArrayList;
import java.util.Random;

public class Model {
    double[] weights;
    int order;
    int epochLimit;
    double alpha;
    int batchSize;
    final double LIMIT = .0000000001;
    int v = 1;
    public Model(int d, double a, int el, int m,int v){
        order = d;
        alpha = a;
        epochLimit = el;
        batchSize = m;
        this.v = v;
    }
    public void printWeights(){
        for(int i = 0; i < weights.length-1; i ++){
            System.out.print(weights[i]+", ");
        }
       System.out.println(weights[weights.length-1]);
    }
    public double[] fit(DataSet ds){
        if(v >= 4){
            System.out.println("\t* Beginning mini-batch gradient descent");
            System.out.println(String.format("\t  (alpha=%.6f, epochLimit=%d, batchSize=%d)",alpha,epochLimit,batchSize));
        }
        long startTime = System.currentTimeMillis();
        double[][] dataSet = augmentData(ds);
        weights = new double[dataSet[0].length-1];
        for(int i = 0; i < weights.length; i++){
            weights[i] = 0;
        }
        //printWeights();
        int t = 0;
        int e = 0;
        double curCost = J(dataSet);
        double prevCost = 0;
        while(e < epochLimit && curCost > LIMIT && Math.abs(prevCost - curCost) > LIMIT){
            if(e%1000 == 0 && v >= 4){
                System.out.print(String.format("\t  Epoch %6d (iter %6d): Cost =\t %12.9f",e,t,curCost));
                if(v >= 5){
                    System.out.print(String.format(";\tModel: Y = %.4f",weights[0]));
                    for(int i = 1; i < weights.length; i++){
                        System.out.print(String.format(" + %.4f X%d",weights[i],i));
                    }
                }
                System.out.println();
            }
            //mini batch
            if(batchSize == 0){
                batchSize = dataSet.length;
            }
            //split data into batches
            ArrayList<double[][]> batches = splitData(dataSet, batchSize);
            int numBatch = batches.size();
            double[][] errVal = new double[numBatch][batchSize];
            double[][] batch;


            //for each batch
            for(int b = 0; b<numBatch; b++) {
                batch = batches.get(b);
                // calculate the error at each point
                for (int i = 0; i < batch.length; i++) {
                    double subTotal = 0;
                    for (int j = 0; j < weights.length; j++) {
                        subTotal += weights[j] * batch[i][j];
                    }
                    errVal[b][i] = batch[i][batch[i].length - 1] - subTotal;
                }
                //for each weight
                for (int k = 0; k < weights.length; k++) {
                    double temp;
                    //estimate derivative
                    double compA = 0;
                    for (int i = 0; i < batch.length; i++) {
                        compA += (-2 * batch[i][k]) * errVal[b][i];
                    }
                    compA = compA / batch.length;
                    //multiply estimated derivative by alpha
                    double comp = alpha * compA;
                    //update weight k
                    temp = weights[k] - comp;
                    weights[k] = temp;
                }
                t++;
            }
            e++;
            prevCost = curCost;
            curCost = J(dataSet);

        }
        long endTime = System.currentTimeMillis();
        long fitTime = endTime - startTime;
        double avgTime = fitTime/(double)t;
        if(v >= 4){
            System.out.print(String.format("\t  Epoch %6d (iter %6d): Cost =\t %12.9f",e,t,curCost));
            if(v >= 5){
                System.out.print(String.format(";\tModel: Y = %.4f",weights[0]));
                for(int i = 1; i < weights.length; i++){
                    System.out.print(String.format(" + %.4f X%d",weights[i],i));
                }
            }
            System.out.println();
        }
        if(v >= 4){
            System.out.println("\t* Done with fitting!");
        }
        if(v >= 3){
            System.out.println(String.format("\tTraining took %dms, %d epochs, %d iterations (%.4fms / iteration)",fitTime,e,t,avgTime));
            if(e == epochLimit){
                System.out.println("\tGD Stop condition: Reached Epoch Limit of "+epochLimit);
            }
            if(curCost <= LIMIT){
                System.out.println("\tGD Stop condition: Cost ~= 0");
            }
            if(Math.abs(prevCost - curCost) <= LIMIT){
                System.out.println("\tGD Stop condition: ∆Cost ~= 0");
            }
            System.out.print(String.format("\tModel: Y = %.4f",weights[0]));
            for(int i = 1; i < weights.length; i++){
                System.out.print(String.format(" + %.4f X%d",weights[i],i));
            }
            System.out.println();
        }
        return weights;
    }
    private ArrayList<double[][]> splitData(double[][] ds, int m){
        int numData = ds.length;
        int numAttr = ds[0].length;
        int numBatches = numData/m;
        ArrayList<double[][]> batches = new ArrayList<double[][]>();
        int[][] a = new int[numData][2];
        Random gen = new Random();
        //assign random numbers to each data point
        for(int i = 0; i < numData; i++){
            a[i][0] = i;
            a[i][1] = gen.nextInt();
        }

        //sort by random number
        boolean sorted = false;
        int[] temp = new int[2];
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < numData - 1; i++) {
                if (a[i][1] > a[i+1][1]) {
                    temp[0] = a[i][0];
                    temp[1] = a[i][1];
                    a[i][0] = a[i+1][0];
                    a[i][1] = a[i+1][1];
                    a[i+1][0] = temp[0];
                    a[i+1][1] = temp[1];
                    sorted = false;
                }
            }
        }
        int index = 0;
        for(int i = 0; i < numBatches; i ++){
            double[][] b = new double[m][numAttr];
            for(int j = 0; j < m; j ++){
                b[j] = ds[index];
                index++;
            }
            batches.add(b);
        }
        double[][] b = new double[numData%m][numAttr];
        for(int j = 0; j < b.length; j ++){
            b[j] = ds[index];
            index++;
        }
        if(b.length != 0){
            batches.add(b);
        }
        return batches;

    }
    private double[][] augmentData(DataSet ds){
        int numAttr = ds.get(0).getNumAttr();
        double[][] dataSet = new double[ds.getSize()][((numAttr)*order)+2];
        for(int i = 0; i<ds.getSize(); i++){
           dataSet[i][0] = 1;
           for(int j = 1; j <= numAttr; j++){
               dataSet[i][j] = ds.get(i).getAttr(j-1);
           }
           for(int j = numAttr+1; j < dataSet[i].length-1; j++){
               dataSet[i][j] = dataSet[i][j-numAttr] * dataSet[i][((j-1)%numAttr)+1];
           }
           dataSet[i][dataSet[i].length-1] = ds.get(i).getValue();
        }
        return dataSet;
    }
    public double loss(double[] d){
        double loss = d[d.length-1] - h(d);
       //System.out.println("yi: "+d[d.length-1]+" h(d): "+ h(d) + " loss: "+loss);
        return loss * loss;
    }
    public double J(double[][] ds){
        double output = 0;
        //System.out.println("**********\noutput: "+output);
        for(int i = 0; i < ds.length; i++){
            output+=loss(ds[i]);
            //System.out.println("output: "+output);
        }
        //System.out.println("output / ds.length = cost: "+output +"/"+(double)ds.length+"="+output/3);
        //System.out.println(ds.length);
        //System.out.println(output/ds.length);
       output/= (double)ds.length;
       //System.out.println("return: "+output);
        return output;
    }
    public double h(double[] d){
        double output = 0;
        for(int i = 0; i < weights.length; i++){
            //System.out.println("weights[i]; "+ weights[i]+ " d[i]: "+d[i]);
            output+= weights[i]*d[i];
        }
        return output;
    }
    public double Error(DataSet ds){
        double[][] dataSet = augmentData(ds);
        return J(dataSet);
    }
}
