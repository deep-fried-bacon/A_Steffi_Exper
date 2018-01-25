package jsteffi.utilities;

import java.io.File;

import ij.*;


public class ImagePlusPlus {
	
	
	

	public File path;
	public String name;
	private ImagePlus imp;
	
	public ImagePlusPlus(File imp_path) {
		//probs should add some sort of exception throw if file isn't an image
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