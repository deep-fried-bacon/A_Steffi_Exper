package jsteffi;
import ij.*;
import ij.io.*;

import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.charset.Charset;


public class Experiment {
	public static ArrayList<Experiment> insts = new ArrayList<Experiment>();
	public static boolean testing = true;
	//public static DEFAULT
	
	public File path;
	
	public String name;
	public LocalDate date;
	public String genotype;
	
	public ExperimentView experView;
	
	public Hashtable<String, Integer> channels;
	
	public ArrayList<File> hemisegFileList;
	public ArrayList<Hemisegment> hemisegs;
	
	
	public int specCount = 0;
	
	
	public Experiment() {
		insts.add(this);
		
		makePath();		// instantiates path
		//experView = new ExperimentView(this);
		
		loadChannels();

		createHemisegs();
		
		loadNucs();
	}
	
	public void close() {
		insts.remove(insts.indexOf(this));
	}
	
	public void makePath() {
		String pathStr;
		if (testing) {
			pathStr = "C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\150729_w1118";
		}
		else {
			DirectoryChooser dc = new DirectoryChooser("Choose folder containing hemisegment folders.");
			pathStr = dc.getDirectory();
		}
		path = new File(pathStr);
		parseName();
	}
	
	public void parseName() {
		name = path.getName();
		String[] nameList = name.split("_");
		genotype = nameList[1];

		try {
			String tempDate = nameList[0];
			if (tempDate.length() != 6){
				/*** exception ***/
				IJ.log("date of exper folder name is not 6 chars");
			}
			else {
				int year = Integer.parseInt(tempDate.substring(0,2));
				int month = Integer.parseInt(tempDate.substring(2,4));
				int day = Integer.parseInt(tempDate.substring(4,6));
				date = LocalDate.of(2000+year, month, day);
			}
		}
		catch (Exception e) {
			
		}
		
	}
	
	public void loadChannels() {
		File metadata = new File(path, name+"_metadata.py");
		List<String> chanStrL = new ArrayList<String>();
		try {
			chanStrL = Files.readAllLines(metadata.toPath(), Charset.defaultCharset());
		}
		catch (Exception e) {
			IJ.log("couldn't open metadata.py");
			
		}
		//String[] temp = new String
		//String[] chanStrA = chanStrL.toArray(String[]);
		channels = new Hashtable<String, Integer>();
		boolean go = false; 
		for(int i = 0; i < chanStrL.size(); i++) {
			if (go) {
				if (chanStrL.get(i).contains("}")) {
					go = false;
				}
				else {
					String[] tempL = chanStrL.get(i).split(":");
					int v = Integer.parseInt(tempL[0]);
					String k = tempL[1];
					k = k.replaceAll("\"","\t");
					k = k.replaceAll(",","\t");
					k = k.trim();
					//char[] chs = tempL[1].toCharArray();
					//boolean keep = False
					channels.put(k,v);
				}
			}			
			else if (chanStrL.get(i).contains("channels {")) {
				go = true;
			}
		}
	
	}
	
	public void createHemisegs() {
		hemisegFileList = new ArrayList<File>();
		hemisegs = new ArrayList<Hemisegment>();
		File[] subDirs = path.listFiles();
		for(int i = 0; i < subDirs.length; i++) {
			
		//IJ.log(subDirs[i].getName() + " - " + subDirs[
			if (subDirs[i].getName().startsWith(name) && subDirs[i].isDirectory()) {
				hemisegFileList.add(subDirs[i]);
				
				hemisegs.add(new Hemisegment(this, subDirs[i]));
			}	
		}
	}
	
	public void loadNucs() {
		for (Hemisegment h : hemisegs) {
			h.loadNucs();
			h.makeCellDataPointers();
		}
	}
	
	
	/** give null ArrayLists if you don't 
		want any data printed from that set of data 
		
		if String FileSuf == null?
	**/
 	// public void exportNucData(String FileSuf, ArrayList<String> geoHeadings, ArrayList<String> intensHeadings, ArrayList<String> data3DHeadings){
		// make default outputDir and outputFileName
		// exportNucData(headings, outputDir, outputFileName);
	// }
	
	
	
		/** for now only dealing data3D and geo **/
	public boolean exportNucData(String FileSuf, ArrayList<String> geoHeadings, /** ArrayList<String> intensHeadings,**/ ArrayList<String> data3DHeadings){
		IJ.log("geoHeadings = " + geoHeadings);
		IJ.log("data3DHeadings = " + data3DHeadings);
		
		File outCsv = new File(path, name + "_" + FileSuf + ".csv");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(outCsv));
			
			String headings = "Hemisegment,Cell,NucID,";
			for (String heading : geoHeadings) {
				headings += (heading + ",");
			}
			for (String heading : data3DHeadings) {
				headings += (heading + ",");
			}
			
			writer.write(headings);
			for (Hemisegment hemiseg : hemisegs) {
				
				for (Nucleus nuc : hemiseg.vl3.nucs) {
					writer.newLine();

					String temp = (hemiseg.name + ",vl3," + nuc.id + ",");
					for (String heading : geoHeadings) {
						temp += (nuc.geoData.get(heading) + ",");
					}
					
					for (String heading : data3DHeadings) {
						if (nuc.data3D == null) {
							IJ.log(temp);
							temp += "*,";

						}
						else {
							temp += (nuc.data3D.get(heading) + ",");
						}
					}
					writer.write(temp);

				}
				writer.newLine();
				
				
				
				for (Nucleus nuc : hemiseg.vl4.nucs) {
					writer.newLine();

					String temp = (hemiseg.name + ",vl4," + nuc.id + ",");
					for (String heading : geoHeadings) {
						temp += (nuc.geoData.get(heading) + ",");
					}
					for (String heading : data3DHeadings) {
						if (nuc.data3D == null) {
							IJ.log(temp);
							temp += "*,";
						}
						else {
							temp += (nuc.data3D.get(heading) + ",");
						}
					}
					writer.write(temp);

				}
				writer.newLine();
				
				
				

			}
			
			
			writer.close();
			return true;
		}
		catch (FileNotFoundException e) {
			/** deal with exception appropriately **/
			IJ.log("FileNotFoundException in Experiment.exportNucData");
			return false;
		}
		catch (IOException e) {
			/** deal with exception appropriately **/
			IJ.log("IOException in Experiment.exportNucData");
			return false;
		}
		
		

	}
		// make default outputDir and outputFileName
		// exportNucData(headings, outputDir, outputFileName);
	
	
	
	
	
	
	
	/*
	public void exportNucData(ArrayList<String> headings, File outputDir, String ouptutFileName){
		
	}	 */
}