package ivpl.analyzer.safe;

class ArrayCopy {

	static double[] copyArray(double[] array){
		double[] newarray = new double[array.length];
		System.arraycopy(array,0,newarray,0,array.length);
		return newarray;
	}
}
