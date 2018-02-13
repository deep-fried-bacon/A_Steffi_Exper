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
	
	public File path;
	
	public String name;
	public LocalDate date;
	public String genotype;
	
	public ExperimentView experView;
	
	public Hashtable<String, Integer> channels;
	
	public ArrayList<File> hemisegFileList;
	public ArrayList<Hemisegment> hemisegs;
	
	
	public int specCount = 0;
	public int specCount2 = 0;
	
	
	public Experiment(File path) {
		insts.add(this);
		
		//makePath();		
		this.path = path;
		/** experView = new ExperimentView(this) **/
		
		parseName();

		loadChannels();

		createHemisegs();
		
		loadNucs();
	}
	
	public void close() {
		insts.remove(insts.indexOf(this));
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
			if (subDirs[i].getName().startsWith(name) && subDirs[i].isDirectory()) {
				hemisegFileList.add(subDirs[i]);
				hemisegs.add(new Hemisegment(this, subDirs[i]));
			}	
		}
	}
	
	public void loadNucs() {
		for (Hemisegment h : hemisegs) {
			h.loadNucs();
		}
	}
	
	/** give null ArrayLists if you don't 
		want any data printed from that set of data 
		
		if String FileSuf == null?
	**/
 	
		/** for now only dealing with data3D and geo, not intens **/
	public boolean exportNucData(String fileSuf, ArrayList<String> geoHeadings, /** ArrayList<String> intensHeadings,**/ ArrayList<String> data3DHeadings){
		//IJ.log("geoHeadings = " + geoHeadings);
		//IJ.log("data3DHeadings = " + data3DHeadings);
		File outCsv = new File(path, name + "_" + fileSuf + ".csv");
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
							//IJ.log(temp);
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
							//IJ.log(temp);
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
			IJ.log("specCount" + specCount);
			IJ.log("specCount2" + specCount2);
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
	
	
	public String toString() {
		return ("jsteffi.Experiment: " + name);
	}
	
	public String toStringLong() {
		
		String has = "\nHas: ";
		String doesntHave = "\nDoesn't Have: ";
		
		if (path == null) doesntHave += "path, ";
		else has += "path, ";
		
		if (name == null) doesntHave += "name, ";
		else has += "name, ";
		if (date == null) doesntHave += "date, ";
		else has += "date, ";
		if (genotype == null) doesntHave += "genotype, ";
		else has += "genotype, ";
		
		if (experView == null) doesntHave += "experView, ";
		else has += "experView, ";
		
		if (channels == null) doesntHave += "channels, ";
		else has += "channels, ";
		
		if (hemisegFileList == null) doesntHave += "hemisegFileList, ";
		else has += "hemisegFileList, ";
		if (hemisegs == null) doesntHave += "hemisegs, ";
		else has += "hemisegs, ";
		
		
		
		
		has = has.substring(0, has.length() - 4);
		doesntHave = doesntHave.substring(0, doesntHave.length() - 4);
		
		return (this.toString()
				+ has
				+ doesntHave);			
	}
	
	public String fullSummary() {
		String temp = this.toString();
		
		temp += ("\npath: " + path);
		
		temp += ("\nname: " + name);
		temp += ("\ndate: " + date);
		temp += ("\ngenotype: " + genotype);
		
		temp += ("\nexperView: " + experView);
		
		temp += ("\nchannels: " + channels);
		
		temp += ("\nhemisegFileList: " + hemisegFileList);
		temp += ("\nhemisegs: " + hemisegs);
		
		return temp;
	}
}