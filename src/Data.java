/*
 * Data class that holds a single piece of data
 * Luke Zoroufy
 * 10/30/2020
 */
import java.util.ArrayList;
import java.util.Scanner;

public class Data {
	ArrayList<Double> attributes;
	double value;
	int numOfAttr;
	public Data() {
	}
	public Data(String line){
		attributes = new ArrayList<Double>();
		Scanner parser = new Scanner(line);
		while (parser.hasNextDouble()) {
			attributes.add(parser.nextDouble());
		}
		value = attributes.get(attributes.size()-1);
		attributes.remove(attributes.size()-1);
		numOfAttr = attributes.size();
	}
	public double getValue() {
		return value;
	}
	public double getAttr(int index) {
		return attributes.get(index);
	}
	public int getNumAttr(){
		return numOfAttr;
	}
}
