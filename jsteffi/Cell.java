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
import java.awt.*;

import jsteffi.utilities.*;

public class Cell {
	public Hemisegment hemiseg;
	public int vlNum = -1;
	
	public File roiPath;
	public Roi roi;
	
	public ArrayList<Double> roiX = new ArrayList<Double>(0);
	public ArrayList<Double> roiY = new ArrayList<Double>(0);

	//public 
	//public Hashtable<String,MutableDouble> geoData = new Hashtable<String,MutableDouble>();
	//Rectangle bounds =
	
	public boolean nucsLoaded = false;

	public ArrayList<Nucleus> nucs = new ArrayList<Nucleus>();
	public int nucCount = -1;
	
		
	public ImagePlus cellHyp = null;
	public ImagePlus cellOrthStack = null;
	int xLength;
	int yLength;
		
	int channelCount;
	
	int zLength;		// slices
	
	
	public Cell(Hemisegment hemiseg, File roiPath, int vlNum, Calibration cal) {
		this.hemiseg = hemiseg;
		this.vlNum = vlNum;
		
		this.roiPath = roiPath;
		roi = openRoiCsv(roiPath, cal);
		//Rectangle bounds
		
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
		
	public void makeCellHyp() {
		//hemiseg.hyp.setRoi(roi);
		//cellHyp = hemiseg.hyp.duplicate();
		//hemiseg.hyp.deleteRoi();
		
		cellHyp = cropStack(hemiseg.hyp, roi);
		
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
	

	// public void orthMsrments() {
		// // int specCount = 0;
		
		// /** requires cellHyp 
		// **/
		// Duplicator d = new Duplicator();
		// int nucChan = hemiseg.exper.channels.get("Nuclei");
			
		// /** TEMPORARILY
		// **/
		// // try {
			// ImagePlus nucStack;
			// if (nucChan > hemiseg.hyp.getNChannels()) {
				// IJ.log("nucChan issue in orthMsrments, cell: " + this);
				// nucStack = d.run(hemiseg.hyp, 1, 1, 1, hemiseg.hyp.getNSlices(), 1, 1);	

			// }
			// else {
				// nucStack = d.run(hemiseg.hyp, nucChan, nucChan, 1, hemiseg.hyp.getNSlices(), 1, 1);	
			// }
			
			// ResultsTable rt = new ResultsTable();
				
			// for (int i = 0; i < nucs.size(); i++) {
				// nucStack.setRoi(nucs.get(i).roi);
				// // nucs.get(i).stack = nucStack.duplicate();	//single nucleus stack
				// nucs.get(i).setStack(nucStack.duplicate());
				
				// ImagePlus[] temp = orthView(nucs.get(i).getStack(),1,0);
				// // nucs.get(i).orthStack = temp[0];
				// nucs.get(i).setOrthStack(temp[0]);
				
				// ZProjector zp = new ZProjector();
				// // nucs.get(i).orth = zp.run(nucs.get(i).orthStack,"max");
				// nucs.get(i).setOrth(zp.run(nucs.get(i).getOrthStack(),"max"));
				
				// // IJ.log(
				// // IJ.log("Before: Auto_Threshold at = new Auto_Threshold()");

				// Auto_Threshold at = new Auto_Threshold();
				
				// // IJ.log("After: Auto_Threshold at = new Auto_Threshold()");
				// // IJ.log("Before: Object[] temp2 = at.exec(nucs.get(i).orth, \"IsoData\", false, false, true, false, false, false)");
				
				// Object[] temp2 = at.exec(nucs.get(i).getOrth(), "IsoData", false, false, true, false, false, false);
				
				// // IJ.log("After: Object[] temp2 = at.exec(nucs.get(i).orth, \"IsoData\", false, false, true, false, false, false)");
				// // IJ.log("temp2 = " + temp2);
				// // IJ.log("temp2.length = " + temp2.length);
				// // IJ.log("temp2[0]" + temp2[0]);
				// // IJ.log("temp2[1]" + temp2[1]);
				// // nucs.get(i).orthThresh = (ImagePlus)temp2[1];
				// nucs.get(i).setOrthThresh((ImagePlus)temp2[1]);
				
				// // nucs.get(i).orthThresh.setCalibration(hemiseg.cal);
				// nucs.get(i).getOrthThresh().setOverlay(null);

				// rt.reset();
				
				// int msr = Measurements.FERET + Measurements.AREA + Measurements.CENTROID + Measurements.RECT;
				// ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_OVERLAY_OUTLINES, msr, rt, 0, Double.POSITIVE_INFINITY);
				
				// pa.analyze(nucs.get(i).getOrthThresh());
				// Overlay nucRoisH = nucs.get(i).getOrthThresh().getOverlay();
				// nucs.get(i).getOrthThresh().setOverlay(null);

				// /**gotta fix this, because this will happen, if for no other reason then vo nuclei
					// also should consider despeckle**/
			
				// // double minFeret;
				// // double area;
				// int index = 0;

				// if (nucRoisH.size() > 1) {
					// /** exception or something
					// **/
					// int count = 0;
					// for (int j = 0; j < nucRoisH.size(); j++) {
						// if (nucRoisH.get(j).getStatistics().area > 75) {
							// count++;
							// index = j;	
						// }
					// }
					
					
					
					
					// if (count > 1) {
						// IJ.log(hemiseg.name + " vl"+vlNum);

						// for (int j = 0; j < nucRoisH.size(); j++) {
							// IJ.log("    \t" + nucRoisH.get(j).getStatistics().area);
						// }
						// hemiseg.exper.specCount2++;
						// continue;
					// }
					
					// else if (count < 1) {
						// // nucs.get(i).orthThresh.show();
						// IJ.log("all particles too small");
						// for (int j = 0; j < nucRoisH.size(); j++) {
							// IJ.log("    \t" + nucRoisH.get(j).getStatistics().area);
						// }
						// continue;
					// }
					// /*
					// if (count == 1) {				
						// minFeret = rt.getValueAsDouble(rt.getColumnIndex("MinFeret"),index);
						// area = rt.getValueAsDouble(rt.getColumnIndex("Area"),index);
					// } 
					// else {
						// IJ.log(hemiseg.name + " vl"+vlNum);

						// for (int j = 0; j < nucRoisH.size(); j++) {
							// IJ.log("    \t" + nucRoisH.get(j).getStatistics().area);
						// }
						// hemiseg.exper.specCount++;
						// continue;
					// }
					// */
				// } 
				// else if (nucRoisH.size() < 1) {
					// nucs.get(i).getOrthThresh().show();
					// IJ.log("fuck, no particles for nuc orthview");
					// continue;
				// }					
				
				// hemiseg.exper.specCount++;

				// double minFeret = rt.getValueAsDouble(rt.getColumnIndex("MinFeret"),index);
				// double area = rt.getValueAsDouble(rt.getColumnIndex("Area"),index);
				// double height = rt.getValueAsDouble(rt.getColumnIndex("Height"), index);
				
				// // nucs.get(i).orthThresh.show();
				// // rt.show("i");
					
				// nucs.get(i).orthRoi = nucRoisH.get(index);


				// if (nucs.get(i).data3D == null) {
					// nucs.get(i).data3D = new Hashtable<String, MutableDouble>(1);
				// }
				
				// nucs.get(i).data3D.put("Thickness from MinFeret",new MutableDouble(minFeret));
				// nucs.get(i).data3D.put("Thickness from Height",new MutableDouble(height));
				// nucs.get(i).data3D.put("Orth Area", new MutableDouble(area));;	
				
			// }
		
		// // catch (Exception e) {
			// /** exception
 			// **/
			// // IJ.log("exception in Cell.orthMsrments()");
		
	// }
	
	
	
	public void orthMsrments() {

		Duplicator d = new Duplicator();
		int nucChan = hemiseg.exper.channels.get("Nuclei");
			
	
		ImagePlus nucChannelStack;
		if (nucChan > hemiseg.hyp.getNChannels()) {
			IJ.log("nucChan issue in orthMsrments, cell: " + this);
			nucChannelStack = d.run(hemiseg.hyp, 1, 1, 1, hemiseg.hyp.getNSlices(), 1, 1);	

		}
		else {
			nucChannelStack = d.run(hemiseg.hyp, nucChan, nucChan, 1, hemiseg.hyp.getNSlices(), 1, 1);	
		}
		
		ResultsTable rt = new ResultsTable();
		
		for (int nucID = 0; nucID < nucs.size(); nucID++) {
			Nucleus nuc = nucs.get(nucID);
			
			nuc.setStack(cropStack(nucChannelStack, nuc.roi));
			
			nuc.setOrthStack(verticalCrossSection(nuc.getStack(),null));
			
			/* z-Projection */
			ZProjector zp = new ZProjector();
			nuc.setOrth(zp.run(nuc.getOrthStack(),"max"));
			
			/* Auto Threshold */
			Auto_Threshold at = new Auto_Threshold();
			Object[] temp2 = at.exec(nuc.getOrth(), "IsoData", false, false, true, false, false, false);
			nuc.setOrthThresh((ImagePlus)temp2[1]);
			
			
			/* Particle Analyzer */
			nuc.getOrthThresh().setOverlay(null);
			rt.reset();
			int msr = Measurements.FERET + Measurements.AREA + Measurements.CENTROID + Measurements.RECT;
			ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_OVERLAY_OUTLINES, msr, rt, 0, Double.POSITIVE_INFINITY);
			pa.analyze(nuc.getOrthThresh());
			Overlay nucOrthRois = nuc.getOrthThresh().getOverlay();
			nuc.getOrthThresh().setOverlay(null);

			
			int index = 0;

			if (nucOrthRois.size() > 1) {
				/** exception or something
				**/
				
				int count = 0;
				for (int j = 0; j < nucOrthRois.size(); j++) {
					if (nucOrthRois.get(j).getStatistics().area > 75) {
						count++;
						index = j;	
					}
				}
				
				if (count > 1) {
					IJ.log(hemiseg.name + " vl"+vlNum);

					for (int j = 0; j < nucOrthRois.size(); j++) {
						IJ.log("    \t" + nucOrthRois.get(j).getStatistics().area);
					}
					hemiseg.exper.specCount2++;
					continue;
				}
				
				else if (count < 1) {
					// nuc.orthThresh.show();
					IJ.log("all particles too small");
					for (int j = 0; j < nucOrthRois.size(); j++) {
						IJ.log("    \t" + nucOrthRois.get(j).getStatistics().area);
					}
					continue;
				}
				/*
				if (count == 1) {				
					minFeret = rt.getValueAsDouble(rt.getColumnIndex("MinFeret"),index);
					area = rt.getValueAsDouble(rt.getColumnIndex("Area"),index);
				} 
				else {
					IJ.log(hemiseg.name + " vl"+vlNum);

					for (int j = 0; j < nucOrthRois.size(); j++) {
						IJ.log("    \t" + nucOrthRois.get(j).getStatistics().area);
					}
					hemiseg.exper.specCount++;
					continue;
				}
				*/
			} 
			else if (nucOrthRois.size() < 1) {
				nuc.getOrthThresh().show();
				IJ.log("fuck, no particles for nuc orthview");
				continue;
			}					
			
			hemiseg.exper.specCount++;

			double minFeret = rt.getValueAsDouble(rt.getColumnIndex("MinFeret"),index);
			double area = rt.getValueAsDouble(rt.getColumnIndex("Area"),index);
			double height = rt.getValueAsDouble(rt.getColumnIndex("Height"), index);
			double width = rt.getValueAsDouble(rt.getColumnIndex("Width"), index);

			
			// nuc.orthThresh.show();
			// rt.show("nucID");
				
			nuc.orthRoi = nucOrthRois.get(index);


			if (nuc.data3D == null) {
				nuc.data3D = new Hashtable<String, MutableDouble>(1);
			}
			
			nuc.data3D.put("Thickness from MinFeret",new MutableDouble(minFeret));
			nuc.data3D.put("Thickness from Height",new MutableDouble(height));
			nuc.data3D.put("Orth Width",new MutableDouble(width));

			nuc.data3D.put("Orth Area", new MutableDouble(area));;	
			
		}
	
		
	}
	
	
	
	
	
	
	// public void makeCellXOrthView() {
		// if (cellHyp == null) {
			// makeCellHyp();
		// }

		// int[] channels = new int[2];
		// channels[0] = hemiseg.exper.channels.get("Cell");
		// channels[1] = hemiseg.exper.channels.get("Nuclei");
		
		// ImageStack[] outStacks = new ImageStack[2];
		// ImageStack impStack = cellHyp.getStack();
		// int index = 0;
		
		// for (int channel : channels) {
			// ImageStack sliceHolder = new ImageStack(yLength,zLength);
				
				// for (int x = 0; x < xLength; x++) {
					// byte[] xSlice = new byte[yLength*zLength];
					
					// for (int z = 0; z < zLength; z++) {
						// ImageProcessor ip = impStack.getProcessor(cellHyp.getStackIndex(channel,z+1,1));			
						// byte[] pixels = (byte[])ip.getPixels();
						// int y = 0;
						// while ((y*xLength + x) < pixels.length && (z*yLength + y) < xSlice.length) {
							// xSlice[z*yLength + y] = pixels[y*xLength + x];
							// y++;
						// }					
					// }
					// ByteProcessor bp = new ByteProcessor(yLength,zLength,xSlice);
					// sliceHolder.addSlice(bp);
				// }
				
			// outStacks[index] = (sliceHolder.duplicate());
			// index++;
			// }
			
		// ImageStack outStack = RGBStackMerge.mergeStacks(outStacks[0],outStacks[1],null,true);
		// ImagePlus temp = new ImagePlus();
		// temp.setStack(outStack);
		// temp.show();
	// }
	
	 public void makeCellXOrthView() {
		if (cellHyp == null) {
			makeCellHyp();
		}
		
		// int[] channels

		// int[] channels = new int[2];
		
		// channels[0] = hemiseg.exper.channels.get("Cell");
		// channels[1] = hemiseg.exper.channels.get("Nuclei");
		
		
			
		//ImageStack outStack = RGBStackMerge.mergeStacks(outStacks[0],outStacks[1],null,true);
		//ImagePlus temp = new ImagePlus();
		//temp.setStack(outStack);
		//temp.show();
		this.cellOrthStack = verticalCrossSection(cellHyp, null);
	}
	
	
	
	
	public MutableDouble yScaled(MutableDouble num) {
		Rectangle bounds = roi.getBounds();
		double height = bounds.height;
		double y = bounds.y;
		
		double temp = num.get();
		temp = temp - y;
		double temp2 = temp/height;
		
		MutableDouble outNum = new MutableDouble(temp2);
		return outNum;
		
	}
	
	public MutableDouble yScaled(double num) {
		Rectangle bounds = roi.getBounds();
		double height = bounds.height;
		double y = bounds.y;
		
		//double temp = num.get();
		//temp = num - y;
		double temp = num/height;
		
		MutableDouble outNum = new MutableDouble(temp);
		return outNum;
		
	}
	
	public void nucsYScaled () {
		for (int nucID = 0; nucID < nucCount; nucID++) {
			Nucleus nuc = nucs.get(nucID);
			double inY = nuc.geoData.get("Y").get();
			double inY2 = hemiseg.cal.getRawY(inY);
			MutableDouble outY = nuc.cell.yScaled(inY2);
			nuc.data3D.put("Y Scaled to Cell",outY);
			
		}
	}
	
	/**
		if channels == null, uses all channels
		*/
	public static ImagePlus verticalCrossSection(ImagePlus imp, int[] channels) {
		
		if (imp == null) return null;
		if (channels == null) {
			channels = new int[imp.getNChannels()];
			for (int i = 0; i < channels.length; i++) {
				channels[i] = i+1;
			}
		}
		
		ImageStack impStack = imp.getStack();
		
		int xLength = imp.getDimensions()[0];
		int yLength = imp.getDimensions()[1];
		
		//int staticChannelCount = imp.getDimensions()[2];
		int zLength = imp.getDimensions()[3];
		
		
		//ImageStack[] outStacks = new ImageStack[channels.length];
		ImageStack outStack = new ImageStack(yLength, zLength, channels.length*xLength);
		ImagePlus outImp = new ImagePlus();
		//outImp.setStack(outStack);
		//for (int channel = 1; channel <= staticChannelCount; channel++) {
			
		for (int i = 0; i < channels.length; i++) {
			int channel = channels[i];
			//ImageStack sliceHolder = new ImageStack(yLength,zLength);
			
			for (int x = 0; x < xLength; x++) {
				byte[] xSlice = new byte[yLength*zLength];
				
				for (int z = 0; z < zLength; z++) {
					ImageProcessor ip = impStack.getProcessor(imp.getStackIndex(channel,z+1,1));			
					byte[] pixels = (byte[])ip.getPixels();
					int y = 0;
					while ((y*xLength + x) < pixels.length && (z*yLength + y) < xSlice.length) {
						xSlice[z*yLength + y] = pixels[y*xLength + x];
						y++;
					}
				}
				
				//ByteProcessor bp = new ByteProcessor(yLength,zLength,xSlice);
				//sliceHolder.addSlice(bp);
				//outStack.setPixels(xSlice,outImp.getStackIndex(channel+1,x+1,1));
				//outStack.setPixels(xSlice,outImp.getStackIndex(channel+1,x+1,1));
				outStack.setPixels(xSlice,x*channels.length+i+1);

				
			}
			//outStacks[i] = (sliceHolder.duplicate());
		}			
		
		
		//ImagePlus[] outImps = new ImagePlus[outStacks.length];
		// for (int i = 0; i < outImps.length; i++) {
			// ImagePlus temp = new ImagePlus((""+(i)), outStacks[i]);
			// outImps[i] = temp.duplicate();
		// }
		outImp.setStack(outStack);

		outImp.setDimensions(channels.length,xLength,1);

		return outImp;
	}	
	
	
	
	
	
	
	public static ImagePlus horizontalCrossSection(ImagePlus imp, int[] channels) {
		
		if (imp == null) return null;
		if (channels == null) {
			channels = new int[imp.getNSlices()];
			for (int i = 0; i < channels.length; i++) {
				channels[i] = i+1;
			}
		}
		
		ImageStack impStack = imp.getStack();
		
		int xLength = imp.getDimensions()[0];
		int yLength = imp.getDimensions()[1];
		
		int staticChannelCount = imp.getDimensions()[2];
		int zLength = imp.getDimensions()[3];
		
		
		//ImageStack[] outStacks = new ImageStack[channels.length];
		ImageStack outStack = new ImageStack(xLength, zLength, channels.length*yLength);
		ImagePlus outImp = new ImagePlus();
		outImp.setStack(outStack);
		outImp.setDimensions(channels.length,yLength,1);
		//for (int channel = 1; channel <= staticChannelCount; channel++) {
			
		for (int i = 0; i < channels.length; i++) {
			int channel = channels[i];
			//ImageStack sliceHolder = new ImageStack(yLength,zLength);
			
			for (int y = 0; y < yLength; y++) {
				byte[] ySlice = new byte[xLength*zLength];
				
				for (int z = 0; z < zLength; z++) {
					ImageProcessor ip = impStack.getProcessor(imp.getStackIndex(channel,z+1,1));			
					byte[] pixels = (byte[])ip.getPixels();
					int x = 0;
					//while ((x*yLength + x) < pixels.length && (z*xLength + x) < ySlice.length) {
					while ((y*xLength + x) < pixels.length && (z*xLength + x) < ySlice.length) {
						ySlice[z*xLength + x] = pixels[y*xLength + x];
						y++;
					}
				}
				//ByteProcessor bp = new ByteProcessor(yLength,zLength,ySlice);
				//sliceHolder.addSlice(bp);
				outStack.setPixels(ySlice,outImp.getStackIndex(channel+1,y+1,1));
			}
			//outStacks[i] = (sliceHolder.duplicate());
		}			
		
		
		//ImagePlus[] outImps = new ImagePlus[outStacks.length];
		// for (int i = 0; i < outImps.length; i++) {
			// ImagePlus temp = new ImagePlus((""+(i)), outStacks[i]);
			// outImps[i] = temp.duplicate();
		// }
		 
	
		return outImp;
	}	
	
	
	/**
		if channels == null, uses all channels
		*/
			
	public boolean countNucPixels() {
		if (nucs.get(0).getStack() == null) {
			return false;
		}
		for (int nucID = 0; nucID < nucCount; nucID++) {
			//if nucID
			//if (nucs.get(nucID).stack != null) {
				
			//
			Nucleus nuc = nucs.get(nucID);
			
			//nuc.orthStack.setRoi(nuc.orthRoi);
			//boolean boop = nuc.setCroppedOrthStack(nuc.orthStack.crop());
			//IJ.log("boop = " + boop);
			nuc.setCroppedOrthStack(cropStack(nuc.getOrthStack(), nuc.orthRoi));
			//nuc.orthStack.deleteRoi();
			int pixelCount = 0;
			int pixelSum = 0;

			
			for (int slice = 0; slice < nuc.getCroppedOrthStack().getNSlices(); slice++) {
				//IJ.log("slice " + slice);
				
				ImageProcessor ip = nuc.getCroppedOrthStack().getProcessor();
				
				int width = ip.getWidth();
				int height = ip.getHeight();	
				
				byte[] pix = (byte[])ip.getPixels();
				
				for (int i = 0; i < pix.length; i++) {
					int temp = pix[i]&0xff;
					if (temp > 0){
						pixelCount++;
						pixelSum += temp;
					}
					else if (temp < 0) {
						IJ.log("fuck");
					}
				}	
		
			}
			
			if (nuc.data3D == null) nuc.data3D = new Hashtable<String,MutableDouble>();
			nuc.data3D.put("vol pix count", new MutableDouble((double)pixelCount));
			nuc.data3D.put("vol pix sum", new MutableDouble((double)pixelSum));
				
	
			
			
		}
		
		
		
		return true;
	}
	

	public boolean countNucPixels2() {
		return false;
	}



	
	/*** ArrayList<double> to float[]
	***/
	public float[] ArrLisDouToArrFlo(ArrayList<Double> arrLis) {
		float[] f = new float[arrLis.size()];
		for (int i = 0; i < arrLis.size(); i++) {
			f[i] = arrLis.get(i).floatValue();
		}
		return f;
	}
	
	// returns a duplicate
	public static ImagePlus cropStack(ImagePlus imp, Roi r) {
		imp.setRoi(r);
		//IJ.log("imp.getNChannels
		ImagePlus outImp = imp.duplicate();
		imp.deleteRoi();
		//IJ.log
		return outImp;
	}
	
	public String toString() {
		return ("jsteffi.Cell: " + hemiseg.name + " vl" + vlNum);
	}
		
	public String toStringLong() {
		
		String has = "\nHas: ";
		String doesntHave = "\nDoesn't Have: ";
		
		if (hemiseg == null) doesntHave += "hemiseg, ";
		else has += "hemiseg, ";
		if (vlNum == -1) doesntHave += "vlNum, ";
		else has += "vlNum, ";
		
		if (roiPath == null) doesntHave += "roiPath, ";
		else has += "roiPath, ";
		if (roi == null) doesntHave += "roi, ";
		else has += "roi, ";
		
		if (roiX == null) doesntHave += "roiX, ";
		else has += "roiX, ";
		if (roiY == null) doesntHave += "roiY, ";
		else has += "roiY, ";
		
		//if (nucsLoaded == null) doesntHave += "nucsLoaded, ";
		//else has += "nucsLoaded, ";
		
		if (nucs == null) doesntHave += "nucs, ";
		else has += "nucs, ";
		if (nucCount == -1) doesntHave += "nucCount, ";
		else has += "nucCount, ";
		
		
		
		
		
		
		
		
		has = has.substring(0, has.length() - 4);
		doesntHave = doesntHave.substring(0, doesntHave.length() - 4);
		
		return (this.toString()
				+ has
				+ doesntHave);		
	}
	
	public String fullSummary() {
		String temp = this.toString();
		
		temp += ("\nhemiseg: " + hemiseg);
		temp += ("\nvlNum: " + vlNum);
		
		temp += ("\nroiPath: " + roiPath);
		temp += ("\nroi: " + roi);
		
		temp += ("\nroiX: " + roiX);
		temp += ("\nroiY: " + roiY);
		
		temp += ("\nnucsLoaded: " + nucsLoaded);
		
		temp += ("\nnucs: " + nucs);
		temp += ("\nnucCount: " + nucCount);
		
		
		
		
		return temp;
	}
}








