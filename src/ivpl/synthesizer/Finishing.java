package ivpl.synthesizer;

import static java.lang.Integer.min;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Finishing {
	public static double[] amplify(double[] in, double[] out, double magnification){
		for(int i = 0;i<in.length;i++){
			out[i] = in[i]*magnification;
		}
		return out;
	}

	public static double[] amplify(double[] inout, double magnification){
		return amplify(inout,inout,magnification);
	}

	public static double[] asAmplified(double[] in, double magnification){
		double[] out = new double[in.length];
		return amplify(in,out,magnification);
	}

	public static double[] normalize(double[] in, double[] out){
		double max = 0;
		for(int i = 0;i<in.length;i++){
			max = max(max,abs(in[i]));
		}
		if(Double.compare(max,0)!=0) {
			for (int i = 0; i < in.length; i++) {
				out[i] = in[i] / max;
			}
		}else {
			if(in != out) {
				System.arraycopy(in,0,out,0,in.length);
			}
		}
		return out;
	}

	public static double[] normalize(double[] inout){
		return normalize(inout,inout);
	}

	public static double[] asNormalized(double[] in) {
		double[] out = new double[in.length];
		return normalize(in, out);
	}
}
