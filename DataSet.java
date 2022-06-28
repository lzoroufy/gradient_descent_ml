/*
 * DataSet class is responsible for holding and organizing multiple data points
 * Luke Zoroufy
 * 10/30/2020
 */
import java.util.ArrayList;

public class DataSet {
	ArrayList<Data> data;
	int size;
	
	public DataSet() {
		data = new ArrayList<Data>();
		size = 0;
	}
	public void add(Data d) {
		data.add(d);
		size = data.size();
	}
	public void add(DataSet ds){
		for(int i = 0; i < ds.getSize(); i++){
			this.add(ds.get(i));
		}
	}
	public Data get(int index) {
		return data.get(index);
	}
	public void remove(int index) {
		data.remove(index);
		size = data.size();
	}
	public Data getAndRemove(int index) {
		Data d = this.get(index);
		this.remove(index);
		return d;
	}
	public int getSize() {
		size = data.size();
		return size;
	}
}
