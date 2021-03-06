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
	
	
	public static Experiment experConstructEverything(File path, String outFileSuf, String[] headings) {
		Experiment exper = new Experiment(path);
		exper.runEverything();
		exper.exportNucData(outFileSuf, headings);
		return exper;
	}
	
	// public static Experiment mridulaConstructor(File path) {
		// insts.add(this);
		// this.path = path;
		// parseName();
		// loadChannels();
		// mridulaCreateHemisegs();
		
	// }
	
	public Experiment(File path) {
		insts.add(this);
		this.path = path;
		//experView = new ExperimentView(this);
		
		parseName(); // sets name, data, genotype

		loadChannels(); // sets channels 

		createHemisegs(); // sets hemisegs
		
		createIterables();
	}
	
	public Experiment(File path, int a) {
		insts.add(this);
		this.path = path;
		//experView = new ExperimentView(this);
		
		//parseName(); // sets name, data, genotype
		name = path.getName();
		loadChannels(); // sets channels 

		mridulaCreateHemisegs(); // sets hemiseg
		
		mridulaCreateIterables();
		
		
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
	
	public void mridulaCreateIterables() {
		cells = new ArrayList<Cell>();
		for (Hemisegment hemiseg : hemisegs) {
			for (Cell c : hemiseg.cells) {
				cells.add(c);
			}
			
		}
	}	
	
	public void close() {
		insts.remove(insts.indexOf(this));
		for (Nucleus nuc : nucs) {
			nuc = null;
		}
		for (Cell c : cells) {
			c = null;
		}
		for (Hemisegment hemiseg : hemisegs) {
			hemiseg = null;
		}
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
	
	public void mridulaCreateHemisegs() {
		hemisegFileList = new ArrayList<File>();
		hemisegs = new ArrayList<Hemisegment>();
		File[] subDirs = path.listFiles();
		
		for(int i = 0; i < subDirs.length; i++) {
			if (subDirs[i].getName().startsWith(name) && subDirs[i].isDirectory()) {
				hemisegFileList.add(subDirs[i]);
				hemisegs.add(new Hemisegment(this, subDirs[i], 1));
			}	
		}
	}
	
	
	public void mridulaForEachCell() {
		for (Cell c : cells) {
			c.mridulaDo();
		}
	}
	
	public void mridulaDataCsv() {
		File outCsv = new File(path, name + "_" + "erm" + ".csv");
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(outCsv));
			String temp = "";
			for (Cell c : cells) {
				temp = c.cellID();
				for (double val : c.data2.get("xCol")) {
					temp += "," + val;
				}
				writer.write(temp + "\n");
				
				temp = "Channel 1";
				for (double val : c.data2.get("Channel 1")) {
					temp += "," + val;
				}
				writer.write(temp + "\n");
				
				temp = "Channel 2";
				for (double val : c.data2.get("Channel 2")) {
					temp += "," + val;
				}
				writer.write(temp + "\n");
			}
				
				
			//String labels = "Hemisegment,Cell,";
			
			// for (int i = 0; i < headings.length; i++) {
				// labels += (headings[i] + ",");
			// }
			
			// writer.write(labels+"\n");
			
			// for (Cell c : cells) {
				// String temp = c.hemiseg.name + ",vl"+c.vlNum + ",";
				// for (int i = 0; i < headings.length; i++) {
					// String heading2 = headingRename(headings[i]);
					// if (c.data.containsKey(heading2)) {
						// MutableDouble val = c.data.get(heading2);
						// if (val != null) {
							// temp += (c.data.get(heading2));
						// }
					// }
					// temp += ",";
				// }
				// writer.write(temp + "\n");
					
			// }
			writer.close();
			// return true;
		}
		catch (FileNotFoundException e) {
			/** deal with exception appropriately **/
			IJ.log("FileNotFoundException in Experiment.exportCellData");
			return;
		}
		catch (IOException e) {
			/** deal with exception appropriately **/
			IJ.log("IOException in Experiment.exportCellData");
			return;
		}

	}
		
	
	
	
	
	
	
	public boolean exportCellData(String fileSuf, String[] headings) {
		File outCsv = new File(path, name + "_" + fileSuf + ".csv");
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(outCsv));
			String labels = "Hemisegment,Cell,";
			
			for (int i = 0; i < headings.length; i++) {
				labels += (headings[i] + ",");
			}
			
			writer.write(labels+"\n");
			
			for (Cell c : cells) {
				String temp = c.hemiseg.name + ",vl"+c.vlNum + ",";
				for (int i = 0; i < headings.length; i++) {
					String heading2 = headingRename(headings[i]);
					if (c.data.containsKey(heading2)) {
						MutableDouble val = c.data.get(heading2);
						if (val != null) {
							temp += (c.data.get(heading2));
						}
					}
					temp += ",";
				}
				writer.write(temp + "\n");
					
			}
			writer.close();
			return true;
		}
		catch (FileNotFoundException e) {
			/** deal with exception appropriately **/
			IJ.log("FileNotFoundException in Experiment.exportCellData");
			return false;
		}
		catch (IOException e) {
			/** deal with exception appropriately **/
			IJ.log("IOException in Experiment.exportCellData");
			return false;
		}

	}
		
	public boolean exportNucData(String fileSuf, String[] headings) {
		File outCsv = new File(path, name + "_" + fileSuf + ".csv");
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(outCsv));
			String labels = "Hemisegment,Cell,NucID,";
			
			for (int i = 0; i < headings.length; i++) {
				labels += (headings[i] + ",");
			}
			writer.write(labels+"\n");
			
			for (Cell c : cells) {
				for (Nucleus nuc : c.nucs) {
					String temp = c.hemiseg.name + ",vl"+c.vlNum + "," + nuc.id + ",";
					for (int i = 0; i < headings.length; i++) {
						String heading2 = headingRename(headings[i]);
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
	
	
	public void testOneNuc() {
		nucs.get(0).makeNucImps();
		nucs.get(0).countOrthPixels();
		nucs.get(0).yScaled();
		//nucs.get(0).sumSlicesOrthStack();
		//nucs.get(0).sumSlicesStack();
		nucs.get(0).allSliceSums();
	}
	
	
	public void testOneCell() {
		//cells.get(0).thickness();
		cells.get(0).volume2();
		
	}
	
	public void forEachCell() {
		makeNucImps();
		allSliceSums();
		
		for (Cell c : cells) {
			c.thickness();
			c.volume2();
			//c.makeTotalAV();	
		}	
	}
		
	public void runEverything() {
		makeNucImps();
		countNucOrthPixels();
		nucYScaled();
		allSliceSums();
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
	
	public void allSliceSums() {
		IJ.log("scaling nuc y coordinates to cell");
		for (Nucleus nuc : nucs) {
			nuc.allSliceSums();
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
		
		has = has.substring(0, has.length() - 2);
		doesntHave = doesntHave.substring(0, doesntHave.length() - 2);
		
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