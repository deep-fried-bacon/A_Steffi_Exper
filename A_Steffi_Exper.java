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
			String[] experPaths = {
				"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\150729_w1118",
				"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\150910_Dm2-EGFP",
				"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\151021_Dm2-GFPRNAi",
				"C:\\Users\\localuser\\Desktop\\Code Laboratory\\Steffi\\Steffi NMJ datasets\\151216_Dm2-GFP"
			};

		//int[] whichOnes = {0,1,2,3};
		int[] whichOnes = {1};
		
		boolean nuc = true;
		boolean cell = false;
		
		for (int i = 0; i < whichOnes.length; i++) {
			File path = new File(experPaths[whichOnes[i]]);
			
			if (nuc) {
				String nucFileSuf = "y-area-thickness";
			
				String[] nucHeadings = {"Y","Y Scaled to Cell","Area","Thickness(minFeret)","Thickness(Height)", "vol pix count", "vol pix sum","Cross-sectional Area","orth vol sum","stack vol sum","cropped stack vol sum","cropped stack vol sum2"};
			
				Experiment e = Experiment.experConstructEverything(path, nucFileSuf, nucHeadings);
			}
			
			if (cell) {
				String cellFileSuf = "cell-vol-stuff";
				// String[] cellHeadings = {"Area","Nuc Total Area","Volume", "Volume 2","Nuc Total Volume","thickness mean"};
				String[] cellHeadings = {"Area","Volume 0", "Volume 1", "Volume 2","thickness mean 0", "thickness mean 1","Nuc Total Area","Nuc Total Volume"};
				
				Experiment e = new Experiment(path);
				// e.testOneCell();
				e.forEachCell();
				e.exportCellData(cellFileSuf,cellHeadings);
			}
			
			if (whichOnes.length > 2) {
				e.close();
				e = null;
				System.gc();
			}
		}
	}
	
	public void close() {
		inst_count--;
	}
}