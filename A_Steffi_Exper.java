//package A_Steffi_Exper;
import ij.plugin.PlugIn;
import ij.*;
import ij.io.*;

import java.util.*;
import java.io.File;

import jsteffi.*;
import jsteffi.utilities.*;





public class A_Steffi_Exper implements PlugIn{
	public static int inst_count = 0;
	//public static Exper exper;
	
	public static boolean testing = true;

	
	
	
	public A_Steffi_Exper() {
		inst_count++;
		
	}
	
	public void run(String arg) {
		String py_path = "C:\\Users\\localuser\\Desktop\\Code Laboratory\\jsteffi\\py_testing.py";
		
		

		String pathStr;
		if (testing) {
			String[] experPaths = {"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\150729_w1118",
									"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\150910_Dm2-EGFP",
									"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\151021_Dm2-GFPRNAi",
									"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\151216_Dm2-GFP"};

			
			
			pathStr = experPaths[0];

		}
		
		else {
			DirectoryChooser dc = new DirectoryChooser("Choose folder containing hemisegment folders.");
			pathStr = dc.getDirectory();
		}
		
		
		File path = new File(pathStr);
		Experiment exper = new Experiment(path);

	
	
	
		//IJ.log("py_path");
		if(inst_count == 1) {
			Opener myOpener = new Opener();
			myOpener.open(py_path);	
		}
		
		
			
		//exper.hemisegs.get(0).vl4.orthMsrments();
		
		for (Hemisegment hemiseg : exper.hemisegs) {
			hemiseg.vl3.orthMsrments();
			hemiseg.vl4.orthMsrments();
			
			hemiseg.vl3.countNucPixels();
			hemiseg.vl4.countNucPixels();
			
			hemiseg.vl3.nucsYScaled();
			hemiseg.vl4.nucsYScaled();
		} 
		
		ArrayList<String> geoHeadings = new ArrayList<String>(Arrays.asList("Y","Area","Width","Height"));
		ArrayList<String> data3DHeadings = new ArrayList<String>(Arrays.asList("Thickness from MinFeret", "Thickness from Height",
													"Orth Area", "vol pix count", "vol pix sum","Y Scaled to Cell","Orth Width"));
		exper.exportNucData("y-area-thickness", geoHeadings, data3DHeadings);
		
		
		
			//IJ.log("specCount = " + exper.specCount);
	}
	
	public void close() {
		inst_count--;
	}
	
}