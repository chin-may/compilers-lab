package visitor;

import java.util.ArrayList;
import java.util.Hashtable;


public interface TableData {
	public TableData lookup(String str);
	public String getType(String var);

}

class GoalData implements TableData {
	String MainClass;
	Hashtable<String, ClassData> classes = new Hashtable<>();

	public ClassData lookup(String str) {
		return classes.get(str);
	}

	@Override
	public String getType(String var) {
		// TODO Auto-generated method stub
		return null;
	}
	

}


class ClassAttr {

}

class VarData implements TableData {
	String type;
	TableData prev;

	@Override
	public TableData lookup(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType(String var) {
		// TODO Auto-generated method stub
		return null;
	}
}

class ClassData implements TableData {
	String name;
	Hashtable<String, VarData> attr = new Hashtable<String, VarData>();
	Hashtable<String, FuncData> meth = new Hashtable<>();
	TableData parent;

	@Override
	public TableData lookup(String str) {
		VarData var = attr.get(str);
		if (var != null)
			return var;
		else {
				return parent.lookup(str);
		}

	}
	public FuncData flookup(String str) {
		FuncData fun = meth.get(str);
		if (fun != null)
			return fun;
		else {
				return ((ClassData)parent).flookup(str);
		}

	}
	@Override
	public String getType(String var) {
		// TODO Auto-generated method stub
		return null;
	}

}

class FuncData implements TableData {
	Hashtable<String, VarData> vars = new Hashtable<>();
	ArrayList<String> paramlist = new ArrayList<>();
	String ret;
	TableData parent;

	public TableData lookup(String str) {
		TableData pr = vars.get(str);
		if (pr != null)
			return pr;
		else {
			return parent.lookup(str);
			
		}
	}

	@Override
	public String getType(String var) {
		
		return null;
	}

}


class ProgData implements TableData{
	String mainclass;
	Hashtable<String, ClassData> classes = new Hashtable<>();
	
	@Override
	public TableData lookup(String str) {
		return classes.get(str);
	}

	@Override
	public String getType(String var) {
		return null;
	}
	
	public boolean isParent(String p,String c){
		if(p.equals(c)) return true;
		ClassData cc;
		cc = classes.get(c);
		if(cc==null) return false;
		while(cc.parent!=null){
			if(cc.name.equals(p)){
				return true;
			}
			else{
				if(cc.parent instanceof ClassData ){
					cc = (ClassData)cc.parent;
				}
				else return false;
			}
		}
		return false;
	}
	
}













