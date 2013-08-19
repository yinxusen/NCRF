package utils;

import java.math.BigInteger;

public class Poisson {
	public static BigInteger factorial(int n) {
		BigInteger result = new BigInteger("1");
		if (n < 0) {
			System.err.println("n must be great than 0");
			return new BigInteger("-1");
		} else if (n == 0) {
			return new BigInteger("1");
		} else {
			for (int i = 1; i <= n; i++) {
				BigInteger num = new BigInteger(String.valueOf(i));
				result = result.multiply(num);
			}
			return result;
		}
	}

	public static double getP(int x, double mu) {
		int upper = (int) ((int) Math.pow(mu, x) * Math.pow(Math.E, -mu));
		BigInteger bupper = new BigInteger(upper + "");
		return bupper.divide(factorial(x)).intValue();
	}

	public static void main(String[] args) {
		int x;
		double mu;

	}
}
