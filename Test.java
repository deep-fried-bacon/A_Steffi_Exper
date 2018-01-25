import java.util.*;
import java.io.*;


public class Test {
	
	
	public static void poop (String[] args) {
		ArrayList<MutableDouble> myArr = new ArrayList<MutableDouble>();
		for (int i = 0; i < 10; i++) {
			myArr.add(new MutableDouble(i));
		}
		System.out.println(myArr);
		
		
		myArr.get(8).value = 10000;
				System.out.println(myArr);

	}
	
	
	
	
	public static void main(String[] args) {
		
		// nucs 1, 2, 3
		// Mtypes a, b, c, d
		
		MutableDouble a1 = new MutableDouble(1);
		MutableDouble a2 = new MutableDouble(2);
		MutableDouble a3 = new MutableDouble(3);

		MutableDouble b1 = new MutableDouble(1);
		MutableDouble b2 = new MutableDouble(4);
		MutableDouble b3 = new MutableDouble(9);
		
		MutableDouble c1 = new MutableDouble(1);
		MutableDouble c2 = new MutableDouble(8);
		MutableDouble c3 = new MutableDouble(27);
		
		MutableDouble d1 = new MutableDouble(1);
		MutableDouble d2 = new MutableDouble(16);
		MutableDouble d3 = new MutableDouble(81);
		
		
		
		
		
		ArrayList<MutableDouble> nuc1SingleM = new ArrayList<MutableDouble>(Arrays.asList(a1,b1,c1,d1));
		ArrayList<MutableDouble> nuc2SingleM = new ArrayList<MutableDouble>(Arrays.asList(a2,b2,c2,d2));
		ArrayList<MutableDouble> nuc3SingleM = new ArrayList<MutableDouble>(Arrays.asList(a3,b3,c3,d3));
		
		//ArrayList<MutableDouble>[] nucData = new ArrayList<MutableDouble>[]{nuc1SingleM,nuc2SingleM,nuc3SingleM};
		
		
		// Hashtable {Mtype:Array<nuc>}
		Hashtable<String,ArrayList<MutableDouble>> cellSingleM = new Hashtable<String,ArrayList<MutableDouble>>();
		
		String[] Mtypes = new String[]{"a","b","d","c"};
		
		for (int i = 0; i < 4; i++) {
			ArrayList<MutableDouble> temp = new ArrayList<MutableDouble>();
			
			temp.add(nuc1SingleM.get(i));
			temp.add(nuc2SingleM.get(i));
			temp.add(nuc3SingleM.get(i));
			
			
			cellSingleM.put(Mtypes[i],temp);
		}
		
		nuc1SingleM.get(1).value = 10000;
		
		
		
		System.out.println("nuc 1\na\tb\tc\td");
		for (MutableDouble one : nuc1SingleM) {
			System.out.print("" + one + "\t");
		}
		System.out.println("");
		
		System.out.println("nuc 2\na\tb\tc\td");
		for (MutableDouble one : nuc2SingleM) {
			System.out.print("" + one + "\t");
		}
		System.out.println("");

		
		System.out.println("nuc 3\na\tb\tc\td");
		for (MutableDouble one : nuc3SingleM) {
			System.out.print("" + one + "\t");
		}
		System.out.println("");
		System.out.println("");

		
		
		System.out.println("cell");
		
		for (int i = 0; i < 4; i++) {
			System.out.println("" + Mtypes[i] + " " + cellSingleM.get(Mtypes[i]));
		}
		
		
		
	}
	
	
	
	
}

class MutableDouble {

		public double value;

		public MutableDouble(double value) {
			this.value = value;
		}
		public void set(double v) {
			value = v;
		}
		public String toString() {
			return (String.valueOf(value));
		}
}

