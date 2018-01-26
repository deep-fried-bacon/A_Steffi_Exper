package jsteffi;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.filter.*;

import java.util.*;
import java.io.*;
import java.time.LocalDate;

//import jsteffi.utilities.*;


public class Hemisegment { 
	
	public static int GEO = 159457; //without display label 158433
	public static int INTENS = 1934366 ; //without display label 1933342
	
	// SUF = suffix
	public static String HYP_SUF = ".tif";
	public static String NUC_BIN_SUF =  "_Nuc-bin.tif";
	
	public static String VL3_CSV_SUF =  "_XY-VL3.csv";
	public static String VL4_CSV_SUF =  "_XY-VL4.csv";
	
	
	
	public Experiment exper;
	
	public File path;
	public String name;
	public ArrayList<File> fileList = new ArrayList<File>();
	
	public ResultsTable rt;
	

	public ImagePlus hyp = null;
	public Calibration cal = null;

	public ImagePlus nucBin = null;
	
	public File vl3Csv = null;
	public File vl4Csv = null;
	
	public Cell vl3 = null;
	public Cell vl4 = null;

	

	
	public Hemisegment(Experiment exper, File hsPath) {
		this.exper = exper;
		this.path = hsPath;
		name = path.getName();

		loadFiles();

	}
	
	public void loadFiles() {
		File[] allFiles = path.listFiles();

		for (int i = 0; i < allFiles.length; i++) {			
			if (allFiles[i].getName().equals(buildFileName(HYP_SUF))) {
				hyp = IJ.openImage(allFiles[i].getPath());
				cal = hyp.getCalibration();
			}
			else if (allFiles[i].getName().equals(buildFileName(NUC_BIN_SUF))) {
				nucBin = IJ.openImage(allFiles[i].getPath());
			}
			else if (allFiles[i].getName().equals(buildFileName(VL3_CSV_SUF))) {
				vl3Csv = allFiles[i];
			}
			else if (allFiles[i].getName().equals(buildFileName(VL4_CSV_SUF))) {
				vl4Csv = allFiles[i];
			}
			else {
				fileList.add(allFiles[i]);
			}
		}
		
		if (cal != null) {
			if (vl3Csv != null) {
				vl3 = new Cell(this, vl3Csv, 3, cal);
			}
			if (vl4Csv != null) {
				vl4 = new Cell(this, vl4Csv, 4, cal);
			}
			
		}
		
	}
	
	public void loadNucs() {

		nucBin.setOverlay(null);
		
		rt = new ResultsTable();
		
		ParticleAnalyzer pa = new ParticleAnalyzer(
			ParticleAnalyzer.SHOW_OVERLAY_OUTLINES,
			GEO,rt,0,
			Double.POSITIVE_INFINITY);
	
		pa.analyze(nucBin);
		Overlay nucRoisH = nucBin.getOverlay();
			
		for (int i = 0; i < nucRoisH.size(); i++) {
			Hashtable<String,Double> nucRow = getRtRow(rt,i);
			Roi nucRoi = nucRoisH.get(i);
			int nucRoiX = (int)nucRoi.getContourCentroid()[0];
			int nucRoiY = (int)nucRoi.getContourCentroid()[1];

			if (vl3 != null && vl3.roi.contains(nucRoiX,nucRoiY)) {
				vl3.nucs.add(new Nucleus(nucRoi,nucRow));
			}
			else if (vl4 != null && vl4.roi.contains(nucRoiX,nucRoiY)) {
				vl4.nucs.add(new Nucleus(nucRoi,nucRow));
			}
			else {
				/*** exception ***/
				IJ.log("uh-oh nucRoi not contained in any cells");
			}
		}
		
		if (vl3 != null) vl3.nucsLoaded = true;
		if (vl4 != null) vl4.nucsLoaded = true;
		nucBin.setOverlay(null);
		

	}
	
	
	public static Hashtable<String,Double> getRtRow(ResultsTable rt, int rowNum) {
		String[] headings = rt.getHeadings();
	
		int colCount = headings.length;
		IJ.log("rowNum = " + rowNum);
		Hashtable<String,Double> row = new Hashtable<String,Double>(colCount);
		for (int i = 0; i < colCount; i++) {
			
			String heading = headings[i];
			IJ.log("heading = " + heading);
			
			int colIndex = rt.getColumnIndex(heading);
			if (colIndex == ResultsTable.COLUMN_NOT_FOUND) {
				if (heading != "Label") {
					/*** exception ***/
					IJ.log("in getRtRow rt heading missing, heading = " + heading);
				}
			}
			else {
			
				Double val = rt.getValueAsDouble(colIndex,rowNum);
			
				row.put(heading,val);
			}
		}
		return row;
	}
	
	
	
	public String buildFileName(String suffix) {
		return (name + suffix);
	}
	public File buildFile(String suffix) {
		return new File(path, name + suffix);
	}
	
	
	
	
	
	
	
}



