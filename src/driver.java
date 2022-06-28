/*
 * Driver program for linear regression gradient descent model
 * Luke Zoroufy
 * 10/30/2020
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class driver {
	public static void main(String[] args) throws Exception {
		new driver(args);
	}
	File f;
	int k = 5;
	int d = 1;
	int D = d;
	double a = .005;
	int e = 10000;
	int m = 0;
	int v = 1;
	ArrayList<Double> trainMSE;
	ArrayList<Double> validMSE;
	public driver(String[] args) throws Exception {
		parseArgs(args);
		DataSet data = loadData();
		Folds folds = null;
		if(k!=1) {
			folds = new Folds(data,k);
			System.out.println("Using " + k + "-fold cross-validation.");
		}
		if(v == 1) {
			System.out.println(String.format("%8s\t%8s\t%8s", "Degree", "TrainMSE", "ValidMSE"));
		}
		trainMSE = new ArrayList<Double>();
		validMSE = new ArrayList<Double>();
		for(int i = d; i<=D; i++) {
			if(v == 2){
				System.out.println("--------------------------------\n* Testing degree "+i);
				System.out.println(String.format("%8s\t%8s\t%8s","","TrainMSE","ValidMSE"));
			}
			if(v >= 3){
				System.out.println("--------------------------------\n* Testing degree "+i);
			}
			double validErrorTotal = 0;
			double trainErrorTotal = 0;
			for(int j = 0; j<k; j++) {
				if(v>=3){
					if(k == 1){
						System.out.println("  * Training with all data. . .");
					}
					else {
						System.out.println("  * Holding out Fold " + (j + 1) + ". . .");
					}
				}
				Model model = new Model(i,a,e,m,v);
				double validError;
				double trainError;
				if(k!=1) {
					DataSet ds = folds.createFittingSet(j);
					model.fit(ds);
					validError = model.Error(folds.get(j));
					validErrorTotal += validError;
					trainError = model.Error(ds);
					trainErrorTotal += trainError;
				}
				else{
					DataSet ds = data;
					model.fit(ds);
					validError = model.Error(ds);
					validErrorTotal += validError;
					trainError = model.Error(ds);
					trainErrorTotal += trainError;
				}
				if(v == 2){
					System.out.println(String.format("%8s\t%8f\t%8f","F_"+(1+j),trainError,validError));
				}
				if(v >= 3){
					System.out.println(String.format("\tCurFoldTrainErr:\t%8f",trainError));
					System.out.println(String.format("\tCurFoldValidErr:\t%8f",validError));
					System.out.println();
				}
			}
			double avgValidError = validErrorTotal/(double)k;
			double avgTrainError = trainErrorTotal/(double)k;
			trainMSE.add(avgTrainError);
			validMSE.add(avgValidError);
			if(v == 1) {
				System.out.println(String.format("%8d\t%8f\t%8f", i, avgTrainError, avgValidError));
			}
			if(v == 2 && k != 1){
				System.out.println(String.format("%8s\t%8f\t%8f","Avg:",avgTrainError, avgValidError));
			}
			if(v >= 3 && k != 1){
				System.out.println("  * Averaging across the folds");
				System.out.println(String.format("\tAvgFoldTrainErr:\t%8f",avgTrainError));
				System.out.println(String.format("\tAvgFoldValidErr:\t%8f",avgValidError));
			}


		}

	}
	public DataSet loadData() throws Exception {
		DataSet ds = new DataSet();
		Scanner sc = new Scanner(f);
		while(sc.hasNextLine()){
			ds.add(new Data(sc.nextLine()));
		}
		return ds;
	}
	public void parseArgs(String[] args) {
		int numArgs = args.length;
		for(int i = 0; i<numArgs; i++) {
			if(args[i].equals("-f")) {
				f = new File(args[i+1]);
				i++;
			}
			else if(args[i].equals("-k")) {
				k = Integer.parseInt(args[i+1]);
				i++;
			}
			else if(args[i].equals("-d")) {
				d = Integer.parseInt(args[i+1]);
				D=d;
				i++;
			}
			else if(args[i].equals("-D")) {
				D = Integer.parseInt(args[i+1]);
				i++;
			}
			else if(args[i].equals("-a")) {
				a = Double.parseDouble(args[i+1]);
				i++;
			}
			else if(args[i].equals("-e")) {
				e = Integer.parseInt(args[i+1]);
				i++;
			}
			else if(args[i].equals("-m")) {
				m = Integer.parseInt(args[i+1]);
				i++;
			}
			else if(args[i].equals("-v")) {
				v = Integer.parseInt(args[i+1]);
				i++;
			}
			else {
				System.out.println("invalid argument "+args[i]);
			}
		}
	}
}
