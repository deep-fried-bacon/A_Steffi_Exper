//package A_Steffi_Exper;
import ij.plugin.PlugIn;
import ij.*;
import ij.io.*;

import java.util.ArrayList;
import java.io.File;

import jsteffi.*;
import jsteffi.utilities.*;





public class A_Steffi_Exper implements PlugIn{
	public static int inst_count = 0;
	//public static Exper exper
	
	public A_Steffi_Exper() {
		inst_count++;
		
	}
	public void run(String arg) {
		String py_path = "C:\\Users\\localuser\\Desktop\\Code Laboratory\\jsteffi\\py_testing.py";
		File pathtemp = new File(py_path);
		//Hemiseg h = new Hemiseg(pathtemp);
		//Cell c = new Cell(pathtemp);
		if(inst_count == 1) {
			Opener myOpener = new Opener();
			myOpener.open(py_path);	
		}
		Experiment exper = new Experiment();
		
		IJ.log("cool");
		IJ.log(""+inst_count);
	}
	public void close() {
		inst_count--;
	}
	
}