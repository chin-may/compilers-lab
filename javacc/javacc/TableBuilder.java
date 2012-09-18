import visitor.Visitor;
import syntaxtree.*;

public class TableBuilder implements Visitor {

	@Override
	public void visit(NodeList n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NodeListOptional n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NodeOptional n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NodeSequence n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NodeToken n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Goal n) {
		n.f0.accept(this);
		
	}

	@Override
	public void visit(MainClass n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TypeDeclaration n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ClassDeclaration n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ClassExtendsDeclaration n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VarDeclaration n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MethodDeclaration n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FormalParameterList n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FormalParameter n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FormalParameterRest n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Type n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayType n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BooleanType n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IntegerType n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Statement n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Block n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AssignmentStatement n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayAssignmentStatement n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IfStatement n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(WhileStatement n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(PrintStatement n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Expression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AndExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CompareExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(PlusExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MinusExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TimesExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayLookup n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayLength n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MessageSend n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ExpressionList n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ExpressionRest n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(PrimaryExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IntegerLiteral n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TrueLiteral n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FalseLiteral n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Identifier n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ThisExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayAllocationExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AllocationExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NotExpression n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BracketExpression n) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param args
	 */

}
