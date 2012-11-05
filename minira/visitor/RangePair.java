package visitor;

public class RangePair implements Comparable<RangePair>{
	public int start;
	public int end;
	public int rangeof;
	public int location;
	public boolean isReg;

	public RangePair(int s, int e, int ro) {
		start = s;
		end = e;
		rangeof = ro;
	}

	@Override
	public int compareTo(RangePair o) {
		if(end>o.end) return 1;
		if(end<o.end) return -1;
		return 0;
	}

}
