package jsteffi;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.filter.*;

import java.util.*;

import jsteffi.utilities.*;


public class Nucleus {
	public Cell cell;
	public int id;
	public Roi roi;
	public Hashtable<String, MutableDouble> geoData;
	
	
	public Hashtable<String, MutableDouble[]> intensData;
		
	public Hashtable<String, MutableDouble> data3D = null;
	
	public ImagePlus stack = null;
	public ImagePlus orthStack = null;
	public ImagePlus orth = null;
	public ImagePlus orthThresh = null;
	
	public ImagePlus croppedOrthStack = null;
	
	
	
	public Roi orthRoi = null;
	
	public double chunkX = -1;
	public double chunkY = -1;
	public double chunkZ = -1;
	
	
	public Nucleus (Cell cell, int id, Roi roi, Hashtable<String, MutableDouble> geoData) {
		if (cell == null || roi == null || geoData == null) {
			//throw Exception;
			/** exception **/
		}
		else {
			this.cell = cell;
			this.chunkZ = cell.hemiseg.sliceCount;
			this.id = id;
			this.roi = roi;
			this.geoData = geoData;		
		}
	}
	
	
	
	
	public ImagePlus getStack() {
		return stack;
	}
	public boolean setStack(ImagePlus stack) {
		if (stack == null) return false;
		if (!(stack.getNChannels() == chunkZ)) return false;
		else {
			if (chunkX == -1) chunkX = stack.getWidth();
			else if (!(stack.getWidth() == chunkX)) return false;
			
			if (chunkY == -1) chunkY = stack.getHeight();
			else if (!(stack.getWidth() == chunkY)) return false;
			
			
			this.stack = stack;
			return true;
		}
	}
	
	public ImagePlus getOrthStack() {
		return orthStack;
	}
	public boolean setOrthStack(ImagePlus orthStack) {
		if (orthStack == null) return false;
		if (!(orthStack.getHeight() == chunkZ)) return false;
		else {
			if (chunkX == -1) chunkX = orthStack.getNSlices();
			else if (!(orthStack.getNSlices() == chunkX)) return false;
			
			if (chunkY == -1) chunkY = orthStack.getWidth();
			else if (!(orthStack.getWidth() == chunkY)) return false;
			
			
			this.orthStack = orthStack;
			return true;
		}
	}
	
	public ImagePlus getOrth() {
		return orth;
	}
	public boolean setOrth(ImagePlus orth) {
		if (orth == null) return false;
		if (!(orth.getHeight() == chunkZ)) return false;
		else {
			if (chunkY == -1) chunkY = orth.getWidth();
			else if (!(orth.getWidth() == chunkY)) return false;
			
			
			this.orth = orth;
			return true;
		}
	}
	
	public ImagePlus getOrthThresh() {
		return orthThresh;
	}
	public boolean setOrthThresh(ImagePlus orthThresh) {
		if (orthThresh == null) return false;
		if (!(orthThresh.getHeight() == chunkZ)) return false;
		else {
			if (chunkY == -1) chunkY = orthThresh.getWidth();
			else if (!(orthThresh.getWidth() == chunkY)) return false;
			
			
			this.orthThresh = orthThresh;
			return true;
		}
	}
	
	
	public ImagePlus getCroppedOrthStack() {
		return croppedOrthStack;
	}
	public boolean setCroppedOrthStack(ImagePlus croppedOrthStack) {
		if (croppedOrthStack == null) return false;
		//if (!(croppedOrthStack.getHeight() == chunkZ)) return false;
		else {
			if (chunkX == -1) chunkX = croppedOrthStack.getNSlices();
			else if (!(croppedOrthStack.getNSlices() == chunkX)) return false;
			
			if (chunkY == -1) chunkY = croppedOrthStack.getWidth();
			else if (!(croppedOrthStack.getWidth() == chunkY)) return false;
			
			
			this.croppedOrthStack = croppedOrthStack;
			return true;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String fullId() {
		return ("Hemisegment " + cell.hemiseg.name + " vl" + cell.vlNum + " Nuc " + id);
	}
	
	
	public String toString() {
		return ("jsteffi.Nucleus: " + fullId());
	}
	
	public String toStringLong() {
		
		String has = "Has: ";
		String doesntHave = "Doesn't Have: ";
		
		if (roi == null) doesntHave += "roi, ";
		else has += "roi, ";
		if (geoData == null) doesntHave += "geoData, ";
		else has += "geoData, ";
		
		
		if (intensData == null) doesntHave += "intensData, ";
		else has += "intensData, ";
		if (data3D == null) doesntHave += "data3D, ";
		else has += "data3D, ";
		
		if (stack == null) doesntHave += "stack, ";
		else has += "stack, ";
		if (orthStack == null) doesntHave += "orthStack, ";
		else has += "orthStack, ";
		
		if (orth == null) doesntHave += "orth, ";
		else has += "orth, ";
		if (orthThresh == null) doesntHave += "orthThresh, ";
		else has += "orthThresh, ";
		if (orthRoi == null) doesntHave += "orthRoi, ";
		else has += "orthRoi, ";
		
		if (chunkX == -1) doesntHave += "chunkX, ";
		else has += "chunkX, ";
		if (chunkY == -1) doesntHave += "chunkY, ";
		else has += "chunkY, ";
		if (chunkZ == -1) doesntHave += "chunkZ, ";
		else has += "chunkZ, ";
		
		
		has = has.substring(0, has.length() - 4);
		doesntHave = doesntHave.substring(0, doesntHave.length() - 4);
		
		return (this.toString()
				+ has + "\n"
				+ doesntHave);
	}
		
	
	public String fullSummary(boolean dataTables) {
		String temp = this.toString();
		temp += ("\nroi: " + roi);
		
		if (dataTables) {
			temp += ("\ngeoData: " + geoData);
			temp += ("\nintensData: " + intensData);
			temp += ("\ndata3D: " + data3D);
		}
		else {
			temp += ("\ngeoData: size = " + geoData.size());
			temp += ("\nintensData: size = " + intensData.size());
			temp += ("\ndata3D: size = " + data3D.size());
		}
		
		temp += ("\nstack: " + stack);
		temp += ("\northStack: " + orthStack);
		temp += ("\north: " + orth);
		temp += ("\northThresh: " + orthThresh);
		
		temp += ("\northRoi: " + orthRoi);
		
		
		temp += ("\nchunkX: " + chunkX + ", chunkY: " + chunkY + ", chunkZ: " + chunkZ);
		
		return temp;
	}
		
	
	
}