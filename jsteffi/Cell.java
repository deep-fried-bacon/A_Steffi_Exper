package jsteffi;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;
import ij.process.*;
import ij.plugin.*;
import ij.plugin.filter.*;

import fiji.threshold.*;


import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.awt.*;

import jsteffi.utilities.*;

public class Cell {
	public Hemisegment hemiseg;
	public int vlNum = -1;
	
	public File roiPath;
	public Roi roi;
	
	public ArrayList<Double> roiX = new ArrayList<Double>(0);
	public ArrayList<Double> roiY = new ArrayList<Double>(0);

	//public 
	//public Hashtable<String,MutableDouble> geoData = new Hashtable<String,MutableDouble>();
	//Rectangle bounds =
	
	//public boolean nucsLoaded = false;

	public ArrayList<Nucleus> nucs = new ArrayList<Nucleus>();
	public int nucCount = 0;
	
		
	public ImagePlus cellHyp = null;
	public ImagePlus cellOrthStack = null;
	int xLength;
	int yLength;
		
	int channelCount;
	
	int zLength;		// slices
	
	
	public Cell(Hemisegment hemiseg, File roiPath, int vlNum, Calibration cal) {
		this.hemiseg = hemiseg;
		this.vlNum = vlNum;
		
		this.roiPath = roiPath;
		roi = openRoiCsv(roiPath, cal);
		
		// Hemisegment loads nuc right after
		//Rectangle bounds
		
	}
		
	public Roi openRoiCsv(File roiCsvPath, Calibration cal) {
		try (Scanner sc = new Scanner(roiCsvPath)) {
			sc.useDelimiter(",|\\s");
			
			while (sc.hasNextDouble()) {
				double temp = sc.nextDouble();
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
		
	public void makeCellHyp() {
		//hemiseg.hyp.setRoi(roi);
		//cellHyp = hemiseg.hyp.duplicate();
		//hemiseg.hyp.deleteRoi();
		
		cellHyp = Functions.cropStack(hemiseg.hyp, roi);
		
		PolygonRoi tempRoi = new PolygonRoi(roi.getFloatPolygon(),2);
		cellHyp.setRoi(tempRoi);
		tempRoi.setLocation(0,0);
		
		ImagePlus temp = WindowManager.getTempCurrentImage();
		WindowManager.setTempCurrentImage(cellHyp);
		IJ.run("Clear Outside", "stack");
		WindowManager.setTempCurrentImage(temp);
		
		
		xLength = cellHyp.getDimensions()[0];
		yLength = cellHyp.getDimensions()[1];
		
		channelCount = cellHyp.getDimensions()[2];
		
		zLength = cellHyp.getDimensions()[3];
	}
	
	
	
	
	
	
	
	 public void makeCellXOrthView() {
		if (cellHyp == null) {
			makeCellHyp();
		}
		
		this.cellOrthStack = Functions.verticalCrossSection(cellHyp, null);
	}
	
	
	
	
	public MutableDouble yScaled(MutableDouble num) {
		Rectangle bounds = roi.getBounds();
		double height = bounds.height;
		double start = bounds.y;
		
		double yPoint = num.get();
		double yPointTemp = yPoint - start;
		double yPointScaled = yPointTemp/height;
		
		MutableDouble outNum = new MutableDouble(yPointScaled);
		return outNum;
		
	}
	
	public MutableDouble yScaled(double yPoint) {
		Rectangle bounds = roi.getBounds();
		double height = bounds.height;
		double start = bounds.y;
		
		//double temp = num.get();
		//temp = num - y;
		double yPointTemp = yPoint - start;
		double yPointScaled = yPointTemp/height;
		
		MutableDouble outNum = new MutableDouble(yPointScaled);
		return outNum;
		
	}
	
	
	/*** ArrayList<double> to float[]
	***/
	public float[] ArrLisDouToArrFlo(ArrayList<Double> arrLis) {
		float[] f = new float[arrLis.size()];
		for (int i = 0; i < arrLis.size(); i++) {
			f[i] = arrLis.get(i).floatValue();
		}
		return f;
	}
	
	
	
	public String toString() {
		return ("jsteffi.Cell: " + hemiseg.name + " vl" + vlNum);
	}
		
	public String toStringLong() {
		
		String has = "\nHas: ";
		String doesntHave = "\nDoesn't Have: ";
		
		if (hemiseg == null) doesntHave += "hemiseg, ";
		else has += "hemiseg, ";
		if (vlNum == -1) doesntHave += "vlNum, ";
		else has += "vlNum, ";
		
		if (roiPath == null) doesntHave += "roiPath, ";
		else has += "roiPath, ";
		if (roi == null) doesntHave += "roi, ";
		else has += "roi, ";
		
		if (roiX == null) doesntHave += "roiX, ";
		else has += "roiX, ";
		if (roiY == null) doesntHave += "roiY, ";
		else has += "roiY, ";
		
		
		
		if (nucs == null) doesntHave += "nucs, ";
		else has += "nucs, ";
		if (nucCount == -1) doesntHave += "nucCount, ";
		else has += "nucCount, ";
		
		
		
		
		
		
		
		
		has = has.substring(0, has.length() - 4);
		doesntHave = doesntHave.substring(0, doesntHave.length() - 4);
		
		return (this.toString()
				+ has
				+ doesntHave);		
	}
	
	public String fullSummary() {
		String temp = this.toString();
		
		temp += ("\nhemiseg: " + hemiseg);
		temp += ("\nvlNum: " + vlNum);
		
		temp += ("\nroiPath: " + roiPath);
		temp += ("\nroi: " + roi);
		
		temp += ("\nroiX: " + roiX);
		temp += ("\nroiY: " + roiY);
		
		//temp += ("\nnucsLoaded: " + nucsLoaded);
		
		temp += ("\nnucs: " + nucs);
		temp += ("\nnucCount: " + nucCount);
		
		
		
		
		return temp;
	}
}








