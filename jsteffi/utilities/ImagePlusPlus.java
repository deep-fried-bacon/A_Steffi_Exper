package utilities;

import java.io.File;

import ij.*;


public class ImagePlusPlus {
	public File path;
	public String name;
	public ImagePlus imp;
	
	public ImagePlusPlus(File imp_path) {
		path = imp_path;
		name = imp_path.getName();
	}
	
	public ImagePlus getImp() {
		if (imp != null) {
			return imp;
		}
		else {
			return IJ.openImage(path.getPath());
		}
	}
}