package ivpl.synthesizer.filter.bit.biquad;

import static java.lang.Math.exp;
import static java.lang.Math.log1p;

public class ShiftedMultiPEQ {
	private final PEQ[] peq;
	private final double[] freq_adjustment;

	public ShiftedMultiPEQ(int oder, double width){
		peq = new PEQ[oder];
		for(int i=0;i<oder;i++){
			peq[i]=new PEQ();
		}
		freq_adjustment = new double[oder];
		double half_width=width*0.5;
		double under_log=log1p(-half_width);
		double top_log=log1p(half_width);
		double offset = (under_log + top_log) * 0.5;
		int o = oder-1;
		for(int a = 0;a<=o;a++){
			int b = o-a;
			freq_adjustment[a] = exp(((under_log*b+top_log*a)/o)-offset);
		}
	}

	public double next(double freq, double A, double bw, double in){
		A /= peq.length;
		bw *= peq.length;
		double x = in;
		for(int i = 0;i<peq.length;i++){
			x = peq[i].next(freq*freq_adjustment[i],A,bw,x);
		}
		//x = peq[0].next(freq,A,bw,peq[1].next(freq,A,bw,in));
		return x;
	}
}
