package visitor;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcData {
	public ArrayList<StmNode> nodes;
	public String label;
	HashMap<String, Integer> labeltable;
	int argnum;
	ArrayList<Integer> bp;
	ArrayList<String> backlabel;
	HashMap<Integer,RangePair> ranges;
	int stackspace;
	int maxcall;

	public ProcData(String lbl, int argn) {
		nodes = new ArrayList<StmNode>();
		labeltable = new HashMap<>();
		label = lbl;
		argnum  = argn;
		bp = new ArrayList<Integer>();
		backlabel = new ArrayList<String>();
		ranges = new HashMap<Integer, RangePair>();
		stackspace = 0;
		maxcall = 0;
	}
	
}
