package jsteffi;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;

import java.util.*;
import java.io.*;
import java.time.LocalDate;

//import utilities.*;
import jsteffi.utilities.*;
//import jsteffi.utilities.ImagePlusPlus;


public class Hemisegment { //implements Mtypes {
	
	// SUF = suffix
	public static String HYP_SUF = ".tif";
	public static String NUC_BIN_SUF =  "_Nuc-bin.tif";
	
	public static String VL3_CSV_SUF =  "_XY-VL3.csv";
	public static String VL4_CSV_SUF =  "_XY-VL4.csv";
	
	
	
	public Experiment exper;
	
	public File path;
	public String name;
	public ArrayList<File> fileList = new ArrayList<File>(0);

	public ImagePlusPlus hyp = null;
	public Calibration cal = null;

	public ImagePlusPlus nucBin = null;
	
	public File vl3Csv = null;
	public File vl4Csv = null;
	
	public Cell vl3 = null;
	public Cell vl4 = null;
	

	
	
	
	
	public Hemisegment(Experiment exp, File hsPath) {
		exper = exp;
		path = hsPath;
		name = path.getName();
		
		
		loadFiles();

	}
	
	public void loadFiles() {
		File[] allFiles = path.listFiles();
		//IJ.log("allFiles = " + allFiles);
		
		IJ.log("buildFileName(HYP_SUF) = " + buildFileName(HYP_SUF));
		for (int i = 0; i < allFiles.length; i++) {
			//IJ.log("allFiles["+i+"].getName() = " + allFiles[i].getName());
			
			
			if (allFiles[i].getName().equals(buildFileName(HYP_SUF))) {
				//IJ.log("in if");
				hyp = new ImagePlusPlus(allFiles[i]);
					// *** by having this be ImagePlus I can't access the image as Hemiseg.hyp, I have to do Hemiseg.hyp.getImp()
						// *** however this way images are only opened if used
				
				//IJ.log("hyp created");
				cal = hyp.getImp().getCalibration();
				//IJ.log("cal created");
				//IJ.log("if HYP_SUF --> i = " + i);

			}
			else if (allFiles[i].getName().equals(buildFileName(NUC_BIN_SUF))) {
				nucBin = new ImagePlusPlus(allFiles[i]);
				//IJ.log("if NUC_BIN_SUF --> i = " + i);

			}
			else if (allFiles[i].getName().equals(buildFileName(VL3_CSV_SUF))) {
				vl3Csv = allFiles[i];
				//IJ.log("if VL3_CSV_SUF --> i = " + i);
			}
			else if (allFiles[i].getName().equals(buildFileName(VL4_CSV_SUF))) {
				vl4Csv = allFiles[i];
				//IJ.log("if VL4_CSV_SUF --> i = " + i);				
			}
			else {
				fileList.add(allFiles[i]);
				//IJ.log("else --> i = " + i);
			}
		}
		
		if (cal != null) {
			//IJ.log("cal != null");
			if (vl3Csv != null) {
				vl3 = new Cell(this, vl3Csv, 3, cal);
			}
			if (vl4Csv != null) {
				vl4 = new Cell(this, vl3Csv, 4, cal);
			}
		}
		else {
			//IJ.log("cal == null");
		}
	}
	
	// public void loadFiles() {
		// File[] fileList = path.listFiles();
		// tifs = new ArrayList<ImagePlusPlus>();
		// otherFiles = new ArrayList<File>();
		
		// for(int i = 0; i < fileList.length; i++) {
			// if (fileList[i].getName().endsWith(".tif")) {
				// tifs.add(new ImagePlusPlus(fileList[i]));
				// if (fileList[i].getName() == buildFileName(HYP)) {
					
				// }
			// }
			// else {
				// otherFiles.add(fileList[i]);
				// if (fileList[i].getName() == buildFileName(VL3_CSV)) {
					
				// }
			// }
		// }
		
	//}
	
	
	
	
	// only creates cal
	// public Calibration getCal() {
		
	// }
	
	// public void haveCellRois() {
		// for (int i = 0; i < otherFiles.size(); i++) {
			// if (otherFiles.get(i).getName() == buildFileName(VL_CSV3)) {
				// hasRoi3 = true;
				// vl3 = new Cell(otherFiles.get(i));
			
			// } else if (otherFiles.get(i).getName() == buildFileName(VL_CSV4)) {
				// hasRoi4 = true;
				// vl4 = new Cell(otherFiles.get(i));

			// }
		// }	
	// }
	
	
	// public void listDir() {
		
	// }
	
	
	public String buildFileName(String suffix) {
		return (name + suffix);
	}
	public File buildFile(String suffix) {
		return new File(path, name + suffix);
	}
	
	

	
	
}



