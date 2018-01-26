package jsteffi;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.filter.*;

import java.util.*;


public class Nucleus {
	public Roi roi;
	public Hashtable<String, Double> geoData;
	public Hashtable<String, Double[]> intensData;
	
	public Nucleus (Roi roi, Hashtable<String, Double> geoData) {
		this.roi = roi;
		this.geoData = geoData;
	}
}