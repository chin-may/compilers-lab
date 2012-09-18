/* 
           MiniJava+ Compiler Project Stage 3 : Type Checking

     The type checking stage of the compiler introduces you to the usage
     of symbol tables.  We saw an example of symbol tables earlier in the
     truth table assignment.  There, we had to use a Hashtable to store the
     values associated with the propositional variables.  That Hashtable is
     a kind of symbol table.  But with a programming language, matters are
     more complicated because now variables have SCOPE.  If we had a simple
     language where you can only have global variables, then we can just
     have one hash table. But with scoping, what we need in general is a
     stack of hash tables, each table represents the compile-time equivalent
     of a run-time stack frame.  However, unlike a run-time stack that
     creates a frame for each instance of a function call, we need only one
     frame for each static scope, that is, each pair of {}'s.  The entry
     of each symbol table will contain the type of each identifier as
     well as information as to the relative position of each variable in
     the frame, so we can eventually generate processor code that refer to
     the correct memory addresses.  

     In MiniJava+, the situation is somewhat simplified because we cannot
     have arbitrarily nested scope.  This is because in Java, classes
     and functions cannot be nested (unlike in ML, Perl, etc...).  Also, 
     in MiniJava, all variables of a method must be declared at the 
     beginning (though this is not enforced by your .cup parser).  This
     means you can't have:

     int x = 2;
     {
        int x = 3; // shadows x on the outside
     }
     ...

     Therefore, our symbol-table-stack is really going to be just
     three levels deep:

         global program level
	 class level
	 method level
	 
     Each identifier of a program will require a symbol table entry.
     An identifier can be class (bankaccount), a method, or a
     variable.  A variable can be an instance variable of a class, a
     formal parameter of a method, or a local variable of a method.
     Each class entry, for example, will need to contain Hashtables 
     for its instance variables and methods.  Our symbol table can
     therefore also be characterized as a tree:

                          global
                       /    |    \
                      /     |     \
		   class  class  class
		 /      \
               vars    methods    ...
	               /     \
		     args   locals 
		     
	      ...    

As your textbook points out, type checking minijava requires two stages,
or two visitors: one to construct the symbol table and one to use the
table to check each construct of the program.  The two stages are
required to resolve forward-reference.

For this assignment, I have provided you with:

  1. The classes for the symbol table structure
  2. The visitor that constructs the symbol table

You have to write:

  3. The visitor that type-checks the program using the symbol table.

It's very possible that my implementation can be improved upon.  The
book, in fact, suggests using a red-black balanced binary search tree(!).  
In particular, it may be possible to elminate the abstract interface, which
will also elminate much in the way of type casting.  Some elements of the
structure are redundant and may not even be useful to you, depending on
how you write your type checker.  

Types in the symbol table are simply represented as strings such as
"int", "int[]" and "bankaccount".  This makes checking for type-equality
easier (this is possible in MiniJava, but not for all languages).  However
PLEASE REMEMBER that in Java, you need to use s1.equals(s2) to check for
string equality, not s1==s2, which is only pointer equality.

*/

import java.util.*;

interface tableentry      // type of all symbol table entries
{
    public tableentry lookup(String v);
}

// table entry for variables: instance vars, local vars and formal params
class varentry implements tableentry
{
    boolean isformal = false;
    boolean isinstance = false;
    String type;    // type such as "int".
    int position;  // relative position in frame
    tableentry previous;  // pointer to previous stack frame
    public tableentry lookup(String s) { return previous.lookup(s); }
}

// table entry for methods contains hashtables for formal parameters and
// for local variables.  The orderparams is an ordered view of the 
// params hash table.  This is because in type checking function calls,
// we need to make sure that the parameters are passed in the right order.
class funcentry implements tableentry
{
    Hashtable<String,varentry> params = new Hashtable<String,varentry>();
    ArrayList<varentry>orderparams = new ArrayList<varentry>();
    Hashtable<String,varentry> locals = new Hashtable<String,varentry>();
    String returntype;                  // return type
    tableentry previous;  // pointer to previous stack frame
    boolean ismain = false;
    public tableentry lookup(String s)
    {
	tableentry e = locals.get(s);
	if (e!=null) return e; 
	else 
	{
	    e = params.get(s);
	    if (e!=null) return e; else return previous.lookup(s);
	}
    } // lookup
} // funcentry

// table entry for classes, including mainclass as a special case
class classentry implements tableentry
{
    boolean ismain = false;
    String name; // for "this"
    Hashtable<String,varentry> fields = new Hashtable<String,varentry>();
    Hashtable<String,funcentry> methods = new Hashtable<String,funcentry>();
    tableentry previous;  // pointer to previous stack frame
    public tableentry lookup(String s)
    {
        varentry v = fields.get(s);
	if (v!=null) return v; 
	else 
	    {  
		funcentry f = methods.get(s);
		if (f!=null) return f; else return previous.lookup(s);
	    }
    }
} // classentry

// table entry for one compilation unit
class programentry implements tableentry
{
    String mainclass;
    Hashtable<String,classentry> classes = new Hashtable<String,classentry>();
    public tableentry lookup(String s)
    {	return classes.get(s);    }
}


/////////////////////////////////////////////////////////////////////////////

// Visitor to construct symbol table.
// note that it simply overrides certain methods of the nullvisitor
class tablemaker extends nullvisitor<tableentry> implements visitor<tableentry>
{

    // current table scope:
    tableentry current;
    programentry top;
    classentry cclass; // current class;
    int position;  // for variable positioning

