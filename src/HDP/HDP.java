package HDP;

import java.util.Iterator;
import java.util.List;

import cern.colt.matrix.*;
import cern.colt.list.*;
import distribution.BaseParam;
import utils.RandUtils;

public class HDP {
	public static final int ACTIVE = 2;
	public static final int FROZEN = 1;
	public static final int HELDOUT = 0;
	int numdp;
	int numconparam;
	BASE base;
	List<DP> dplist;
	List<CONPARAM> conparam;
	DoubleMatrix1D clik;
	IntArrayList ppindex;
	IntArrayList cpindex;
	IntArrayList dpstate;
	IntArrayList ttindex;

	int addClass() {
		/* Stick breaking */
		for (int i = 0; i < dplist.size(); i++) {
			double bb, b1, b2;
			DP dp = dplist.get(i);
			if (dpstate.get(i) == ACTIVE) {
				int parentIndex = ppindex.get(i);
				double alpha = dp.alpha;
				if (parentIndex == -1) {
					/* root is parent */
					b1 = RandUtils.RandGamma(1.0);
					b2 = RandUtils.RandGamma(alpha);
				} else {
					DoubleArrayList beta = dplist.get(parentIndex).beta;
					b1 = RandUtils.RandGamma(alpha * beta.get(base.numclass));
					b2 = RandUtils.RandGamma(alpha * beta.get(base.numclass + 1));
				}
				bb = dp.beta.get(base.numclass) / (b1 + b2);
				dp.beta.set(base.numclass, bb * b1);
				dp.beta.set(base.numclass + 1, bb * b2);
			} else {
				// do nothing
			}
		}

		base.beta.set(base.numclass, 0.0);
		base.beta.set(base.numclass + 1, 1.0);
		base.numclass++;

		// TODO why return this ?
		return base.numclass - 1;
	}

	int delClass(int delIndex) {
		base.classqq.remove(delIndex);
		for (int i = 0; i < dplist.size(); i++) {
			DP dp = dplist.get(i);
			if (dpstate.get(i) == ACTIVE) {
				dp.beta.remove(delIndex);
			}
			if (dpstate.get(i) != HELDOUT) {
				dp.classnd.delete(delIndex);
				dp.classnt.delete(delIndex);
				for (int j = 0; j < dp.datacc.size(); i++) {
					if (dp.datacc.get(j) > delIndex) {
						dp.datacc.set(i, dp.datacc.get(i) + 1);
					}
				}
			}
		}
		base.beta.set(base.numclass, 0.0);
		base.beta.set(base.numclass - 1, 1.0);
		base.numclass--;
		return base.numclass;
	}

	/**
	 * TODO here
	 * @return
	 */
	public double likelihood() {
		for (int i = 0; i < dplist.size(); i++) {
			DP dp = dplist.get(i);
			if (dpstate.get(i) == ACTIVE) {
				for (int j = 0; j < dp.numdata; j++) {

				}
			}
		}
		return 0.0;
	}

	public void SampleConParams(int numIteration) {
		for (int i = 0; i < numconparam; i++) {
			conparam.set(i, conparam.get(i).sample(numIteration));
		}
		// update DP
		for (int i = 0; i < numdp; i++) {
			if (dpstate.get(i) == ACTIVE) {
				DP dp = dplist.get(i);
				dp.alpha = conparam.get(cpindex.get(i)).alpha;
				dplist.set(i, dp);
			}
		}
	}

	public void SampleBeta(int index) {
		int pp = ppindex.get(index);
		DoubleArrayList beta = (pp == -1) ? base.beta : dplist.get(pp).beta;
		DoubleArrayList clik = new DoubleArrayList(beta.size());
		for (int i = 0; i < base.numclass; i++) {
			clik.set(i, dplist.get(index).classnd.get(i) + dplist.get(index).alpha * beta.get(i));
		}
		RandUtils.RandDir(clik, dplist.get(index).beta);
	}

	public void SampleNumberOfTables(int index) {
		int pp = ppindex.get(index);
		DP dp = dplist.get(index);
		DP parentDp = dplist.get(pp);
		if (pp == -1) {
			for (int i = 0; i < base.numclass; i++) {
				dp.classnt.set(i, dp.classnd.get(i) > 0 ? 1 : 0);
				dplist.set(index, dp);
			}
		} else {
			for (int i = 0; i < base.numclass; i++) {
				parentDp.classnd.set(i, parentDp.classnd.get(i)
						- dp.classnt.get(i));
				dp.classnt.set(i,
						RandUtils.RandNumTables(dp.alpha * parentDp.beta.get(i), dp.classnd.get(i)));
			}
		}
	}

	/**
	 * TODO Make sure that we can handle totalNumberOfData of conParams in this
	 * way.
	 * 
	 * @param jj
	 */
	public void CollectTotalPerDP(int jj) {
		int totalnt = 0;
		int totalnd = 0;
		int cp = cpindex.get(jj);
		int tt = ttindex.get(jj);
		for (int cc = 0; cc < base.numclass; cc++) {
			totalnt += dplist.get(jj).classnt.get(cc);
			totalnd += dplist.get(jj).classnd.get(cc);
		}
		conparam.get(cp).totalNumberOfData.set(tt, totalnd);
		conparam.get(cp).totalNumberOfTable.set(tt, totalnt);
	}

