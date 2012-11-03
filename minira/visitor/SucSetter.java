package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class SucSetter<R> extends GJNoArguDepthFirst<R> {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
	int inum = 0;
	ProcData currproc;
	
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
	   StmNode prev = currproc.nodes.get(currproc.nodes.size() - 1);
	   if(prev.suc.contains(inum)){ 
		   prev.suc.remove(inum ); //TODO should be inum - 1??
	   }
	   inum = 0;
	   currproc = new ProcData(n.f0.f0.tokenImage, Integer.parseInt(n.f2.f0.tokenImage));
	   procs.put(n.f0.f0.tokenImage, currproc);
	   
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      for(int i = 0; i < currproc.bp.size(); i++){
    	  int loc = currproc.bp.get(i);
    	  String lab = currproc.backlabel.get(i);
    	  int labloc = currproc.labeltable.get(lab);
    	  currproc.nodes.get(loc).suc.add(labloc);
      }
      //Set predecessors based on successors.
      for(int i = 0; i< currproc.nodes.size(); i++){
    	  for(Integer sucnum:currproc.nodes.get(i).suc){
    		  currproc.nodes.get(sucnum).pred.add(i);
    	  }
      }
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
	  StmNode curr = new StmNode("noop");
	  curr.suc.add(inum+1);
	  currproc.nodes.add(curr);
	  inum++;
      n.f0.accept(this);
      return null;
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
      StmNode curr = new StmNode("cjump temp " + n.f1.f1.f0.tokenImage + " " + n.f2.f0.tokenImage);
      curr.suc.add(inum+1);
      curr.use.add(Integer.parseInt(n.f1.f1.f0.tokenImage));
      currproc.nodes.add(curr);
      currproc.bp.add(inum);
      currproc.backlabel.add(n.f2.f0.tokenImage);
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      inum++;
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public R visit(JumpStmt n) {
      R _ret=null;
      StmNode curr = new StmNode("jump " + n.f1.f0.tokenImage);
      currproc.nodes.add(curr);
      currproc.bp.add(inum);
      currproc.backlabel.add(n.f1.f0.tokenImage);
      inum++;
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
      StmNode curr = new StmNode("hstore temp " + n.f1.f1.f0.tokenImage + " " + n.f2.f0.tokenImage + " temp " + n.f3.f1.f0.tokenImage);
      curr.suc.add(inum+1);
      curr.use.add(Integer.parseInt(n.f1.f1.f0.tokenImage));
      curr.use.add(Integer.parseInt(n.f3.f1.f0.tokenImage));
      currproc.nodes.add(curr);
      inum++;
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
      StmNode curr = new StmNode();
      curr.suc.add(inum+1);
      curr.def.add(Integer.parseInt(n.f1.f1.f0.tokenImage));
      curr.use.add(Integer.parseInt(n.f2.f1.f0.tokenImage));
      currproc.nodes.add(curr);
      inum++;
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
      StmNode curr = new StmNode();
      curr.suc.add(inum+1);
      curr.def.add(Integer.parseInt(n.f1.f1.f0.tokenImage));
      curr.use.addAll((Collection)n.f2.accept(this));
      currproc.nodes.add(curr);
      inum++;
      n.f0.accept(this);
      n.f1.accept(this);
      
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public R visit(PrintStmt n) {
      R _ret=null;
      StmNode curr = new StmNode();
      curr.suc.add(inum+1);
      curr.use.addAll((Collection)n.f1.accept(this));
      currproc.nodes.add(curr);
      inum++;
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
	  if(n.f0.which == 0){
		  return (R)new HashSet<Integer>();
	  }
      return (R)n.f0.accept(this);
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
      HashSet<Integer> temp = new HashSet<>();
      n.f0.accept(this);
      temp.addAll((Collection)n.f1.accept(this));
      n.f2.accept(this);
      for(Node i: n.f3.nodes){
    	  temp.add((Integer) i.accept(this));
      }
      n.f4.accept(this);
      return (R) temp;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public R visit(HAllocate n) {
      R _ret=null;
      n.f0.accept(this);
      return (R)n.f1.accept(this);
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public R visit(BinOp n) {
      n.f0.accept(this);
      n.f1.accept(this);
      Set<Integer> temp = (Set<Integer>) n.f2.accept(this);
      temp.add(Integer.parseInt(n.f1.f1.f0.tokenImage));
      return (R) temp;
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
	  if(n.f0.which == 0){
      Set<Integer> tmp = new HashSet<>((Integer)n.f0.accept(this));
      return (R) tmp;}
	  else{
		  return (R) new HashSet<Integer>();
	  }
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public R visit(Temp n) {
      n.f0.accept(this);
      n.f1.accept(this);
      return (R)(Integer)Integer.parseInt( n.f1.f0.tokenImage);
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
	  currproc.labeltable.put(n.f0.tokenImage, inum);
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }

}
