package HDP;

import java.util.Iterator;
import java.util.List;

import cern.colt.matrix.*;
import cern.colt.list.*;

public class HDP {
	public static final int ACTIVE = 2;
	public static final int FROZEN = 1;
	public static final int HELDOUT = 0;
	int numDP;
	int numConParam;
	BASE base;
	List<DP> dps;
	CONPARAM conParam;
	DoubleMatrix1D clik;
	IntArrayList parentDPIndex;
	IntArrayList conParamIndex;
	IntArrayList stateOfDP;
	
	int addClass() {
		/* Stick breaking */
		for (int i=0; i<dps.size(); i++) {
			double bb, b1, b2;
			DP dp = dps.get(i);
			if(stateOfDP.get(i) == ACTIVE) {
				int parentIndex = parentDPIndex.get(i);
				double alpha = dp.alpha;
				if (parentIndex == -1) {
					/* root is parent */
					b1 = utils.RandUtils.RandGamma(1.0);
					b2 = utils.RandUtils.RandGamma(alpha);
				} else {
					DoubleArrayList beta = dps.get(parentIndex).beta;
					b1 = utils.RandUtils.RandGamma(alpha*beta.get(base.numCLass));
					b2 = utils.RandUtils.RandGamma(alpha*beta.get(base.numCLass+1)); 
				}
				bb = dp.beta.get(base.numCLass) / (b1 + b2);
				dp.beta.set(base.numCLass, bb * b1);
				dp.beta.set(base.numCLass + 1, bb * b2);
			} else {
				// do nothing
			}
		}
		
		base.beta.set(base.numCLass, 0.0);
		base.beta.set(base.numCLass + 1, 1.0);
		base.numCLass ++;
		
		// TODO why return this ?
		return base.numCLass - 1;
	}
}
