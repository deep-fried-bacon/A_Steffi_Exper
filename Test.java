import java.util.*;
import java.io.*;



public class Test {
	
	
	public static void main(String[] args) {
		double[] myList = {77256, 12246, 10447, 9048, 8502, 8009, 7933, 7510, 7394, 7507, 7227, 7402, 7189, 7149, 7088, 7052, 6871, 6632, 6570, 6395, 6070, 5917, 5475, 5463, 5125, 5041, 4499, 4316, 4096, 3626, 3586, 3398, 3176, 2881, 2782, 2642, 2495, 2348, 2224, 2167, 2007, 1882, 1810, 1667, 1681, 1606, 1523, 1477, 1406, 1314, 1265, 1166, 1242, 1150, 1132, 995, 944, 928, 918, 846, 840, 831, 768, 720, 719, 730, 632, 641, 593, 552, 541, 502, 499, 477, 466, 427, 395, 362, 322, 314, 312, 269, 229, 229, 226, 198, 206, 165, 171, 141, 143, 121, 93, 126, 100, 70, 85, 56, 65, 40, 39, 40, 36, 43, 22, 23, 16, 17, 11, 18, 10, 9, 5, 4, 4, 10, 1, 2, 6, 4, 3, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		double[] myList2 = {0, 0, 1690, 1423, 1172, 1102, 1012, 901, 915, 867, 847, 810, 835, 901, 863, 875, 911, 944, 894, 837, 886, 894, 963, 922, 960, 936, 1020, 985, 961, 1020, 1053, 1129, 1075, 1165, 1161, 1194, 1284, 1253, 1252, 1301, 1264, 1313, 1314, 1370, 1434, 1364, 1439, 1475, 1413, 1462, 1568, 1510, 1573, 1590, 1601, 1624, 1682, 1707, 1585, 1640, 1662, 1638, 1703, 1818, 1864, 1761, 1937, 1864, 1876, 1910, 1949, 2029, 2023, 1970, 2019, 2101, 2168, 2131, 2161, 2235, 2177, 2236, 2200, 2301, 2356, 2366, 2403, 2381, 2453, 2456, 2508, 2434, 2494, 2545, 2670, 2624, 2519, 2667, 2764, 2661, 2708, 2770, 2726, 2772, 2769, 2886, 2882, 2932, 2839, 2912, 2972, 2830, 2948, 2998, 2958, 2927, 2912, 2801, 2882, 3009, 2946, 2833, 2804, 2814, 2764, 2798, 2745, 2679, 2744, 2597, 2605, 2619, 2558, 2418, 2452, 2444, 2396, 2319, 2231, 2320, 2251, 2074, 2085, 2019, 1940, 1904, 1806, 1803, 1723, 1670, 1583, 1544, 1560, 1460, 1411, 1315, 1303, 1293, 1173, 1168, 1127, 1057, 1006, 997, 924, 850, 827, 794, 783, 732, 694, 608, 589, 600, 577, 514, 484, 485, 440, 473, 428, 368, 362, 365, 288, 290, 264, 258, 268, 184, 196, 199, 177, 173, 142, 129, 142, 115, 107, 123, 99, 81, 65, 73, 67, 54, 62, 55, 43, 45, 38, 44, 28, 39, 25, 21, 23, 21, 13, 10, 15, 9, 13, 15, 8, 4, 5, 4, 2, 4, 1, 2, 5, 2, 3, 1, 1, 3, 1, 0, 0, 1, 1, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
		
		
		
		
		double[] myListDeriv = createPseudoDeriv(myList2);
		String[] myWordDeriv = createWordDeriv(myListDeriv);
		
		
		
		
		
		
		int[] be = thing(myList2);
		System.out.println(be[0]);
		System.out.println(be[1]);
		
		for (int i = be[1]-3; i <= be[1]+3; i++) {
			System.out.print(myList2[i]+", ");
		}
		//System.out.println(myListDeriv);
		/* System.out.println(myList2[0]);
		for (int i = 0 ; i < myWordDeriv.length; i++) {
			String beforeTemp = "     ";
			String temp = myWordDeriv[i];
			if (temp.equals("up")) {
				beforeTemp = "     " + "  " + "    ";
			} 
			else if (temp.equals("down")) {
				beforeTemp = "     " + "  ";
			}
			else if (temp.equals("top")) {
				beforeTemp = "     " + "  " + "   " + "  ";
			}
			System.out.println(beforeTemp + temp);
			System.out.println(myList2[i+1]);
		} */
		// char[] termp = new char[10];
		// Arrays.fill(termp,' '	);
		// System.out.println(new String(termp)+"asdf");
		// for (double num : myListDeriv) 
	}
	
	public static int[] thing (double[] inputList) {
		int begin = 0;
		double i = inputList[begin];
		while(i < 20) {
			begin++;
			i += inputList[begin];
		}
		
		int end = inputList.length - 1;
		i = inputList[end];
		
		while (i < 20) {
			end--;
			i += inputList[end];
		}
		int[] be = {begin,end};
		return be;
	}
	
	
	public static String[] createWordDeriv(double[] pseudoDeriv) {
		String[] wordDeriv = new String[pseudoDeriv.length];
		
		int t = 0;
		while (pseudoDeriv[t] == 0) {
			t++;
			wordDeriv[t] = "needToGoBack";
		}
		
		for(int i = t; i < pseudoDeriv.length; i++) {
			if (pseudoDeriv[i] > 0) {
				wordDeriv[i] = "up";
				
			}
			else if (pseudoDeriv[i] < 0) {
				wordDeriv[i] = "down";

			}
			else if (pseudoDeriv[i] == 0) {
				String before = wordDeriv[i-1];
				if (before.equals("up") || before.equals("top")) {
					wordDeriv[i] = "top";
				}
				else if (before.equals("down") || before.equals("bot")) {
					wordDeriv[i] = "bot";
				}
				else {
					/** exception **/ 
					System.out.println("Houston we have a problem");
				}
			}
			else {
				/** exception **/
				System.out.println("fuck");
			}
			
		}
		t--;
		
		if (t >= 0) {
			if (wordDeriv[t + 1].equals("up")) {
				for (int u = t; u >= 0; u--) {
					wordDeriv[u] = "bot";
					
				}
			}
			else if (wordDeriv[t +1].equals("down")) {
				for (int u = t; u >= 0; u--) {
					wordDeriv[u] = "top";
					
				}
			}
			else {
				/** exception **/
				System.out.println("uh-oh");
			}
		}
		
		
		return wordDeriv;
	}
	
	
	public static double[] createPseudoDeriv(double[] inputList) {
		double[] outputList = new double[inputList.length - 1];
		for(int i = 0; i < outputList.length; i++) {
			outputList[i] = inputList[i+1] - inputList[i];
			
		}
		return outputList;
	}
	
	
	
	
}

