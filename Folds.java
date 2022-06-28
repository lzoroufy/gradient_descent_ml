/*
 * Folds class creates and holds the list of folds.  It also creates the the training set by withholding a fold.
 * Luke Zoroufy
 * 10/30/2020
 */
import java.util.ArrayList;
import java.util.Random;

public class Folds {
	ArrayList<DataSet> folds;
	int numFolds;
	
	public Folds(DataSet ds, int k) {
		numFolds = k;
		folds = new ArrayList<DataSet>();
		for(int i = 0; i < k; i++) {
			folds.add(new DataSet());
		}
		int[] counts = new int[k];
		for(int i = 0; i < ds.getSize(); i++) {
			counts[i%k]++;
		}
		Random gen = new Random();
		int num;
		for(int i = 0; i < ds.getSize(); i++) {
			num = gen.nextInt(k);
			while(counts[num] == 0){
				num = gen.nextInt(k);
			}
			folds.get(num).add(ds.get(i));
			counts[num]--;
		}
	}
	public DataSet createFittingSet(int index){
		DataSet ds = new DataSet();
		for(int i = 0; i < numFolds; i++){
			if(i != index){
				ds.add(folds.get(i));
			}
		}
		return ds;
	}
	public DataSet get(int i){
		return folds.get(i);
	}

}
