package distribution;

import cern.colt.list.*;

public class MultiNomial {
	public BaseParam baseParam;
	public IntArrayList boxes;
	public int totalBall;

	/**
	 * Get a new instance of multinomial distribution.
	 * 
	 * @param bb
	 */
	public MultiNomial(BaseParam bb) {
		boxes = new IntArrayList(bb.numDim);
		totalBall = 0;
	}

	/**
	 * Add a new ball on one box.
	 * 
	 * @param index
	 */
	public void addBall(int index) {
		boxes.set(index, boxes.get(index) + 1);
		totalBall++;
	}

	/**
	 * Remove a ball from a box. TODO bounds checking, assert the number of ball
	 * will not be negative.
	 * 
	 * @param index
	 */
	public void delBall(int index) {
		boxes.set(index, boxes.get(index) - 1);
		totalBall--;
	}

	public double marginalLikelihood(int index) {
		return Math.log((baseParam.eta.get(index) + boxes.get(index))
				/ (baseParam.totalEta + totalBall));
	}

	public DoubleArrayList marginalLikelihoods() {
		DoubleArrayList ll = new DoubleArrayList(baseParam.numDim);
		for (int i = 0; i < baseParam.numDim; i++) {
			ll.set(i, marginalLikelihood(i));
		}
		return ll;
	}
	
	public double addBallLikelihood(int index) {
		addBall(index);
		return marginalLikelihood(index);
	}
}
