package jsteffi;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;


import java.util.*;
import java.io.*;
import java.time.LocalDate;

import jsteffi.utilities.*;

public class Cell {
	public Hemisegment hemiseg;
	public int vlNum;
	
	public File roiPath;
	public Roi roi;
	
	public ArrayList<Double> roiX = new ArrayList<Double>(0);
	public ArrayList<Double> roiY = new ArrayList<Double>(0);
	
	
	public Overlay nucRois;
	
	
	public Hashtable<String, double[]> nuc_data;
		//or double[][] nuc_data;
		//or ArrayList<ArrayList<double>> nuc_data;
		//or HashTable<double[]> nuc_data;
		
	
	
	
	
	public Cell(Hemimsegment h, File roiCsvPath, int vlMuscleNum, Calibration cal) {
		hemiseg = h;
		vlNum = vlMuscleNum;
		
		roiPath = roiCsvPath;
		roi = openRoiCsv(roiPath, cal);
	}
		
		
		
	public Roi openRoiCsv(File roiCsvPath, Calibration cal) {
		try (Scanner sc = new Scanner(roiCsvPath)) {
			sc.useDelimiter(",|\\s");
			
			while (sc.hasNextDouble()) {
				double temp = sc.nextDouble();
				//float aksjdf = temp;
				roiX.add(cal.getRawX(temp));
				
				if (sc.hasNextDouble()) {
					temp = sc.nextDouble();
					roiY.add(cal.getRawY(temp));
				}
				else {
					//throw exception
					IJ.log("shit, in Cell.openRoiCsv, got x value with no matching y value");
				}
			}
			
			float[] xF = ArrLisDouToArrFlo(roiX);
			float[] yF = ArrLisDouToArrFlo(roiY);
			
			return new PolygonRoi(xF,yF,2);
		}
		catch (FileNotFoundException e) {
			IJ.log("in Cell.openRoiCsv exception: " + e);
			return null;
		}
	}
		
	// ArrayList<double> to float[]
	public float[] ArrLisDouToArrFlo(ArrayList<Double> arrLis) {
		float[] f = new float[arrLis.size()];
		for (int i = 0; i < arrLis.size(); i++) {
			f[i] = arrLis.get(i).floatValue();
		}
		return f;
	}
		
		
		
		//Roi temp = new Roi(0,0,0,0,0);
		
		//roiX = new
		
		
	
		
	
}



