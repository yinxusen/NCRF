package HDP;

import utils.RandUtils;
import cern.colt.list.*;

public class CONPARAM {
	double alpha;
	double alphaa;
	double alphab;
	int munDP;
	IntArrayList totalNumberOfData;
	IntArrayList totalNumberOfTable;

	public CONPARAM sample(int numIteration) {
		alpha = RandUtils.RandConParam(alpha, munDP, totalNumberOfData,
				totalNumberOfTable, alphaa, alphab, numIteration);
		return this;
	}
}
