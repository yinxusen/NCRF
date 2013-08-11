package utils;

import cern.colt.list.*;

/**
 * TODO list Fill in with all random function. Now fake random funtion.
 * 
 * @author yinxusen
 * 
 */
public class RandUtils {

	/**
	 * TODO Fill in with rand gamma
	 * 
	 * @param rr
	 * @return
	 */
	public static double RandGamma(double rr) {
		return 0.0;
	}

	public static double RandBeta(double aa, double bb) {
		aa = RandGamma(aa);
		bb = RandGamma(bb);
		return aa / (aa + bb);
	}

	public static DoubleArrayList RandDir(DoubleArrayList alpha,
			DoubleArrayList beta) {
		double sum = 0.0;
		for (int i = 0; i < beta.size(); i++) {
			beta.set(i, RandGamma(alpha.get(i)));
			sum += beta.get(i);
		}
		for (int i = 0; i < beta.size(); i++) {
			beta.set(i, beta.get(i) / sum);
		}
		return beta;
	}

	public static int RandNumTables(double alpha, int numdata) {
		int retNumTable = 0;
		for (int i = 0; i < numdata; i++) {
			if (Math.random() < alpha / (i + alpha))
				retNumTable++;
		}
		return retNumTable;
	}

	public static double RandConParam(double alpha, int numComponent,
			IntArrayList numData, IntArrayList numTable, double alphaa,
			double alphab, int numIteration) {
		for (int it = 0; it < numIteration; it++) {
			double aa = alphaa;
			double bb = alphab;
			for (int j = 0; j < numComponent; j++) {
				int nd = numData.get(j);
				double xx = RandBeta(alpha + 1.0, nd);
				int zz = Math.random() * (alpha + nd) < nd ? 1 : 0;
				aa += numTable.get(j) - zz;
				bb -= Math.log(xx);
			}
			alpha = RandGamma(aa) / bb;
		}
		return alpha;
	}

	public static int RandMult(DoubleArrayList pi, int length) {
		double sum = 0.0;
		int cc = 0;
		for (cc = 0; cc < length; cc++) {
			sum += pi.getQuick(cc);
		}
		double rand = Math.random() * sum;
		for (cc = 0; cc < length; cc++) {
			rand -= pi.getQuick(cc);
			if (rand < 0.0)
				break;
		}
		return cc;
	}
}
