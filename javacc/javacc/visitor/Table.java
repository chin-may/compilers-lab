package visitor;

import java.util.ArrayList;
import java.util.Hashtable;

interface Table {

}

interface TableData {
	public TableData lookup(String str);

}

class GoalTable implements Table {
	String MainClass;
	Hashtable<String, ClassTable> classes = new Hashtable<>();

	public ClassTable lookup(String str) {
		return classes.get(str);
	}

}

class ClassTable implements Table {
	String name;
	Hashtable<String, ClassAttr> attrs = new Hashtable<>();
	Hashtable<String, FuncData> methods = new Hashtable<>();

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
			FuncData fun = meth.get(str);
			if (fun != null)
				return fun;
			else
				return parent.lookup(str);
		}

	}

}

class FuncData implements TableData {
	Hashtable<String, VarData> vars = new Hashtable<>();
	ArrayList<VarData> paramlist = new ArrayList<>();
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

}


class ProgData implements TableData{
	String mainclass;
	Hashtable<String, ClassData> classes = new Hashtable<>();
	
	@Override
	public TableData lookup(String str) {
		return classes.get(str);
	}
	
}













