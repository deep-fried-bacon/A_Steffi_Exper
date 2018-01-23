


public class Test {
	public static void main(String[] args) {
		TestClass tc = new TestClass();

		TestClass.Two w = tc.new Two();
		System.out.println(w.a);
	}
}