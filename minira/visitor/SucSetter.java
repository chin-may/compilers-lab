package visitor;
import syntaxtree.*;
import java.util.*;

import org.omg.CORBA.FREE_MEM;

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
	String[] regStr = {" t0 ",  " t1 ", " t2 ", " t3 ", " t4 " , " t5 ", " t6 ", " t7 ", " t8 ", " t9 ", 
    " s0 ", " s1 ", " s2 ", " s3 ", " s4 ", " s5 ", " s6 ", " s7 ", " v0 ", " v1 ", " a0 ", " a1 ", " a2 ", " a3 "};
	
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


	   inum = 0;
	   currproc = new ProcData("MAIN", 0);
	   procs.put("MAIN", currproc); 
	   currproc.argnum = 0;
	   n.f0.accept(this);
	   n.f1.accept(this);
	   n.f2.accept(this);
	   currproc.nodes.get(inum - 1).suc.remove(inum);
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

	   //Do liveness analysis
	   boolean test = true;
	   while(test){
		   for(int i = currproc.nodes.size() - 1; i >= 0; i--){
			   StmNode sn = currproc.nodes.get(i);
			   sn.lin_o = new HashSet<>(sn.lin);
			   sn.lout_o = new HashSet<>(sn.lout);
			   Set<Integer> temp = new HashSet<Integer>(sn.lout);
			   if(temp.contains(sn.def)) temp.remove(sn.def);
			   temp.addAll(sn.use);
			   sn.lin = temp;
			   sn.lout = new HashSet<>();
			   for(Integer s:sn.suc){
				   sn.lout.addAll(currproc.nodes.get(s).lin);
			   }
		   }
		   test = false;
		   for(StmNode sn:currproc.nodes){
			   if(!sn.lin.equals(sn.lin_o) || !sn.lout.equals(sn.lout_o)){
				   test = true;
				   break;
			   }
		   }

	   }
	   //Saving live ranges
	   for (int i = 0; i < currproc.nodes.size(); i++) {
		   StmNode currstm = currproc.nodes.get(i);
		   for (Integer tempvar : currstm.lout) {
			   if (!currproc.ranges.containsKey(tempvar)) {
				   currproc.ranges.put(tempvar, new RangePair(i, i, tempvar));
			   } 
		   }
	   }
	   
	   for (int i = 0; i < currproc.nodes.size(); i++) {
		   StmNode currstm = currproc.nodes.get(i);
		   for (Integer tempvar : currstm.lin) {
			   currproc.ranges.get(tempvar).end = i;
		   }
		   
	   }
	   
	   
	   
      ArrayList<RangePair> liveint =  new ArrayList<RangePair>();
      for(RangePair rp: currproc.ranges.values()) liveint.add(rp);
      
      int[] locations = new int[liveint.size()];
      boolean[] isReg = new boolean[liveint.size()];
      
      List<Integer> freeReg = new LinkedList<>();
      for(int i=0;i<18;i++)
    	  freeReg.add(i);
      
     //Linear scan 
      int stackloc = 0;
      List<RangePair> active = new LinkedList<>();
      
      Comparator<RangePair> scmp = new Comparator<RangePair>() {

			@Override
			public int compare(RangePair o1, RangePair o2) {
				if(o1.start > o2.start) return 1;
				if(o1.start < o2.start) return -1;
				return 0;
			}};
      Collections.sort(liveint,scmp);
      //System.err.println("Starting lin scan " + currproc.label);
      for(int i = 0; i< liveint.size(); i++){
    	  //Expire old intervals
    	  //System.err.println("i is " + i);
    	  LinkedList<RangePair> expirelst = new LinkedList<RangePair>();
    	  Collections.sort(active);
    	  for(int l=0; l < active.size(); l++){
    		  if(active.get(l).end < liveint.get(i).start){
    			  expirelst.add(active.get(l));
    			  int t =0;
    			  while(liveint.get(t).rangeof != active.get(l).rangeof) t++;
    			  if(!freeReg.contains(locations[t])) freeReg.add(locations[t]);
    			  //else System.err.println("xxxxxxxxxxxxxx");
    			  //System.err.println("expiring " + locations[t]);
    		  }
    		  active.removeAll(expirelst);
    		  //System.err.println("active after expiring:");
    		  for(int dbg_p=0; dbg_p < active.size(); dbg_p++){
    			  //System.err.print(active.get(dbg_p).rangeof + " ");
    		  }
    		  
    	  }
    	  
    	  if(active.size() == 18){
    		  //System.out.println("before spillatinterval active:");
    		  for(int dbg_p=0; dbg_p < active.size(); dbg_p++){
    			  //System.err.print(active.get(dbg_p).rangeof + " ");
    		  }
    		  //Spill at interval
    		  RangePair spill = active.get(active.size() - 1);
    		  if(spill.end > liveint.get(i).end){
    			  int m = 0;
    			  while(liveint.get(m).rangeof != spill.rangeof) m++;
    			  locations[i] = locations[m];
    			  isReg[i] = true;
    			  locations[m] = stackloc++;
    			  isReg[m] = false;
    			  active.remove(spill);
    			  active.add(liveint.get(i));
    			  Collections.sort(active);
    			  ////System.err.println("taking " + locations[i] + " from " + liveint.get(m).rangeof + " giving to" + liveint.get(i).rangeof);
    			  //System.err.println("putting " + liveint.get(m).rangeof + " on stack " + locations[m] );
    		  }
    		  else{
    			  locations[i] = stackloc++;
    			  isReg[i] = false;
    		  }
    		  //System.err.println("After spill interval active:");
    		  for(int dbg_p=0; dbg_p < active.size(); dbg_p++){
    			  //System.err.print(active.get(dbg_p).rangeof + " ");
    		  }
    	  }
    	  else{
    		  locations[i] = freeReg.remove(0);
    		  isReg[i] = true;
    		  active.add(liveint.get(i));
    		  Collections.sort(active);
    		  //System.err.println("alloting new reg " + locations[i] + " to " + liveint.get(i).rangeof);
    	  }
    	  
    	  
      }
	   
	   

	   
      for(int i=0;i<liveint.size();i++){
    	  currproc.ranges.get(liveint.get(i).rangeof).isReg = isReg[i];
    	  currproc.ranges.get(liveint.get(i).rangeof).location = locations[i];
      }
	   
      Set<Integer> usedReg = new HashSet<Integer>();
      for(int i = 0; i<locations.length; i++){
    	  if(isReg[i] && locations[i] >= 10 && locations[i] <= 17)
    		  usedReg.add(locations[i]);
      }
	   currproc.stacktop = stackloc ;
	   currproc.stackspace = stackloc + 13 + usedReg.size();
      currproc.usedReg = usedReg;
      
      //System.err.println(currproc.label);

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
	   inum = 0;
	   currproc = new ProcData(n.f0.f0.tokenImage, Integer.parseInt(n.f2.f0.tokenImage));
	   procs.put(n.f0.f0.tokenImage, currproc); 
	   currproc.argnum = Integer.parseInt(n.f2.f0.tokenImage);
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
      
      //Do liveness analysis
      boolean test = true;
      while(test){
    	  for(int i = currproc.nodes.size() - 1; i >= 0; i--){
    		  StmNode sn = currproc.nodes.get(i);
    		  sn.lin_o = new HashSet<>(sn.lin);
    		  sn.lout_o = new HashSet<>(sn.lout);
    		  Set<Integer> temp = new HashSet<Integer>(sn.lout);
    		  if(temp.contains(sn.def)) temp.remove(sn.def);
    		  temp.addAll(sn.use);
    		  sn.lin = temp;
    		  sn.lout = new HashSet<>();
    		  for(Integer s:sn.suc){
    			  sn.lout.addAll(currproc.nodes.get(s).lin);
    		  }
    	  }
    	  test = false;
    	  for(StmNode sn:currproc.nodes){
    		  if(!sn.lin.equals(sn.lin_o) || !sn.lout.equals(sn.lout_o)){
    			  test = true;
    			  break;
    		  }
    	  }
    	  
      }
      //Saving live ranges
      for (int i = 0; i < currproc.nodes.size(); i++) {
    	  StmNode currstm = currproc.nodes.get(i);
    	  for (Integer tempvar : currstm.lout) {
    		  if (!currproc.ranges.containsKey(tempvar) && tempvar>=Integer.parseInt(n.f2.f0.tokenImage)) {
    			  currproc.ranges.put(tempvar, new RangePair(i, i, tempvar));
    		  } 
    	  }
      }
      for (int i = 0; i < currproc.nodes.size(); i++) {
    	  StmNode currstm = currproc.nodes.get(i);
    	  for (Integer tempvar : currstm.lin) {
    		  if(tempvar>= Integer.parseInt(n.f2.f0.tokenImage))
	    		  currproc.ranges.get(tempvar).end = i;
    	  }

      }
      
      ArrayList<RangePair> liveint =  new ArrayList<RangePair>();
      for(RangePair rp: currproc.ranges.values()) liveint.add(rp);
      
      int[] locations = new int[liveint.size()];
      boolean[] isReg = new boolean[liveint.size()];
      
      List<Integer> freeReg = new LinkedList<>();
      for(int i=0;i<18;i++)
    	  freeReg.add(i);
      
     //Linear scan 
      int stackloc = Integer.parseInt(n.f2.f0.tokenImage) - 4;
      if(stackloc < 0) stackloc =0;
      List<RangePair> active = new LinkedList<>();
      
      Comparator<RangePair> scmp = new Comparator<RangePair>() {

			@Override
			public int compare(RangePair o1, RangePair o2) {
				if(o1.start > o2.start) return 1;
				if(o1.start < o2.start) return -1;
				return 0;
			}};
      Collections.sort(liveint,scmp);
      //System.err.println("Starting lin scan " + currproc.label);
      for(int i = 0; i< liveint.size(); i++){
    	  //Expire old intervals
    	  //System.err.println("i is " + i);
    	  LinkedList<RangePair> expirelst = new LinkedList<RangePair>();
    	  Collections.sort(active);
    	  for(int l=0; l < active.size(); l++){
    		  if(active.get(l).end < liveint.get(i).start){
    			  expirelst.add(active.get(l));
    			  int t =0;
    			  while(liveint.get(t).rangeof != active.get(l).rangeof) t++;
    			  if(!freeReg.contains(locations[t])) freeReg.add(locations[t]);
    			  //else System.err.println("xxxxxxxxxxxxxx");
    			  //System.err.println("expiring " + locations[t]);
    		  }
    		  active.removeAll(expirelst);
    		  //System.err.println("active after expiring:");
    		  for(int dbg_p=0; dbg_p < active.size(); dbg_p++){
    			  //System.err.print(active.get(dbg_p).rangeof + " ");
    		  }
    		  
    	  }
    	  
    	  if(active.size() == 18){
    		  //System.out.println("before spillatinterval active:");
    		  for(int dbg_p=0; dbg_p < active.size(); dbg_p++){
    			  //System.err.print(active.get(dbg_p).rangeof + " ");
    		  }
    		  //Spill at interval
    		  RangePair spill = active.get(active.size() - 1);
    		  if(spill.end > liveint.get(i).end){
    			  int m = 0;
    			  while(liveint.get(m).rangeof != spill.rangeof) m++;
    			  locations[i] = locations[m];
    			  isReg[i] = true;
    			  locations[m] = stackloc++;
    			  isReg[m] = false;
    			  active.remove(spill);
    			  active.add(liveint.get(i));
    			  Collections.sort(active);
    			  //System.err.println("taking " + locations[i] + " from " + liveint.get(m).rangeof + " giving to" + liveint.get(i).rangeof);
    			  //System.err.println("putting " + liveint.get(m).rangeof + " on stack " + locations[m] );
    		  }
    		  else{
    			  locations[i] = stackloc++;
    			  isReg[i] = false;
    		  }
    		  //System.err.println("After spill interval active:");
    		  for(int dbg_p=0; dbg_p < active.size(); dbg_p++){
    			  //System.err.print(active.get(dbg_p).rangeof + " ");
    		  }
    	  }
    	  else{
    		  locations[i] = freeReg.remove(0);
    		  isReg[i] = true;
    		  active.add(liveint.get(i));
    		  Collections.sort(active);
    		  //System.err.println("alloting new reg " + locations[i] + " to " + liveint.get(i).rangeof);
    	  }
    	  
    	  
      }
      
      
      for(int i=0;i<liveint.size();i++){
    	  currproc.ranges.get(liveint.get(i).rangeof).isReg = isReg[i];
    	  currproc.ranges.get(liveint.get(i).rangeof).location = locations[i];
      }
      
      for(int i = 0; i < Integer.parseInt(n.f2.f0.tokenImage); i++){
    	  RangePair rp = new RangePair(0, currproc.nodes.size(),i);
    	  if(i<4){
    		  rp.isReg = true;
    		  rp.location = 20 + i;
    	  }
    	  else{
    		  rp.isReg = false;
    		  rp.location = i - 4;
    	  }
    	  currproc.ranges.put(i, rp);
      }
      
      Set<Integer> usedReg = new HashSet<Integer>();
      for(int i = 0; i<locations.length; i++){
    	  if(isReg[i] && locations[i] >= 10 && locations[i] <= 17) usedReg.add(locations[i]);
      }
      currproc.usedReg = usedReg;
      currproc.stacktop = stackloc ;
      currproc.stackspace = stackloc + 13 + usedReg.size();
      
      
      //System.err.println(currproc.label);
      
      
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
      //System.out.println("ERROR\n");
      StmNode curr = new StmNode();
      currproc.nodes.add(curr);
      inum++;
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
      curr.def = Integer.parseInt(n.f1.f1.f0.tokenImage);
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
      curr.def = Integer.parseInt(n.f1.f1.f0.tokenImage);
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
      return n.f0.accept(this);
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
      //This is for the 'return' instruction
      StmNode curr = new StmNode();
      curr.use.addAll((Collection)n.f3.accept(this));
      currproc.nodes.add(curr);
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
	   int argnum = n.f3.nodes.size();
	   if(argnum > currproc.maxcall) currproc.maxcall = argnum;
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
		   Set<Integer> tmp = new HashSet<>();
		   tmp.add( (Integer)n.f0.accept(this));
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
      Integer dbg_ret = (Integer)Integer.parseInt( n.f1.f0.tokenImage);
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
