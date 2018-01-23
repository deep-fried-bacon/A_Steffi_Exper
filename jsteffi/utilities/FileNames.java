package utilities;

import java.lang.reflect.*;

public interface FileNames {
	
	public static final String HYP = ".tif";
	public static final String NUC_BIN = "_Nuc-bin.tif";
	
	public static final String HOECHST = "_Hoe.tif";
	public static final String EU = "_EU.tif";

	
	//public static final String[] VL_CSV = {null, null, null, "_XY-VL3.csv", "_XY-VL4.csv"}; 
	
	public static final String VL_CSV3 = "_XY-VL3.csv";
	public static final String VL_CSV4 = "_XY-VL4.csv";
	
	
	public static void main(String[] args) {
		Field[] fields = FileNames.class.getDeclaredFields();
		for(Field field : fields) {
			System.out.println(field);
		}
	}
	

}