package utils;

public class ZigguratGaussianSampler {
	final int numblock = 128;
	final double R = 3.442619855899;
	final double A = 9.91256303526217e-3;
	final double UIntToU = 1.0 / (double) Integer.MAX_VALUE;

	double ADivY0;
	int[] xComp;

	double[] x;
	double[] y;

	double GaussianPdfDenorm(double x) {
		return Math.exp(-x * x / 2.0);
	}

	double GaussianPdfDenormInv(double y) {
		return Math.sqrt(-2.0 * Math.log(y));
	}

	double SampleTail() {
		double x, y;
		do {
			x = -Math.log(Math.random()) / R;
			y = -Math.log(Math.random());
		} while (y + y < x * x);
		return R + x;
	}

	public ZigguratGaussianSampler() {
		x = new double[numblock + 1];
		y = new double[numblock];
		/* Box 0 */
		x[0] = R;
		y[0] = GaussianPdfDenorm(R);
		/* Box 1 */
		x[1] = R;
		y[1] = y[0] + A / x[1];
		/* Box 2 ... Box numblock-1 */
		for (int i = 2; i < numblock; i++) {
			x[i] = GaussianPdfDenormInv(y[i - 1]);
			y[i] = y[i - 1] + A / x[i];
		}
		x[numblock] = 0.0;
		ADivY0 = A / y[0];
		xComp = new int[numblock];
		xComp[0] = (int) ((R * y[0] / A) * Integer.MAX_VALUE);

		for (int i = 1; i < numblock - 1; i++) {
			xComp[i] = (int) ((x[i + 1] / x[i]) * Integer.MAX_VALUE);
		}
		xComp[numblock - 1] = 0;
	}

	public double NextSample() {
		double retval = 0.0;
		while (true) {
			double sign = Math.random() > 0.5 ? 1 : -1;
			int box = (int) Math.floor(Math.random() * numblock);
			int integer = (int) Math.floor(Math.random() * Integer.MAX_VALUE);
			if (0 == box) {
				if (integer < xComp[0]) {
					retval = integer * UIntToU * ADivY0 * sign;
					break;
				}
				retval = SampleTail() * sign;
				break;
			}
			if (integer < xComp[box]) {
				retval = integer * UIntToU * x[box] * sign;
				break;
			}
			double tmp = integer * UIntToU * x[box];
			if (y[box - 1] + (y[box] - y[box - 1]) * Math.random() < GaussianPdfDenorm(tmp)) {
				retval = tmp * sign;
				break;
			}
		}
		if (Math.abs(retval) > 1) {
			retval = Math.random() * 2 - 1;
		}
		return retval;
	}

	public double NextSample(double mu, double sigma) {
		return mu + NextSample() * sigma;
	}

	public static void main(String[] args) {
		ZigguratGaussianSampler gsampler = new ZigguratGaussianSampler();
		for (int i = 0; i < 1000; i++) {
			System.out.println(gsampler.NextSample());
		}
	}
}