    public tableentry visit(program x) 
    {
	programentry prog = new programentry();
	current = prog;  // current table scope is entire program
	top = prog;
	prog.mainclass = x.mc.classname;
	x.mc.accept(this);
	x.cl.accept(this); // visit all class declarations
	return prog;       // return the program 
    }

    public tableentry visit(classdec x) 
    {
	classentry cl = new classentry();
	cl.name = x.classname;
	cl.previous = top; // set previous scope pointer
	current = cl;          // current scope now this class
        programentry prog = (programentry)cl.previous;
	prog.classes.put(x.classname,cl);  // enter into program entry
	position = 0;
	cclass = cl;
        x.vars.accept(this); // visit all field variables
	x.methods.accept(this); // visit all instance methods
	return cl;
    }

    // type type of main is going to be ()->int just to be simple
    public tableentry visit(mainclass x)
    {
	classentry M = new classentry();
	M.ismain = true;
	M.name = x.classname;
	M.previous = top; // set previous scope (program)
	current = M; // current scope now points to this classentry
        programentry prog = (programentry)M.previous;
        prog.classes.put(x.classname,M);
	funcentry special = new funcentry();
	special.ismain=true;
	special.returntype = "void";
	M.methods.put("main",special);
	special.previous = M;
	current=special;
        position = 0;
	cclass = M;
	x.body.accept(this);
	return M;
    }

    public tableentry visit(methoddec x)
    {
	funcentry F = new funcentry();
	F.previous = cclass;
	classentry pc = cclass; // parent class
	pc.methods.put(x.mname,F);
	current = F;
        position = 0;
	x.args.accept(this); // visit formal params
        position = 0;
	x.body.accept(this); // visit statements (vardecstats actually)
	F.returntype = typetostring(x.rettype); // set return type
	return F;
    }

    public tableentry visit(vardecstat x) 
    {
        varentry V = new varentry();
	V.position = position++;
	V.previous = current;
	V.type = typetostring(x.t);
	if (current instanceof classentry) 
          {  V.isinstance=true;
	     ((classentry)current).fields.put(x.v,V);
	  } // classentry parent
	else if (current instanceof funcentry)
	  {
	     V.isformal=false;
	     ((funcentry)current).locals.put(x.v,V);
	  } // funcentry parent
	else System.out.println("something bad with "+x.v);
	//System.out.println("vardec for "+x.v+", type "+V.type);
	return V;
    }//vardecstat

    public tableentry visit(formal x)
    {
	varentry A = new varentry();
	A.position = position++;
	A.previous = current;
	A.isformal=true;
	A.type = typetostring(x.ty);
	funcentry pf = (funcentry)current; // parent function dec
	pf.params.put(x.name,A);  // insert into parameters of hash table
	pf.orderparams.add(A); //    insert into ordered view of hash table
	return A;
    }

      public static String typetostring(typ t)
      {
  	  if (t instanceof vartype) return ((vartype)t).classname;
	  if (t instanceof inttype) return "int";
	  if (t instanceof booltype) return "boolean";
	  if (t instanceof stringtype) return "String";  // may need change
	  if (t instanceof intarraytype) return "int[]";
	  else return null;  // null indicates error
      }    

} // tablemaker

//////////////////////////////////////////////////////////////////


// Visitor to do type checking - you need to write this.
// return "void" for statements.

/* 
   One very important role of the type checker is reporting errors.
   Knowing the line number where the error occured would certainly
   help.  Because of the way the compiler is structured, this
   is not too hard to do, even if you neglected to do it in you .cup
   parser.  I've changed by "baseinfo" super class of the absyn classes to
   include a line number for each absyn class that inherits from it:

class baseinfo
{
    typ mjtype = null;  // for type checking.
    String currentclass = "GLOBAL";  // might be useful
    int line;   // line number where construct ends.
    public baseinfo() { line = Global.line; }
    // more stuff may be added...
}  // baseinfo
   
Each time an instance of one of the absyn classes is created, the
baseinfo constructor is automatically called, which will record the
line that the construct is close to.

The variable Global.line can be used as you scan and parse the program
to record the current line that's being parsed.  To use it, add the
following to be beginning of you .lex file:

<YYINITIAL>\n { Global.line += 1; }

You should now be able to print useful line numbers with your error
messages.

*/

class typechecker implements visitor<String>
{
    // Symbol table pointer
    programentry top;  // top level pointer
    classentry currentclass; // current class pointer
    funcentry currentmethod; // current method pointer

    typechecker(programentry t) {top=t;}

    // utility for reporting errors
    void report(String s, int line)
    {
	System.out.println("Error near line "+line+": "+s);
    }

// Sample visit method:
    public String visit(plusexp x)
    {
	String t1 = x.e1.accept(this);
	String t2 = x.e2.accept(this);
	if (!t1.equals("int") || !t2.equals("int"))
	    report("malformed arithmetic expression",x.line);	
	return "int";
    }

    // ...
} // typechecker


/*
    Finally, integrate the type checker with your C++ translator:

    public static void main(String[] args) throws Exception
    {
        Global.sourcefile = args[0];
        Global.targetfile = args[1];
        parser Pr = new parser();
        absyn Prog = (absyn) Pr.parse().value;  // parse and get absyn tree

        // type check:
        tablemaker tm = new tablemaker();
        programentry symboltable = (programentry)Prog.accept(tm);
        typechecker tpr = new typechecker(symboltable);
        String t = Prog.accept(tpr);
        System.out.println("type checking complete");

        PrintWriter pw = new PrintWriter(new FileWriter(args[1]));
        cppvisitor translator = new cppvisitor(pw);
        Prog.accept(translator);
        pw.close();
   } // main
*/

