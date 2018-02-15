package jsteffi;
import ij.*;
import ij.io.*;

import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.charset.Charset;

import jsteffi.utilities.*;


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
	
	
	public ArrayList<Cell> cells;
	public ArrayList<Nucleus> nucs;
	
	
	
	//public int specCount = 0;
	//public int specCount2 = 0;
	
	public static Experiment experConstructEverything(File path, String outFileSuf, ArrayList<String> headings) {
		Experiment exper = new Experiment(path);
		exper.runEverything();
		exper.exportNucData(outFileSuf, headings);
		return exper;
	}
	
	public Experiment(File path) {
		insts.add(this);
		
		//makePath();		
		this.path = path;
		/** experView = new ExperimentView(this) **/
		
		parseName(); // sets name, data, genotype

		loadChannels(); // sets channels 

		createHemisegs(); // sets hemisegs
		
		createIterables();
		
		//loadNucs();
	}
	
	public void createIterables() {
		cells = new ArrayList<Cell>();
		nucs = new ArrayList<Nucleus>();
		for (Hemisegment hemiseg : hemisegs) {
			for (Cell c : hemiseg.cells) {
				cells.add(c);
				for (Nucleus nuc : c.nucs) {
					nucs.add(nuc);
				}
			}
			
		}
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
	// public boolean _exportNucData(String fileSuf, ArrayList<String> headings) {
		
		// File outCsv = new File(path, name + "_" + fileSuf + ".csv");
		// BufferedWriter writer = null;
		
		// try {
			// writer = new BufferedWriter(new FileWriter(outCsv));
			// String labels = "Hemisegment,Cell,NucID,";
			
			// for (String heading : geoHeadings) {
				// labels += (heading + ",");
			// }
			
			
			// writer.write(labels);
			
			// for (Hemisegment hemiseg : hemisegs) {
				
				// for (Nucleus nuc : hemiseg.vl3.nucs) {
					// writer.newLine();

					// String temp = (hemiseg.name + ",vl3," + nuc.id + ",");
					// for (String heading : headings) {
						// if (nuc.data.containsKey(heading)) temp += (nuc.data.get(heading) + ",");
						// else temp += ",";
					// }
					// writer.write(temp);
				// }
				// writer.newLine();
				
				// for (Nucleus nuc : hemiseg.vl4.nucs) {
					// writer.newLine();

					// String temp = (hemiseg.name + ",vl4," + nuc.id + ",");
					// for (String heading : headings) {
						// if (nuc.data.containsKey(heading)) temp += (nuc.data.get(heading) + ",");
						// else temp += ",";
					// }
					// writer.write(temp);
				// }
				// writer.newLine();
			// }
			
			// writer.close();
			// //IJ.log("specCount" + specCount);
			// //IJ.log("specCount2" + specCount2);
			// return true;
		// }
		// catch (FileNotFoundException e) {
			// /** deal with exception appropriately **/
			// IJ.log("FileNotFoundException in Experiment.exportNucData");
			// return false;
		// }
		// catch (IOException e) {
			// /** deal with exception appropriately **/
			// IJ.log("IOException in Experiment.exportNucData");
			// return false;
		// }
	// }
	
	
	
	public boolean exportNucData(String fileSuf, ArrayList<String> headings) {
		
		File outCsv = new File(path, name + "_" + fileSuf + ".csv");
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(outCsv));
			String labels = "Hemisegment,Cell,NucID,";
			
			for (String heading : headings) {
				labels += (heading + ",");
			}
			
			
			writer.write(labels+"\n");
			
			
			for (Cell c : cells) {
				for (Nucleus nuc : c.nucs) {
					String temp = c.hemiseg.name + ",vl"+c.vlNum + "," + nuc.id + ",";
					for (String heading : headings) {
						String heading2 = headingRename(heading);
						if (nuc.data.containsKey(heading2)) {
							MutableDouble val = nuc.data.get(heading2);
							if (val != null) {
								temp += (nuc.data.get(heading2));
							}
						}
						temp += ",";
					}
					writer.write(temp + "\n");
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
	
	
	public static String headingRename(String heading) {
		Hashtable<String,String> headingDict = new Hashtable<String,String>();
		headingDict.put("Thickness(minFeret)", "orthRoi - MinFeret");
		headingDict.put("Thickness(Height)", "orthRoi - Height");
		headingDict.put("Cross-sectional Area", "orthRoi - Area");
		// headingDict.put("orthRoi - minFeret", "Thickness(minFeret)");
		// headingDict.put("orthRoi - minFeret", "Thickness(minFeret)");
		// headingDict.put("orthRoi - minFeret", "Thickness(minFeret)");
		// headingDict.put("orthRoi - minFeret", "Thickness(minFeret)");
		
		if (headingDict.containsKey(heading)) return headingDict.get(heading);
		else return heading;

		
	}
	
	
	public void testOnOneNuc() {
		nucs.get(0).makeNucImps();
		nucs.get(0).countOrthPixels();
		nucs.get(0).yScaled();
		nucs.get(0).sumSlicesOrthStack();
	}
	
	public void runEverything() {
		makeNucImps();
		countNucOrthPixels();
		nucYScaled();
	}
	
	
	
	public void makeNucImps() {
		IJ.log("making nuc cross-sections...");
		for (Nucleus nuc : nucs) {
			nuc.makeNucImps();
		}
	}
	
	public void countNucOrthPixels() {
		IJ.log("counting pixels in nuc volume...");
		for (Nucleus nuc : nucs) {
			nuc.countOrthPixels();
		}
	}
	public void nucYScaled() {
		IJ.log("scaling nuc y coordinates to cell");
		for (Nucleus nuc : nucs) {
			nuc.yScaled();
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