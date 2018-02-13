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
	
	private ImagePlus stack = null;
	private ImagePlus orthStack = null;
	private ImagePlus orth = null;
	private ImagePlus orthThresh = null;
	private ImagePlus croppedOrthStack = null;
		
	public Roi orthRoi = null;
	
	public double chunkX = -1;
	public double chunkY = -1;
	public double chunkZ = -1;
	
	public double xCal = -1;
	public double yCal = -1;
	public double zCal = -1;

	
	public Nucleus (Cell cell, int id, Roi roi, Hashtable<String, MutableDouble> geoData) {
		if (cell == null || roi == null || geoData == null) {
			 throw new NullPointerException();
		}
		else {
			this.cell = cell;
			this.chunkZ = cell.hemiseg.sliceCount;
			
			this.xCal = cell.hemiseg.cal.pixelWidth;
			this.yCal = cell.hemiseg.cal.pixelHeight;
			this.zCal = cell.hemiseg.cal.pixelDepth;
			
			
			this.id = id;
			this.roi = roi;
			this.geoData = geoData;		
		}
	}
	
	public ImagePlus getStack() {
		return stack;
	}
	public boolean setStack(ImagePlus stack) {
		if (stack == null) {
			//IJ.log("null");
			return false;
		}
		if (!(stack.getNSlices() == chunkZ)) {
			//IJ.log("chunkZ = " +  chunkZ);
			//IJ.log("stack.getNChannels() = " +  stack.getNChannels());
			return false;
		}
		else {
			if (chunkX == -1) chunkX = stack.getWidth();
			else if (!(stack.getWidth() == chunkX)) {
				//IJ.log("ChunkX");
				return false;
			}
			
			if (chunkY == -1) chunkY = stack.getHeight();
			else if (!(stack.getWidth() == chunkY)) {
				//IJ.log("ChunkY");
				return false;
			}
			
			
			this.stack = stack;
			this.stack.setCalibration(cell.hemiseg.cal);
			
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
			this.orthStack.setCalibration(cell.hemiseg.cal.copy());
			
			this.orthStack.getCalibration().pixelWidth = yCal;
			this.orthStack.getCalibration().pixelHeight = zCal;
			this.orthStack.getCalibration().pixelDepth = xCal;
			
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
			this.orth.setCalibration(cell.hemiseg.cal.copy());
			
			this.orth.getCalibration().pixelWidth = yCal;
			this.orth.getCalibration().pixelHeight = zCal;
			this.orth.getCalibration().pixelDepth = xCal;
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
			
			this.orth.setCalibration(cell.hemiseg.cal.copy());
			
			this.orthThresh.getCalibration().pixelWidth = yCal;
			this.orthThresh.getCalibration().pixelHeight = zCal;
			this.orthThresh.getCalibration().pixelDepth = xCal;
			return true;
		}
	}
	
	public ImagePlus getCroppedOrthStack() {
		return croppedOrthStack;
	}
	public boolean setCroppedOrthStack(ImagePlus croppedOrthStack) {
		if (croppedOrthStack == null) return false;
		else {
			if (chunkX == -1) chunkX = croppedOrthStack.getNSlices();
			else if (!(croppedOrthStack.getNSlices() == chunkX)) {
				//IJ.log("nSlices = " + croppedOrthStack.getNSlices());
				//IJ.log("chunkX = " + chunkX);

				return false;
			}
			
			/* if (chunkY == -1) chunkY = croppedOrthStack.getWidth();
			else if (!(croppedOrthStack.getWidth() == chunkY)) {
				IJ.log("width = " + croppedOrthStack.getWidth());
				IJ.log("chunkY = " + chunkY);
				cropped
				return false;
			} */
			
			this.croppedOrthStack = croppedOrthStack;
			
			this.croppedOrthStack.setCalibration(cell.hemiseg.cal.copy());
			
			this.croppedOrthStack.getCalibration().pixelWidth = yCal;
			this.croppedOrthStack.getCalibration().pixelHeight = zCal;
			this.croppedOrthStack.getCalibration().pixelDepth = xCal;
			
			
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