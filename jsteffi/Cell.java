package jsteffi;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;
import ij.process.*;
import ij.plugin.*;


import java.util.*;
import java.io.*;
import java.time.LocalDate;

import jsteffi.utilities.*;

public class Cell {
	public Hemisegment hemiseg;
	public int vlNum;
	
	public File roiPath;
	public Roi roi;
	
	public ArrayList<Double> roiX = new ArrayList<Double>(0);
	public ArrayList<Double> roiY = new ArrayList<Double>(0);
	
	public boolean nucsLoaded = false;

	public ArrayList<Nucleus> nucs = new ArrayList<Nucleus>();
	public int nucCount;
	
	/*** P stands for Pointers 
		 the idea is that the data is "stored" in the Nucleus objects 
		 and the objects in cell are simply pointing to them ***/
		 
	public ArrayList<Roi> nucRoiP;
	
	/*** nucGeoDataP.get("<Column Heading>");
		 nucGeoDataP.get("<Column Heading>").get(<NucId>); ***/
	public Hashtable<String,ArrayList<MutableDouble>> nucGeoDataP;
	
	
	
	
	
	public ImagePlus cellHyp = null;
	int xLength;
	int yLength;
		
	int channelCount;
	
	int zLength;		// slices
	
	
	public Cell(Hemisegment hemiseg, File roiPath, int vlNum, Calibration cal) {
		this.hemiseg = hemiseg;
		this.vlNum = vlNum;
		
		this.roiPath = roiPath;
		roi = openRoiCsv(roiPath, cal);
	}
		
		
	public Roi openRoiCsv(File roiCsvPath, Calibration cal) {
		try (Scanner sc = new Scanner(roiCsvPath)) {
			sc.useDelimiter(",|\\s");
			
			while (sc.hasNextDouble()) {
				double temp = sc.nextDouble();
				//float aksjdf = temp;
				roiX.add(cal.getRawX(temp));
				
				if (sc.hasNextDouble()) {
					temp = sc.nextDouble();
					roiY.add(cal.getRawY(temp));
				}
				else {
					/*** exception ***/
					IJ.log("shit, in Cell.openRoiCsv, got x value with no matching y value");
				}
			}
			float[] xF = ArrLisDouToArrFlo(roiX);
			float[] yF = ArrLisDouToArrFlo(roiY);
			
			return new PolygonRoi(xF,yF,2);
		}
		catch (FileNotFoundException e) {
			/*** exception ***/
			IJ.log("in Cell.openRoiCsv exception: " + e);
			return null;
		}
	}
	
	
		
/* 	public Hashtable<String,double[]> readRT(ResultsTable rt) {
		String[] headings = rt.getHeadings();
		Hashtable<String,double[]> data = new Hashtable<String,double[]>(headings.length);
		
		for (int i = 0; i < headings.length; i++) {
			data.put(headings[i],rt.getColumnAsDoubles(rt.getColumnIndex(headings[i])));
		}
		return data;
		
	}
	 */	
		
	public boolean makeNucDataPointers() {
		if (nucsLoaded == false) return false;
		else {
			nucRoiP = new ArrayList<Roi>(nucCount);
			//headings = 
			nucGeoDataP = new Hashtable<String,ArrayList<MutableDouble>>(hemiseg.geoHeadings.length);
			for (String heading : hemiseg.geoHeadings) {
					nucGeoDataP.put(heading,new ArrayList<MutableDouble>(nucCount));
			}			
			for (int i = 0; i < nucCount; i++) {
				nucRoiP.add(i, nucs.get(i).roi);
				
				for (String heading : hemiseg.geoHeadings) {
					
					/*** nucs --> ArrayList<Nucleus>
						 .get(i) --> Nucleus
						 Nucleus.geoData --> Hashtable<String,MutableDouble>
						 geoData.get(Heading --> String) --> MutableDouble ***/
					MutableDouble val = nucs.get(i).geoData.get(heading);
					nucGeoDataP.get(heading).add(i,val);
				}				
			}
			return true;
		}
	}
	
	
	
	/** <edit when less tired> **/
	public void makeCellHyp() {
		//ImageProcessor ip = hyp.getProcessor();
		//ip.setRoi(roi);
		//cellHyp = new ImagePlus("cell + blah",ip.duplicate(
		//IJ.log("starting makeCellHyp");
		hemiseg.hyp.setRoi(roi);
		cellHyp = hemiseg.hyp.duplicate();
		hemiseg.hyp.deleteRoi();
		
		PolygonRoi tempRoi = new PolygonRoi(roi.getFloatPolygon(),2);
		cellHyp.setRoi(tempRoi);
		tempRoi.setLocation(0,0);
		
		ImagePlus temp = WindowManager.getTempCurrentImage();
		WindowManager.setTempCurrentImage(cellHyp);
		IJ.run("Clear Outside", "stack");
		WindowManager.setTempCurrentImage(temp);
	
		
		xLength = cellHyp.getDimensions()[0];
		yLength = cellHyp.getDimensions()[1];
		
		channelCount = cellHyp.getDimensions()[2];
		zLength = cellHyp.getDimensions()[3];
		
		//IJ.log("" + cellHyp.getStack());
		
		
		
	}
	
