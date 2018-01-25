import java.util.*;
import java.io.*;


public class Test {
	public static void main(String[] args) throws FileNotFoundException  {
		String s = "C:\\Amelia\\A_Steffi_Exper\\150729_w1118_L6-R3_XY-VL3.csv";
		File f = new File(s);
		Scanner sc = new Scanner(f);
		sc.useDelimiter(",|\\s");
		double a;
		String b;
		int count = 0;
		while (sc.hasNextDouble()) {
			//a = sc.nextDouble();
			//if (sc.hasNextDouble()) {
				a = sc.nextDouble();
				System.out.println(a);
			//}
			// } else {
				// count++;
				// b = sc.next();
				// System.out.print(b);
			// }
		
		}
		System.out.println("   "+count);
	}
}


