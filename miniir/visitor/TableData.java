package visitor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;


public interface TableData {
	public TableData lookup(String str);
	public String getType(String var);

}

class GoalData implements TableData {
	String MainClass;
	Hashtable<String, ClassData> classes = new Hashtable<String, ClassData>();

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
	String name;
	int posnum;
	int address;
	int varloc;

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
	Hashtable<String, FuncData> meth = new Hashtable<String, FuncData>();
	TableData parent;
	LinkedList<String> allfun, allatt;

	@Override
	public TableData lookup(String str) {
		VarData var = attr.get(str);
		if (var != null)
			return var;
		else if(parent instanceof ClassData){
				return parent.lookup(str);
		}
		else return null;

	}
	public FuncData flookup(String str) {
		FuncData fun = meth.get(str);
		if (fun != null)
			return fun;
		else if(parent instanceof ClassData){
				return ((ClassData)parent).flookup(str);
		}
		else return null;

	}
	@Override
	public String getType(String var) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getClassName(String fun){
		if(meth.containsKey(fun)) return name;
		else if(parent instanceof ClassData) return ((ClassData) parent).getClassName(fun);
		assert(false);
		return null;
	}
	public int getAttrNum(String att){
		int j = 0;
		for(String s:allatt){
			if(s.equals(att)) return j;
			j++;
		}
		assert(false);
		return -1;
	}

}

class FuncData implements TableData {
	Hashtable<String, VarData> vars = new Hashtable<String, VarData>();
	ArrayList<String> paramlist = new ArrayList<String>();
	ArrayList<String> paramIDlist = new ArrayList<String>();
	String ret;
	TableData parent;
	String name;
	int locnum;

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
	
	public int getFormalNum(String s){
		int j = 0;
		for(String f:paramIDlist){
			if(f.equals(s)) return j;
			j++;
		}
		assert(false);
		return -1;
	}

}


class ProgData implements TableData{
	String mainclass;
	Hashtable<String, ClassData> classes = new Hashtable<String, ClassData>();
	
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
	
	public boolean isLooping(String clname){
		ClassData cd = classes.get(clname);
		while(cd.parent!=null && cd.parent instanceof ClassData){
			cd = (ClassData) cd.parent;
			if(cd.name.equals(clname)){
				return true;
			}
		}
		return false;
	}
	
	public LinkedList<String> setAllAtt(ClassData cd){
		if(cd.parent instanceof ProgData){
			cd.allatt = new LinkedList<String>();
			for(VarData v:cd.attr.values()){
				cd.allatt.add(v.name);
			}
			return cd.allatt;
		}
		else{
			cd.allatt = setAllAtt((ClassData)cd.parent);
			for(VarData v:cd.attr.values()){
				if(!cd.allatt.contains(v.name))cd.allatt.add(v.name);
			}
			return cd.allatt;
			
		}
	}
	public LinkedList<String> setAllFun(ClassData cd){
		if(cd.parent instanceof ProgData){
			cd.allfun = new LinkedList<String>();
			for(FuncData f:cd.meth.values()){
				cd.allfun.add(f.name);
			}
			return cd.allfun;
		}
		else{
			cd.allfun = setAllFun((ClassData)cd.parent);
			for(FuncData f:cd.meth.values()){
				if(!cd.allfun.contains(f.name))cd.allfun.add(f.name);
			}
			return cd.allfun;
			
		}
	}
	
}