	/** for the mo. just does nuclei channel and cell channel**/
	public void makeCellXOrthView() {
		//IJ.log("starting makeCellXOrthView"); 
		if (cellHyp == null) {
			makeCellHyp();
		}
		//IJ.log("" + cellHyp);

		int[] channels = new int[2];
		channels[0] = hemiseg.exper.channels.get("Cell");
		channels[1] = hemiseg.exper.channels.get("Nuclei");
		
		IJ.log("xLength="+xLength+",yLength="+yLength+",zLength="+zLength);
		IJ.log("channels="+channels);
		ImageStack[] outStacks = new ImageStack[2];
		//outStacks[0] = new ImageStack();
		//outStacks[1] = new ImageStack();
		//Arrays.fill(outImps,new ImagePlus());
		ImageStack impStack = cellHyp.getStack();
		int index = 0;
		for (int channel : channels) {
			ImageStack sliceHolder = new ImageStack(yLength,zLength);
			for (int x = 0; x < xLength; x++) {
				byte[] xSlice = new byte[yLength*zLength];
				for (int z = 0; z < zLength; z++) {
					ImageProcessor ip = impStack.getProcessor(cellHyp.getStackIndex(channel,z+1,1));			
					byte[] pixels = (byte[])ip.getPixels();
					
					//for (int y = 0; y < yLength; y++) {
					int y = 0;
					while ((y*xLength + x) < pixels.length && (z*yLength + y) < xSlice.length) {
						xSlice[z*yLength + y] = pixels[y*xLength + x];
						y++;

					}
					//}
				
				}
				ByteProcessor bp = new ByteProcessor(yLength,zLength,xSlice);
				sliceHolder.addSlice(bp);
			}
			//ImagePlus temp = new ImagePlus();
			//temp.setStack(sliceHolder);
			IJ.log("sliceHolder.size()="+sliceHolder.size());
			outStacks[index] = (sliceHolder.duplicate());
			index++;
			}
		//IJ.log("outStacks"
		IJ.log("outStacks[0].size()="+outStacks[0].size());
		ImageStack outStack = RGBStackMerge.mergeStacks(outStacks[0],outStacks[1],null,true);
		ImagePlus temp = new ImagePlus();
		temp.setStack(outStack);
		temp.show();
	
	}
	
	/** xToStack and yToStack act as booleans with 
		0 --> false
		1 --> true
		other --> error **/  
	public ImagePlus[] orthView(ImagePlus imp, int xToStack, int yToStack) {
		if (!(xToStack == 0 || xToStack == 1)) {
			/*** exception ***/
			IJ.log("value passed to orthView for yxToStack must equal 0 or 1");
			return null;
		} 
		if (!(yToStack == 0 || yToStack == 1)) {
			/*** exception ***/
			IJ.log("value passed to orthView for yToStack must equal 0 or 1");
			return null;
		} 
		ImageStack impStack = imp.getStack();
		
		
		
		//int outImpCount = //((if(xStack){1}else{0})*channelCount + (if(xStack){1}else{0})*channelCount;
		
		ImagePlus[] outImps = new ImagePlus[xToStack*channelCount + yToStack*channelCount];
		Arrays.fill(outImps,new ImagePlus());
		

		if (xToStack == 1) {
			for (int channel = 1; channel <= channelCount; channel++) {
				ImageStack sliceHolder = new ImageStack(yLength,zLength);
				for (int x = 0; x < xLength; x++) {
					byte[] xSlice = new byte[yLength*zLength];
					for (int z = 0; z < zLength; z++) {
						ImageProcessor ip = impStack.getProcessor(imp.getStackIndex(channel,z+1,1));			
						byte[] pixels = (byte[])ip.getPixels();
						
						//for (int y = 0; y < yLength; y++) {
						int y = 0;
						while ((y*xLength + x) < pixels.length) {
							y++;
							xSlice[z*xLength + y] = pixels[y*xLength + x];
						}
						//}
					
					}
					ByteProcessor bp = new ByteProcessor(yLength,zLength,xSlice);
					sliceHolder.addSlice(bp);
				}
				ImagePlus temp = new ImagePlus();
				temp.setStack(sliceHolder);
				outImps[channel] = (temp);
			}
			
			
		}
		 
		
		if (yToStack == 1) {
			
		}
		return outImps;
	}
	
	
	/** </edit when less tired> **/

	
	public boolean makeNucIntensData() {
		for (Nucleus nuc : nucs) {
			
		}
		return false;
	}
	
	
	
	
	/*** ArrayList<double> to float[] ***/
	public float[] ArrLisDouToArrFlo(ArrayList<Double> arrLis) {
		float[] f = new float[arrLis.size()];
		for (int i = 0; i < arrLis.size(); i++) {
			f[i] = arrLis.get(i).floatValue();
		}
		return f;
	}
}



