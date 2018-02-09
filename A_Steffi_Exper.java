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
	
	public A_Steffi_Exper() {
		inst_count++;
		
	}
	public void run(String arg) {
		String py_path = "C:\\Users\\localuser\\Desktop\\Code Laboratory\\jsteffi\\py_testing.py";
		
		
		Experiment exper = new Experiment();
		//IJ.log("py_path");
		if(inst_count == 1) {
			Opener myOpener = new Opener();
			myOpener.open(py_path);	
		}
		
		
		
		for (Hemisegment hemiseg : exper.hemisegs) {
			hemiseg.vl3.orthMsrments();
			hemiseg.vl4.orthMsrments();
			
			hemiseg.vl3.countNucPixels();
			hemiseg.vl4.countNucPixels();
		}
		
		ArrayList<String> geoHeadings = new ArrayList<String>(Arrays.asList("Y","Area"));
		ArrayList<String> data3DHeadings = new ArrayList<String>(Arrays.asList("Thickness from MinFeret", "Thickness from Height", "Orth Area", "vol pix count", "vol pix sum"));
		exper.exportNucData("y-area-thickness", geoHeadings, data3DHeadings);
		
		
		
			//IJ.log("specCount = " + exper.specCount);
	}
	public void close() {
		inst_count--;
	}
	
}