	public void SampleDataAssignment(int jj) {
		DP dp = dplist.get(jj);
		BaseParam bp = base.hh;
		int numData = dp.numdata;
		for (int ii = 0; ii < numData; ii++) {
			int ss = dp.datass.get(ii);
			int cc = dp.datacc.get(ii);
			dp.delBall(cc);
			dp.classnd.set(cc, dp.classnd.get(cc) - 1);
			DoubleArrayList mlik = dp.marginalLikelihoods();
			for (cc = 0; cc <= base.numclass; cc++) {
				mlik.set(cc, mlik.get(cc) * (dp.classnd.get(cc) + dp.alpha * dp.beta.get(cc)));
			}
			cc = RandUtils.RandMult(mlik, mlik.size());
			dp.datacc.set(ii, cc);
			dp.addBall(cc);
			dp.classnd.set(cc, dp.classnd.get(cc) + 1);
			/* add class if necessary */
			if (cc == base.numclass) {
				addClass();
			}
		}
	}

	public DoubleArrayList iterate(DoubleArrayList iterLik, int numIteration, int doConParam, int doLik) {
		int jj = 0;
		for (int i = 0; i < numIteration; i++) {
			for (jj = numdp - 1; jj >= 0; jj--) {
				if (dpstate.getQuick(jj) == ACTIVE) {
					SampleDataAssignment(jj);
					SampleNumberOfTables(jj);
					CollectTotalPerDP(jj);
				}
			}
			for (jj = 0; jj < numdp; jj++) {
				if (dpstate.get(jj) == ACTIVE) {
					SampleBeta(jj);
				}
			}
			// TODO fill in with the deletion
			/* delete useless classes */
			for (int cc = base.numclass - 1; cc >= 0; cc--) {
			}

			if (doConParam > 0)
				SampleConParams(doConParam);
			if (doLik == 1)
				iterLik.set(i, likelihood());
		}
		return iterLik;
	}

	/**
	 * Active a specified DP
	 * Usually a HELDOUT DP
	 */
	public void DPActivate(int jj) {
		DP dp = dplist.get(jj);
		if (dpstate.get(jj) == HELDOUT) {
			dp.classnd = new IntArrayList(base.numclass);
			dp.classnt = new IntArrayList(base.numclass);
			dp.datacc = new IntArrayList(dp.numdata);
			for (int ii = 0; ii < dp.numdata; ii++) {
				int cc = RandUtils.RandUniform(base.numclass);
				dp.datacc.set(ii, cc);
				dp.addBall(dp.datass.get(ii));
				dp.classnd.set(cc, dp.classnd.get(cc) + 1);
			}
			for (int cc = 0; cc < base.numclass; cc++) {
				dp.classnt.set(cc, dp.classnd.get(cc));
			}
			int parentIndex = ppindex.get(jj);
			if (parentIndex > -1) {
				for (int cc = 0; cc < base.numclass; cc++) {
					dplist.get(parentIndex).classnd.set(cc,
							dplist.get(parentIndex).classnd.get(cc) + dp.classnt.get(cc));
				}
				CollectTotalPerDP(parentIndex);
			}
		}
		int cp = cpindex.get(jj);
		dp.alpha = conparam.get(cp).alpha;
		dp.beta = new DoubleArrayList(base.numclass);
		dplist.set(jj, dp);
		CollectTotalPerDP(jj);
		dpstate.set(jj, ACTIVE);
	}

	public void DPHeldOut(int jj) {
		DP dp = dplist.get(jj);
		for (int ii = 0; ii < dp.numdata; ii++) {
			dp.delBall(ii);
		}
		int pp = ppindex.get(jj);
		/*
		 * TODO 
		 * Why not while pp here? something wrong?
		 */
		if (pp > -1) {
			for (int cc = 0; cc < base.numclass; cc++) {
				dplist.get(pp).classnd.set(cc, dplist.get(pp).classnd.get(cc)
						- dp.classnt.get(cc));
			}
			while (pp > -1) {
				SampleNumberOfTables(pp);
				CollectTotalPerDP(pp);
				pp = ppindex.get(pp);
			}
		}
		int cp = cpindex.get(jj);
		int tt = ttindex.get(jj);
		conparam.get(cp).totalNumberOfData.set(tt, 0);
		conparam.get(cp).totalNumberOfTable.set(tt, 0);
		dpstate.set(jj, HELDOUT);
	}

	public void predict(DoubleArrayList lik, int numBurnIn, int numSample, int numPredict, IntArrayList predictJJ,
			int doConParam) {
		for (int jj = 0; jj < numPredict; jj++) {
			DPHeldOut(predictJJ.get(jj));
		}
		for (int jj = 0; jj < numPredict; jj++) {
			DoubleArrayList likTmp = new DoubleArrayList(numSample);
			DPActivate(predictJJ.get(jj));
			iterate(null, numBurnIn, doConParam, 0);
			iterate(lik, numSample, doConParam, 1);
			lik.addAllOf(likTmp);
			DPHeldOut(predictJJ.get(jj));
		}
		for (int jj = 0; jj < numPredict; jj++) {
			DPActivate(predictJJ.get(jj));
		}
	}
}
