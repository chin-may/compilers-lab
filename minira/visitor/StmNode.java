package visitor;

import java.util.HashSet;
import java.util.Set;

public class StmNode {
	public Set<Integer> suc;
	public Set<Integer> pred;
	public Set<Integer> use;
	public int def;
	public Set<Integer> lin;
	public Set<Integer> lout;
	public Set<Integer> lin_o;
	public Set<Integer> lout_o;
	public String stm;
	
	StmNode(){
		suc = new HashSet<>();
		pred = new HashSet<>();
		use = new HashSet<>();
		lin = new HashSet<>();
		lout = new HashSet<>();
		def = -1;
	}

	StmNode(String stm){
		suc = new HashSet<>();
		pred = new HashSet<>();
		use = new HashSet<>();
		lin = new HashSet<>();
		lout = new HashSet<>();
		this.stm = stm;
		def = -1;
	}
}
