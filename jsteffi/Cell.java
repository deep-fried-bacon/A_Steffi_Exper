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
	
	public boolean nucsLoaded = false;

	public ArrayList<Nucleus> nucs = new ArrayList<Nucleus>();
	public int nucCount;
	
	/*** P stands for Pointers 
		 the idea is that the data is "stored" in the Nucleus objects 
		 and the objects in cell are simply pointing to them ***/
		 
	public ArrayList<Roi> nucRoiP;
	
	/*** nucGeoDataP.get("<Column Heading>");
		 nucGeoDataP.get("<Column Heading>").get(<NucId>); ***/
	public Hashtable<String,ArrayList<MutableDouble>> nucGeoDataP;
	
	
	public Cell(Hemisegment hemiseg, File roiPath, int vlNum, Calibration cal) {
		this.hemiseg = hemiseg;
		this.vlNum = vlNum;
		
		this.roiPath = roiPath;
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
					/*** exception ***/
					IJ.log("shit, in Cell.openRoiCsv, got x value with no matching y value");
				}
			}
			float[] xF = ArrLisDouToArrFlo(roiX);
			float[] yF = ArrLisDouToArrFlo(roiY);
			
			return new PolygonRoi(xF,yF,2);
		}
		catch (FileNotFoundException e) {
			/*** exception ***/
			IJ.log("in Cell.openRoiCsv exception: " + e);
			return null;
		}
	}
		
		
/* 	public Hashtable<String,double[]> readRT(ResultsTable rt) {
		String[] headings = rt.getHeadings();
		Hashtable<String,double[]> data = new Hashtable<String,double[]>(headings.length);
		
		for (int i = 0; i < headings.length; i++) {
			data.put(headings[i],rt.getColumnAsDoubles(rt.getColumnIndex(headings[i])));
		}
		return data;
		
	}
	 */	
		
	public boolean makeNucDataPointers() {
		if (nucsLoaded == false) return false;
		else {
			nucRoiP = new ArrayList<Roi>(nucCount);
			//headings = 
			nucGeoDataP = new Hashtable<String,ArrayList<MutableDouble>>(hemiseg.geoHeadings.length);
			for (String heading : hemiseg.geoHeadings) {
					nucGeoDataP.put(heading,new ArrayList<MutableDouble>(nucCount));
			}			
			for (int i = 0; i < nucCount; i++) {
				nucRoiP.add(i, nucs.get(i).roi);
				
				for (String heading : hemiseg.geoHeadings) {
					
					/*** nucs --> ArrayList<Nucleus>
						 .get(i) --> Nucleus
						 Nucleus.geoData --> Hashtable<String,MutableDouble>
						 geoData.get(Heading --> String) --> MutableDouble ***/
					MutableDouble val = nucs.get(i).geoData.get(heading);
					nucGeoDataP.get(heading).add(i,val);
				}				
			}
			return true;
		}
	}
	
	/*** ArrayList<double> to float[] ***/
	public float[] ArrLisDouToArrFlo(ArrayList<Double> arrLis) {
		float[] f = new float[arrLis.size()];
		for (int i = 0; i < arrLis.size(); i++) {
			f[i] = arrLis.get(i).floatValue();
		}
		return f;
	}
}



