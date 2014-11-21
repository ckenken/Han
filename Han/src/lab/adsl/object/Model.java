package lab.adsl.object;

import java.util.List;
import java.util.Map;

public class Model {

	public static final int INVALID = -1;
	public static final int EMPTY = 0;
	public static final int VALID = 1;

	// variables
	public Map<Long, Double> regionProb;
	public long modelId = -1; 
	// mapping segments 
	public List<Long> sIds; 
	// -1: invalid 0:empty 1: valid
	public int status = VALID; 

}
