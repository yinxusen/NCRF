package HDP;

import java.util.List;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.*;
import distribution.BaseParam;

/**
 * @classqq: sufficient statistics of mix components.
 * @author yinxusen
 *
 */
public class BASE {

	public int numclass;
	public BaseParam hh;
	public DoubleArrayList beta;
	public List<DoubleArrayList> classqq;
}
