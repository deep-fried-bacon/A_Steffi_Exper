package utilities;
//package ij.measure;

public interface Mtypes {
	
	public static final int AREA=0, MEAN=1, STD_DEV=2, MODE=3, MIN=4, MAX=5,
	X_CENTROID=6, Y_CENTROID=7, X_CENTER_OF_MASS=8, Y_CENTER_OF_MASS=9,
	PERIMETER=10, ROI_X=11, ROI_Y=12, ROI_WIDTH=13, ROI_HEIGHT=14,
	MAJOR=15, MINOR=16, ANGLE=17, CIRCULARITY=18, FERET=19, 
	INTEGRATED_DENSITY=20, MEDIAN=21, SKEWNESS=22, KURTOSIS=23, 
	AREA_FRACTION=24, RAW_INTEGRATED_DENSITY=25, CHANNEL=26, SLICE=27, FRAME=28, 
	FERET_X=29, FERET_Y=30, FERET_ANGLE=31, MIN_FERET=32, ASPECT_RATIO=33,
	ROUNDNESS=34, SOLIDITY=35, MIN_THRESHOLD=36, MAX_THRESHOLD=37, LAST_HEADING=37;
	
	
    public static final int[] BINARY_M = {1, 2 ,4, 8, 16,
		32, 64, 128, 256, 512, 1024, 2048, 4096, 8192,
		8192, 16384, 0x8000, 0x10000, 0x20000, 0x40000, 0x80000, 
		0x100000, 0x100000, 0x200000, 0x400000, 0x800000};
        
		
	public static final String[] STRING_M = {"Area",
		"Mean","StdDev","Mode","Min","Max", "X","Y",
		"XM","YM","Perim.","BX","BY","Width","Height",
		"Major","Minor","Angle","Circ.","Feret", 
		"IntDen","Median","Skew","Kurt","%Area",
		"RawIntDen","Ch", "Slice","Frame","FeretX",
		"FeretY","FeretAngle","MinFeret","AR","Round",
		"Solidity","MinThr","MaxThr"};
}

//gi={'Mean': "intens", 'IntDen': "intens", 'BY': "geo", 'BX': "geo", 'FeretAngle': "geo", 'FeretY': "geo", 'StdDev': "intens", 'RawIntDen': "intens", 'Median': "intens", 'Major': "geo", 'FeretX': "geo", 'Min': "intens", 'Round': "geo", 'Angle': "geo", 'XM': "geo", 'Kurt': "intens", 'YM': "geo", 'Label': "geo", 'Feret': "geo", 'Area': "geo", 'Y': "geo", 'Solidity': "geo", 'X': "geo", 'AR': "geo", 'Height': "geo", 'MinFeret': "geo", 'Width': "geo", 'Minor': "geo", 'Max': "geo", 'Mode': "geo", 'Skew': "intens", 'Perim.': "geo", '%Area': "intens"}


	
		 
