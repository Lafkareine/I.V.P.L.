package ivpl;

import static java.lang.Math.*;

public class Unit {

	private static double log2p12 = log(2)/12;

	public static double HzToNN(double Hz){
		return log(Hz/440)/ log2p12 +69;
	}

	public static double NNToHz(double NN){
		return exp((NN-69)* log2p12) * 440;
	}

	public static double PPSToHz(double pps, float sample_rate){
		return pps * sample_rate;
	}

	public static double NNToPPS(double NN, float sample_rate){
		return NNToHz(NN) / sample_rate;
	}

	public static double PPSToNN(double pps, float sample_rate){
		return HzToNN(pps * sample_rate);
	}

	public static double HzToPPS(double Hz, float sample_rate){
		return Hz / sample_rate;
	}

	public static double DBToLiner(double dB){
		return pow(dB/10.0,10);
	}

	public static double LinerToDB(double liner_magnification){
		return log10(liner_magnification)*10;
	}
}
