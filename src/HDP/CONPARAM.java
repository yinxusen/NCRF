package HDP;

import utils.RandUtils;
import cern.colt.list.*;

public class CONPARAM {
	double alpha;
	double alphaa;
	double alphab;
	int numdp;
	IntArrayList totalnd;
	IntArrayList totalnt;

	public CONPARAM sample(int numIteration) {
		alpha = RandUtils.RandConParam(alpha, numdp, totalnd,
				totalnt, alphaa, alphab, numIteration);
		return this;
	}
}
