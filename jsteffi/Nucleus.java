package jsteffi;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.filter.*;

import java.util.*;

import jsteffi.utilities.*;


public class Nucleus {
	public int id;
	public Roi roi;
	
	public Hashtable<String, MutableDouble> geoData;
	public Hashtable<String, MutableDouble[]> intensData;
	
	public Hashtable<String, MutableDouble> data3D = null;
	
	public ImagePlus stack = null;
	public ImagePlus orthStack = null;
	public ImagePlus orth = null;
	public ImagePlus orthThresh = null;
	
	
	public Nucleus (int id, Roi roi, Hashtable<String, MutableDouble> geoData) {
		this.id = id;
		this.roi = roi;
		this.geoData = geoData;
		
		
	}
}