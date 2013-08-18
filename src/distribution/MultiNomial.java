package distribution;

import cern.colt.list.*;

public class MultiNomial {
	public BaseParam baseparam;
	public IntArrayList boxes;
	public int totalball;

	/**
	 * Get a new instance of multinomial distribution.
	 * 
	 * @param bb
	 */
	public MultiNomial(BaseParam bb) {
		boxes = new IntArrayList(bb.numdim);
		totalball = 0;
	}

	/**
	 * Add a new ball on one box.
	 * 
	 * @param index
	 */
	public void addBall(int index) {
		boxes.set(index, boxes.get(index) + 1);
		totalball++;
	}

	/**
	 * Remove a ball from a box. TODO bounds checking, assert the number of ball
	 * will not be negative.
	 * 
	 * @param index
	 */
	public void delBall(int index) {
		boxes.set(index, boxes.get(index) - 1);
		totalball--;
	}

	public double marginalLikelihood(int index) {
		return Math.log((baseparam.eta.get(index) + boxes.get(index))
				/ (baseparam.totaleta + totalball));
	}

	public DoubleArrayList marginalLikelihoods() {
		DoubleArrayList ll = new DoubleArrayList(baseparam.numdim);
		for (int i = 0; i < baseparam.numdim; i++) {
			ll.set(i, marginalLikelihood(i));
		}
		return ll;
	}
	
	public double addBallLikelihood(int index) {
		addBall(index);
		return marginalLikelihood(index);
	}
}
