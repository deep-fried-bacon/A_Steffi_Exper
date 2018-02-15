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
	
	//public static boolean testing = true;

	
	
	
	public A_Steffi_Exper() {
		inst_count++;
		
	}
	
	public void run(String arg) {
		String py_path = "C:\\Users\\localuser\\Desktop\\Code Laboratory\\jsteffi\\py_testing.py";
		
		if(inst_count == 1) {
			Opener myOpener = new Opener();
			myOpener.open(py_path);	
		}
		

		String pathStr;
			String[] experPaths = {"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\150729_w1118",
									"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\150910_Dm2-EGFP",
									"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\151021_Dm2-GFPRNAi",
									"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\151216_Dm2-GFP"};

			
			
			//pathStr = experPaths[3];

		
		
		int[] whichOnes = {0,1,2,3};
		
		for (int i = 0; i < whichOnes.length; i++) {
				 
			//ArrayList<String> headings = new ArrayList<String>(Arrays.asList("Y","Area","Thickness(minFeret)","Thickness(Height)", "vol pix count", "vol pix sum","Y Scaled to Cell","Cross-sectional Area"));
			
			File path = new File(experPaths[whichOnes[i]]);
			String fileSuf = "y-area-thickness";
			
			String[] headings = {"Y","Y Scaled to Cell","Area","Thickness(minFeret)","Thickness(Height)", "vol pix count", "vol pix sum","Cross-sectional Area","orth vol sum","stack vol sum","cropped stack vol sum","cropped stack vol sum2"};
			
			Experiment e = Experiment.experConstructEverything(path, fileSuf, headings);
			e.close();
			e = null;
			System.gc();
			
			//Experiment e = new Experiment(path);
			//e.testOnOneNuc();
		}
		
		
		
	}
	
	public void close() {
		inst_count--;
	}
	
}