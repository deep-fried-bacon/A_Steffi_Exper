package jsteffi;
import ij.*;
import ij.io.*;


import java.util.*;
import java.io.File;
import java.time.LocalDate;

import utilities.*;


public class Hemisegment implements FileNames{ //implements Mtypes {
	
	public Experiment exper;
	
	public File path;

	public String name;
	public ArrayList<ImagePlusPlus> tifs;
	public ArrayList<File> otherFiles;
		//or public HashMap<String, ImagePlusPlus> images
	
	//public HashMap<String, Cell> vls;
	
	
	
	
	public Hemisegment(Experiment exp, File hsPath) {
		//IJ.log("MEAN = " + MEAN);
		exper = exp;
		path = hsPath;
		name = path.getName();
		
		
		loadTifs();
		// try {
		// }
		// catch (Exception e) {
		
			// IJ.log("finished hemiseg:" + name);
		
		// }
	}
	
	public void loadTifs() {
		File[] myList = path.listFiles();
		tifs = new ArrayList<ImagePlusPlus>();
		otherFiles = new ArrayList<File>();
		
		for(int i = 0; i < myList.length; i++) {
			if (myList[i].getName().endsWith(".tif")) {
				tifs.add(new ImagePlusPlus(myList[i]));
			}
			else {
				otherFiles.add(myList[i]);
			}
		}
		
	}
	
	public void tryForCellRois() {
		for (int i = 0; i < otherFiles.size(); i++) {
			// if (otherFiles[i].getName() == buildFileName(VL_CSV[3]) || otherFiles[i].getName() == buildFileName(VL_CSV[4])) {
				// return;
			// }
		}
	}
	
	
	public String buildFileName(String suffix) {
		return (name + suffix);
	}
	public File buildFile(String suffix) {
		return new File(path, name + suffix);
	}
	
	

	
	
}



