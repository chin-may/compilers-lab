%{
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
extern FILE* yyin;

extern int yyparse();
extern int yylineno;
int yydebug = 1;

char* curr;
char* temp;
char* primexp;
char* expr;
char* explist;
char midexp[10000];
char midexp[10000];
char temppri[10000];
char arrayexpr[10000];
char tempstm[10000];
char stmnt[10000];
%}


%initial-action
{   
        curr = malloc(10000000);
        curr[0] = '\0';
        temp = malloc(1000000);
        primexp = malloc(1000);
        expr = malloc(1000);
        primexp[0] = '\0';
        temppri[0] = '\0';
        expr[0] = '\0';
        midexp[0] = '\0';
        arrayexpr[0] = '\0';

};


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
%token <id> THIS
%token <id> NEW
%token <id> RETURN
%token <id> CLASS
%token <id> DEFINE
%token <id> PUBLIC
%token <id> STATIC
%token <id> VOID
%token <id> INT
%token <id> BOOLEAN
%token <id> IF
%token <id> ELSE
%token <id> WHILE
%token <id> EXTENDS
%token <id> STRING

%type <id> MacroDefinitionList
%type <id> MainClass
%type <id> TypeDeclarationList
%type <id> Statement
%type <id> Expression
%type <id> PrimaryExpression
%type <id> ExpressionList



%% 
// Grammar section.  Add your rules here.
// Example rule to parse empty classes. 
//macrojava: CLASS IDENTIFIER '{' '}' { printf ("Parsed the empty class successfully!");}
Goal: MacroDefinitionList MainClass TypeDeclarationList //{printf("%s",curr);}

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
Statement: '{' StatementList '}' { printf( "{\n %s \n}", stmnt ); }
         | IDENTIFIER '=' Expression ';'    { sprintf( stmnt, "%s = %s", $1, expr); }
         | ArrayExpression '=' Expression ';' { sprintf( stmnt, "%s = %s", arrayexpr, expr); }
         | IF '(' Expression ')' Statement   { strcpy( temp, stmnt );  sprintf( stmnt, "if ( %s ) %s", expr, temp ); }
         | IF '(' Expression ')' Statement ELSE { strcpy( tempstm, stmnt ); } 
             Statement { strcpy( temp, stmnt );  sprintf( stmnt, "if ( %s ) %s else %s", expr, tempstm, temp ); }
         | WHILE '(' Expression ')' Statement { strcpy( temp, stmnt );  sprintf( stmnt, "while ( %s ) %s", expr, temp ); }
         | IDENTIFIER '(' ExpressionList ')' ';' //Macro call
         | Expression '.' IDENTIFIER  '(' ExpressionList ')' ';'
         ;

ExpressionList: /*empty*/
              | Expression { strcpy(explist, expr); }
              | MidExpression ',' Expression { strcpy(explist, midexp); strcat(explist, ", ");  strcat(explist, expr);} 
              ;

MidExpression: Expression { strcpy(midexp, $1); }
             | MidExpression ',' Expression { strcat(midexp, ", "); strcat(midexp, expr);}
             ;

Expression: PrimaryExpression '&' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s & %s",temppri, primexp);}
          |	PrimaryExpression '<' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s < %s",temppri, primexp);}
          | PrimaryExpression '+' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s + %s",temppri, primexp);}
          | PrimaryExpression '-' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s - %s",temppri, primexp);}
          | PrimaryExpression '*' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s * %s",temppri, primexp);}
          | PrimaryExpression '/' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s / %s",temppri, primexp);}
          | PrimaryExpression '.' IDENTIFIER { sprintf(expr, "%s.%s", primexp, $3); }
          | PrimaryExpression '.' IDENTIFIER '(' {strcpy(temppri, primexp); } ExpressionList ')' { sprintf( expr, "%s.%s( %s)", temppri, $3, $6); }
          | ArrayExpression             { strcpy( expr, arrayexpr); }
          | IDENTIFIER '(' ExpressionList ')'/* Macro expr call */
          | Expression '+' PrimaryExpression { sprintf( expr, "%s + %s", expr, primexp); }
          | PrimaryExpression { strcpy( expr, primexp); }
          ;

ArrayExpression: PrimaryExpression '[' {strcpy(temppri, primexp);} PrimaryExpression ']' { sprintf( arrayexpr, "%s[%s]", temppri, $4) ; }

PrimaryExpression: INTVAL           {sprintf(primexp, "%d"); }
                 | BOOLVAL          {strcpy(primexp, $1);} 
                 | IDENTIFIER       {strcpy(primexp, $1);} 
                 | THIS             {strcpy(primexp, $1);}
                 | NEW INT '[' Expression ']' { sprintf(primexp, "new int [ %s ]", $4);}
                 | NEW IDENTIFIER '(' ')'     { sprintf(primexp, "new %s()",$2);}
                 | '!' Expression             { sprintf(primexp, "! %s ", $2);}
                 | '(' Expression ')'         { sprintf(primexp, "( %s )", $2);}
                 ;

MacroDefinition: MacroDefStatement
               | MacroDefExpression
               ;

MacroDefStatement: '#' DEFINE IDENTIFIER '(' IdentifierList ')' '{' StatementList '}'
                 ;
MacroDefExpression: '#' DEFINE IDENTIFIER '(' IdentifierList ')' '(' Expression ')'
                  ;

IdentifierList: /*empty*/
              | IDENTIFIER
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
