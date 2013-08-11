package HDP;

import java.util.List;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import distribution.BaseParam;
import distribution.MultiNomial;

public class DP extends MultiNomial {

	public DP(BaseParam bb) {
		super(bb);
		// TODO Auto-generated constructor stub
	}
	public double alpha;
	public DoubleArrayList beta;
	public IntArrayList numDataItemsEachComponent;
	public IntArrayList numTablesEachComponent;
	public IntArrayList clusterOfData;
	public int numDataItemsFromThisDP;
	public IntArrayList sufficientStatistics;
}
