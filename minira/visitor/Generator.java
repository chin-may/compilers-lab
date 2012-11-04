//
// Generated by JTB 1.3.2
//

package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class Generator<R> extends GJNoArguDepthFirst<R> {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
	
	ProcData currproc;
	String[] regStr = {" t0 ",  " t1 ", " t2 ", " t3 ", " t4 " , " t5 ", " t6 ", " t7 ", " t8 ", " t9 ", 
    " s0 ", " s1 ", " s2 ", " s3 ", " s4 ", " s5 ", " s6 ", " s7 "};
   public R visit(NodeList n) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeListOptional n) {
      if ( n.present() ) {
         R _ret=null;
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this);
            _count++;
         }
         return _ret;
      }
      else
         return null;
   }

   public R visit(NodeOptional n) {
      if ( n.present() )
         return n.node.accept(this);
      else
         return null;
   }

   public R visit(NodeSequence n) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeToken n) { return null; }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
   public R visit(Goal n) {
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public R visit(StmtList n) {
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public R visit(Procedure n) {
	  currproc = procs.get(n.f0.f0.tokenImage);
	  emit(n.f0.f0.tokenImage + " [ " + n.f2.f0.tokenImage + " ]\n" );
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      return _ret;
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
   public R visit(Stmt n) {
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public R visit(NoOpStmt n) {
	   emit("noop\n");
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public R visit(ErrorStmt n) {
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public R visit(CJumpStmt n) {
      R _ret=null;
      int tnum = Integer.parseInt(n.f1.f0.tokenImage);
      RangePair var = currproc.ranges.get(tnum);
      if(var.isReg){
    	  emit("cjump " + regStr[var.location] + n.f2.f0.tokenImage);
      }
      else{
    	  emit("aload v0 spilledarg " + var.location + "\n");
    	  emit("cjump v0 " + n.f2.f0.tokenImage + "\n");
      }
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public R visit(JumpStmt n) {
      R _ret=null;
      emit("jump " + n.f1.f0.tokenImage + "\n");
      n.f0.accept(this);
      n.f1.accept(this);
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public R visit(HStoreStmt n) {
      R _ret=null;
      RangePair var1, var2;
      var1 = currproc.ranges.get(Integer.parseInt(n.f1.f1.f0.tokenImage));
      var2 = currproc.ranges.get(Integer.parseInt(n.f3.f1.f0.tokenImage));
      if(var1.isReg && var2.isReg ){
    	  emit("hstore " + regStr[var1.location] + n.f2.f0.tokenImage + regStr[var2.location] + "\n");
      }
      else if(var1.isReg && !var2.isReg){
    	  emit("aload v0 spilledarg " + var2.location + "\n");
    	  emit("hstore " + regStr[var1.location] + n.f2.f0.tokenImage + " v0\n");
      }
      else if(!var1.isReg && var2.isReg){
    	  emit("aload v0 spilledarg " + var1.location + "\n");
    	  emit("hstore v0 " + n.f2.f0.tokenImage + regStr[var2.location]  + "\n");
      }
      else{
    	  emit("aload v0 spilledarg " + var1.location + "\n");
    	  emit("aload v1 spilledarg " + var2.location + "\n");
    	  emit("hstore v0 " + n.f2.f0.tokenImage + " v1\n");
      }
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public R visit(HLoadStmt n) {
      R _ret=null;
      RangePair var1, var2;
      var1 = currproc.ranges.get(Integer.parseInt(n.f1.f1.f0.tokenImage));
      var2 = currproc.ranges.get(Integer.parseInt(n.f2.f1.f0.tokenImage));
      if(var1.isReg && var2.isReg ){
    	  emit("hload " + regStr[var1.location] + regStr[var2.location] + n.f2.f0.tokenImage + "\n");
      }
      else if(var1.isReg && !var2.isReg){
    	  emit("aload v0 spilledarg " + var2.location + "\n");
    	  emit("hload " + regStr[var1.location] + " v0 "+ n.f2.f0.tokenImage + "\n" );
      }
      else if(!var1.isReg && var2.isReg){
    	  emit("aload v0 spilledarg " + var1.location + "\n");
    	  emit("hload v0 " + regStr[var2.location] +  n.f3.f0.tokenImage + "\n");
      }
      else{
    	  emit("aload v0 spilledarg " + var1.location + "\n");
    	  emit("aload v1 spilledarg " + var2.location + "\n");
    	  emit("hload v0 v1 " + n.f2.f0.tokenImage + "\n" );
      }
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public R visit(MoveStmt n) {
      R _ret=null;
      RangePair var = currproc.ranges.get(Integer.parseInt(n.f1.f1.f0.tokenImage));
      if(var.isReg){
      switch(n.f2.f0.which){
      //TODO
      case 0:
    	  Call c = (Call) n.f2.f0.choice;
      case 1:
    	  HAllocate h = (HAllocate) n.f2.f0.choice;
      case 2:
    	  BinOp b = (BinOp) n.f2.f0.choice;
      case 3:
    	  SimpleExp s = (SimpleExp) n.f2.f0.choice;
    	  
      }
    	  
      }
      else{
    	  
      }
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public R visit(PrintStmt n) {
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      return _ret;
   }

   /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public R visit(Exp n) {
      R _ret=null;
      n.f0.accept(this);
      
      return _ret;
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
   public R visit(StmtExp n) {
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
   public R visit(Call n) {
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public R visit(HAllocate n) {
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public R visit(BinOp n) {
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      return _ret;
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public R visit(Operator n) {
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public R visit(SimpleExp n) {
      R _ret=null;
      switch(n.f0.which){
      case 0:
    	  
      
      }
      n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public R visit(Temp n) {
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n) {
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Label n) {
	   //System.out.println(n.f0.tokenImage + "\n");
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }
   
   void emit(String s){
	   System.out.println(s.toUpperCase());
   }

}
