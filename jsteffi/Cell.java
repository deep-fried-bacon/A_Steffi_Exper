package jsteffi;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;
import ij.process.*;
import ij.plugin.*;
import ij.plugin.filter.*;

import fiji.threshold.*;


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
	
	
	/** 
		P stands for Pointers 
		the idea is that the data is "stored" in the Nucleus objects 
		and the objects in cell are simply pointing to them 
	**/
	public ArrayList<Roi> nucRoiP;
	
	/*** nucGeoDataP.get("<Column Heading>");
		 nucGeoDataP.get("<Column Heading>").get(<NucId>); ***/
	public Hashtable<String,ArrayList<MutableDouble>> nucGeoDataP;
	public Hashtable<String,ArrayList<MutableDouble>> nucData3DP;
	
	
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
		
	public boolean makeNucDataPointers() {
		if (nucsLoaded == false) return false;
		else {
			nucRoiP = new ArrayList<Roi>(nucCount);
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
	
	public boolean makeNucData3DPointers() {
		if (nucsLoaded == false) return false;
		else if (nucs.get(0).data3D == null) return false;
		else {
			Set<String> keys = nucs.get(0).data3D.keySet();
			nucData3DP = new Hashtable<String,ArrayList<MutableDouble>>(keys.size());

			for (String key : keys) {
				nucData3DP.put(key,new ArrayList<MutableDouble>(nucCount));
				
			}
			for (int i = 0; i < nucCount; i++) {
				for (String key : keys) {
					MutableDouble val = nucs.get(i).data3D.get(key);
					nucData3DP.get(key).add(i, val);
				}
			}
		}
		return true;
	}
	
	
	//public static
	
	
	/** <edit when less tired> **/
	public void makeCellHyp() {
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
	}
	
	/** def got to rename **/
	public void erm() {
		int specCount = 0;
		
		/** requires cellHyp **/
		Duplicator d = new Duplicator();
		int nucChan = hemiseg.exper.channels.get("Nuclei");
			
		/** TEMPORARILY **/
		try {
			ImagePlus nucStack = d.run(hemiseg.hyp, nucChan, nucChan, 1, hemiseg.hyp.getNSlices(), 1, 1);		
			ResultsTable rt = new ResultsTable();
				
			for (int i = 0; i < nucs.size(); i++) {
				nucStack.setRoi(nucRoiP.get(i));
				nucs.get(i).stack = nucStack.duplicate();	//single nucleus stack
				
				ImagePlus[] temp = orthView(nucs.get(i).stack,1,0);
				nucs.get(i).orthStack = temp[0];
				
				Auto_Threshold at = new Auto_Threshold();
				Object[] temp2 = at.exec(nucs.get(i).orth, "IsoData", false, false, true, false, false, false);
				nucs.get(i).orthThresh = (ImagePlus)temp2[1];
				
				nucs.get(i).orthThresh.setCalibration(hemiseg.cal);
				nucs.get(i).orthThresh.setOverlay(null);

				rt.reset();
				
				int msr = Measurements.FERET + Measurements.AREA + Measurements.CENTROID;
				ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_OVERLAY_OUTLINES, msr, rt, 0, Double.POSITIVE_INFINITY);
				
				pa.analyze(nucs.get(i).orthThresh);
				Overlay nucRoisH = nucs.get(i).orthThresh.getOverlay();
				nucs.get(i).orthThresh.setOverlay(null);

				/**gotta fix this, because this will happen, if for no other reason then vo nuclei**/
				/**also should consider despeckle**/
			
				double minFeret;
				double area;
				if (nucRoisH.size() > 1) {
					/** exception or something **/
					int count = 0;
					int index = -1;
					for (int j = 0; j < nucRoisH.size(); j++) {
						if (nucRoisH.get(j).getStatistics().area > 75) {
							count++;
							index = j;	
						}
					}
					
					if (count == 1) {				
						minFeret = rt.getValueAsDouble(rt.getColumnIndex("MinFeret"),index);
						area = rt.getValueAsDouble(rt.getColumnIndex("Area"),index);
					} 
					else {
						IJ.log(hemiseg.name + " vl"+vlNum);

						for (int j = 0; j < nucRoisH.size(); j++) {
							IJ.log("    \t" + nucRoisH.get(j).getStatistics().area);
						}
						hemiseg.exper.specCount++;
						continue;
					}
				} 
				else if (nucRoisH.size() < 1) {
					nucs.get(i).orthThresh.show();
					IJ.log("fuck, no particles for nuc orthview");
					continue;
				}					
				else {
					minFeret = rt.getValueAsDouble(rt.getColumnIndex("MinFeret"),0);
					area = rt.getValueAsDouble(rt.getColumnIndex("Area"),0);
				}	

				if (nucs.get(i).data3D == null) {
					nucs.get(i).data3D = new Hashtable<String, MutableDouble>(1);
				}
				
				nucs.get(i).data3D.put("Thickness",new MutableDouble(minFeret));
				nucs.get(i).data3D.put("Thickness Area", new MutableDouble(area));;		
			}
		}
		catch (Exception e) {
			/** exception **/
			IJ.log("exception in Cell.erm()");
		}
	}
	
	/** for the mo. just does nuclei channel and cell channel**/
	public void makeCellXOrthView() {
		if (cellHyp == null) {
			makeCellHyp();
		}

		int[] channels = new int[2];
		channels[0] = hemiseg.exper.channels.get("Cell");
		channels[1] = hemiseg.exper.channels.get("Nuclei");
		
		ImageStack[] outStacks = new ImageStack[2];
		ImageStack impStack = cellHyp.getStack();
		int index = 0;
		
		for (int channel : channels) {
			ImageStack sliceHolder = new ImageStack(yLength,zLength);
				
				for (int x = 0; x < xLength; x++) {
					byte[] xSlice = new byte[yLength*zLength];
					
					for (int z = 0; z < zLength; z++) {
						ImageProcessor ip = impStack.getProcessor(cellHyp.getStackIndex(channel,z+1,1));			
						byte[] pixels = (byte[])ip.getPixels();
						int y = 0;
						while ((y*xLength + x) < pixels.length && (z*yLength + y) < xSlice.length) {
							xSlice[z*yLength + y] = pixels[y*xLength + x];
							y++;
						}					
					}
					ByteProcessor bp = new ByteProcessor(yLength,zLength,xSlice);
					sliceHolder.addSlice(bp);
				}
				
			outStacks[index] = (sliceHolder.duplicate());
			index++;
			}
			
		ImageStack outStack = RGBStackMerge.mergeStacks(outStacks[0],outStacks[1],null,true);
		ImagePlus temp = new ImagePlus();
		temp.setStack(outStack);
		temp.show();
	}
	
	/** xToStack and yToStack act as booleans with 
		0 --> false
		1 --> true
		other --> error **/  
	public static ImagePlus[] orthView(ImagePlus imp, int xToStack, int yToStack) {
		
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
		
		int staticXLength = imp.getDimensions()[0];
		int staticYLength = imp.getDimensions()[1];
		
		int staticChannelCount = imp.getDimensions()[2];
		int staticZLength = imp.getDimensions()[3];
		
		
		ImageStack[] outStacks = new ImageStack[staticChannelCount+1];
		if (xToStack == 1) { 
			
			for (int channel = 1; channel <= staticChannelCount; channel++) {
				ImageStack sliceHolder = new ImageStack(staticYLength,staticZLength);
				
				for (int x = 0; x < staticXLength; x++) {
					byte[] xSlice = new byte[staticYLength*staticZLength];
					
					for (int z = 0; z < staticZLength; z++) {
						ImageProcessor ip = impStack.getProcessor(imp.getStackIndex(channel,z+1,1));			
						byte[] pixels = (byte[])ip.getPixels();
						int y = 0;
						while ((y*staticXLength + x) < pixels.length && (z*staticYLength + y) < xSlice.length) {
							xSlice[z*staticYLength + y] = pixels[y*staticXLength + x];
							y++;
						}
					}
					ByteProcessor bp = new ByteProcessor(staticYLength,staticZLength,xSlice);
					sliceHolder.addSlice(bp);
				}
				outStacks[channel] = (sliceHolder.duplicate());
			}			
		}
		
		ImagePlus[] outImps = new ImagePlus[outStacks.length - 1];
		for (int i = 0; i < outImps.length; i++) {
			ImagePlus temp = new ImagePlus((""+(i+1)), outStacks[i+1]);
			outImps[i] = temp.duplicate();
		}
		 
		/** do the horizontal cross section **/
		/**
			if (yToStack == 1) {
				
			}
		**/
		return outImps;
	}
	/** </edit when less tired> **/

	
	public boolean countNucPixels() {
		if (nucs.get(0).stack == null) {
			return false;
		}
		for (int nucID = 0; nucID < nucCount; nucID++) {
			//if nucID
			if (nucs.get(nucID).stack != null) {
				
			}
		}
		
		
		
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
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



