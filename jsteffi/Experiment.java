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
	
	public File path;
	
	public String name;
	public LocalDate date;
	public String genotype;
	
	public ExperimentView experView;
	
	public Hashtable<String, Integer> channels;
	
	public ArrayList<File> hemisegFileList;
	public ArrayList<Hemisegment> hemisegs;
	
	

	
	public Experiment() {
		insts.add(this);
		
		makePath();		// instantiates path
		experView = new ExperimentView(this);
		
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
		}
	}
	
	
	
	
	
	
	public void exportNucData(ArrayList<String> headings){
		//make default outputDir and outputFileName
		//exportNucData(headings, outputDir, outputFileName);
	}
	public void exportNucData(ArrayList<String> headings, File outputDir, String ouptutFileName){
		
	}
	
	
	
	
	
}