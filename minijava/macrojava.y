%{
#include<stdio.h>
#include<stdlib.h>
extern FILE* yyin;

extern int yyparse();
extern int yylineno;
int yydebug = 1;
%}

%union{
	int ival; // integers
	char *bval;// true and false
	char  *kw; // such as class, int, boolean etc
	char *op; // such as +, -, * etc
	char *id; // identifiers
}

%token <ival> INTVAL
%token <bval> BOOLVAL
%token <kw> KEYWORD
%token <op> OPERATOR
%token <id> IDENTIFIER
%token THIS
%token NEW
%token RETURN
%token CLASS
%token DEFINE
%token PUBLIC
%token STATIC
%token VOID
%token INT
%token BOOLEAN
%token IF
%token ELSE
%token WHILE
%token EXTENDS
%token STRING

%% 
// Grammar section.  Add your rules here.
// Example rule to parse empty classes. 
//macrojava: CLASS IDENTIFIER '{' '}' { printf ("Parsed the empty class successfully!");}
Goal: MacroDefinitionList MainClass TypeDeclarationList 

MacroDefinitionList: /*empty*/
                   | MacroDefinitionList MacroDefinition
                   ;


TypeDeclarationList: /*empty*/
                   | TypeDeclarationList TypeDeclaration
                   ;

TypeDeclaration: CLASS IDENTIFIER '{' VarDeclarationList  MethodDeclarationList '}'
               | CLASS IDENTIFIER EXTENDS IDENTIFIER '{' VarDeclarationList MethodDeclarationList '}'
               ;

VarDeclarationList: /*empty*/
                  | VarDeclarationList  Type IDENTIFIER ';'
                  | VarDeclarationList  IDENTIFIER IDENTIFIER ';'
                  ;

Param: Type IDENTIFIER
     | IDENTIFIER IDENTIFIER
     ;
ParamList: /*empty*/
         | Param
         | MidParam ',' Param
         ;

MidParam: Param
        | MidParam ',' Param
        ;

MethodDeclarationList:/*empty*/
                     | MethodDeclarationList  MethodDeclaration 
                     ;

MainClass: CLASS IDENTIFIER '{' PUBLIC STATIC VOID IDENTIFIER '(' STRING '['']' 
         IDENTIFIER')' '{' IDENTIFIER '.'IDENTIFIER'.'IDENTIFIER '(' Expression ')' ';' '}' '}'
         ;

//StatementList: /*empty*/
//             | StatementList  Statement 

StatementList: /*empty*/
             | Statement
             | MidStatement Statement
             ;

MidStatement: Statement
            | MidStatement Statement
            ;

Type: INT '['']'
    | BOOLEAN
    | INT
    ;

MethodDeclaration: PUBLIC IDENTIFIER IDENTIFIER '(' ParamList ')' '{'  VarDeclarationList StatementList RETURN Expression ';' '}'
                 | PUBLIC Type IDENTIFIER '(' ParamList ')' '{'  VarDeclarationList StatementList RETURN Expression ';' '}'
                 ;
Statement: '{' StatementList '}'
         | IDENTIFIER '=' Expression ';'
         | ArrayExpression '=' Expression ';'
         | IF '(' Expression ')' Statement
         | IF '(' Expression ')' Statement ELSE Statement
         | WHILE '(' Expression ')' Statement
         | IDENTIFIER '(' ExpressionList ')' ';' //Macro call
         | Expression '.' IDENTIFIER  '(' ExpressionList ')' ';'
         ;

ExpressionList: /*empty*/
              | Expression
              | MidExpression ',' Expression
              ;

MidExpression: Expression
             | MidExpression ',' Expression
             ;

Expression: PrimaryExpression '&' PrimaryExpression
          |	PrimaryExpression '<' PrimaryExpression
          | PrimaryExpression '+' PrimaryExpression
          | PrimaryExpression '-' PrimaryExpression
          | PrimaryExpression '*' PrimaryExpression
          | PrimaryExpression '/' PrimaryExpression
          | PrimaryExpression '.' IDENTIFIER
          | PrimaryExpression '.' IDENTIFIER '(' ExpressionList ')'
          | ArrayExpression
          | IDENTIFIER '(' ExpressionList ')'/* Macro expr call */
          | PrimaryExpression
          ;

ArrayExpression: PrimaryExpression '[' PrimaryExpression ']'

PrimaryExpression: INTVAL
                 | BOOLVAL
                 | IDENTIFIER
                 | THIS
                 | NEW INT '[' Expression ']'
                 | NEW IDENTIFIER '(' ')'
                 | '!' Expression
                 | '(' Expression ')'
                 ;

MacroDefinition: MacroDefStatement
               | MacroDefExpression
               ;

MacroDefStatement: '#' DEFINE IDENTIFIER '{' StatementList '}'
                 ;
MacroDefExpression: '#' DEFINE IDENTIFIER '(' Expression ')'
                  ;

IdentifierList: /*empty*/
              | MidIdentifier ',' IDENTIFIER
              ;

MidIdentifier: IDENTIFIER
             | MidIdentifier ',' IDENTIFIER
             ;

%%
main(){
	// parse through the input until there is no more.
	do {
		yyparse();
	} while (!feof(yyin));
}

void yyerror(const char *s){
	printf ("Parse error: %s\t%d\n" , s, yylineno)	;
    int g;
    while(!feof(yyin)){
        scanf("%d",&g);
        }
    exit(1);
}
