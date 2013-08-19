package utils;

/**
 * Here we only use i.i.d. Gaussian distributions.
 * @author yinxusen
 *
 */
public class MultiGaussianSampler {
	int numModal;
	ZigguratGaussianSampler sampler;

	public MultiGaussianSampler(int n) {
		numModal = n;
		sampler = new ZigguratGaussianSampler();
	}

	public double[] NextSample(double[] mu, double[] sigma) {
		double[] retval = new double[numModal];
		for (int i = 0; i < numModal; i++) {
			retval[i] = sampler.NextSample(mu[i], sigma[i]);
		}
		return retval;
	}

	public double SampleOne(double[] mu, double[] sigma) {
		int index = (int) Math.floor(Math.random() * numModal);
		return NextSample(mu, sigma)[index];
	}

	public static void main(String[] args) {

	}

}
