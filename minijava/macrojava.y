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
char* midexp;
char* temppri;
char* arrayexpr;
char* tempstm;
char* stmnt;
char* stmlist;
char* midstm;
%}


%initial-action
{   
        curr = malloc(10000000);
        curr[0] = '\0';
        temp = malloc(1000000);
        primexp = malloc(1000);
        expr = malloc(1000);
        explist = malloc(1000);
        midexp = malloc(1000);
        temppri = malloc(1000);
        arrayexpr = malloc(1000);
        tempstm = malloc(1000);
        stmnt = malloc(1000);
        stmlist = malloc(1000);
        midstm = malloc(1000);


        primexp[0] = '\0';
        temppri[0] = '\0';
        expr[0] = '\0';
        midexp[0] = '\0';
        arrayexpr[0] = '\0';
        explist[0] = '\0';

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
%token <kw> THIS
%token <kw> NEW
%token <kw> RETURN
%token <kw> CLASS
%token <kw> DEFINE
%token <kw> PUBLIC
%token <kw> STATIC
%token <kw> VOID
%token <kw> INT
%token <kw> BOOLEAN
%token <kw> IF
%token <kw> ELSE
%token <kw> WHILE
%token <kw> EXTENDS
%token <kw> STRING

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

{   if( strcmp( $15, "System" ) || strcmp( $17, "out" ) || strcmp( $19, "println" ) ) yyerror( "System.out.println not found" );
    sprintf( mainclst, "class %s { public static void main ( String[] %s ) { System.out.println( %s ); } }", $2, $12, expr ); } 
         ;


StatementList: /*empty*/ { strcpy( stmlist, " \0" ); }
             | Statement { strcpy(stmlist, stmnt); }
             | MidStatement Statement { strcpy(stmlist, midstm ); strcat(stmlist, stmnt);}
             ;

MidStatement: Statement { strcpy(midstm, stmnt); }
            | MidStatement Statement { strcat(midstm, stmnt );}
            ;

Type: INT '['']' { strcpy( typest, "int[]\0" ); }
    | BOOLEAN    { strcpy( typest, "boolean\0" ); }
    | INT        { strcpy( typest, "int\0" ); }
    ;

MethodDeclaration: PUBLIC IDENTIFIER IDENTIFIER '(' ParamList ')' '{'  VarDeclarationList StatementList RETURN Expression ';' '}'
{ sprintf( methoddec, "public %s %s ( %s ) {\n %s %s return %s;\n }", $2, $3, paraml, vardecl, stmlist, expr ); }
                 | PUBLIC Type IDENTIFIER '(' ParamList ')' '{'  VarDeclarationList StatementList RETURN Expression ';' '}'
{ sprintf( methoddec, "public %s %s ( %s ) {\n %s %s return %s;\n }", typest, $3, paraml, vardecl, stmlist, expr ); }
                 ;
Statement: '{' StatementList '}' { sprintf( stmnt, "\n{\n %s \n}\n", stmlist ); }
         | IDENTIFIER '=' Expression ';'    { sprintf( stmnt, "%s = %s ;\n", $1, expr);  }
         | ArrayExpression '=' Expression ';' { sprintf( stmnt, "%s = %s ;\n", arrayexpr, expr); }
         | IF '(' Expression ')' Statement   { strcpy( temp, stmnt );  sprintf( stmnt, "if ( %s ) %s", expr, temp ); }
         | IF '(' Expression ')' Statement ELSE { strcpy( tempstm, stmnt ); } 
             Statement { strcpy( temp, stmnt );  sprintf( stmnt, "if ( %s ) %s else %s", expr, tempstm, temp ); }
         | WHILE '(' Expression ')' Statement { strcpy( temp, stmnt );  sprintf( stmnt, "while ( %s ) %s", expr, temp ); }
         | IDENTIFIER '(' ExpressionList ')' ';' //Macro call
         | Expression '.' IDENTIFIER  '(' { strcpy( temp, expr ); } ExpressionList ')' ';' { sprintf( stmnt, "%s.%s( %s )", temp, $3, explist ); }
         ;

ExpressionList: /*empty*/ { strcpy(explist, " \0");}
              | Expression { strcpy(explist, expr); }
              | MidExpression ',' Expression { strcpy(explist, midexp); strcat(explist, ", ");  strcat(explist, expr);} 
              ;

MidExpression: Expression { strcpy(midexp, expr); }
             | MidExpression ',' Expression { strcat(midexp, ", "); strcat(midexp, expr);}
             ;

Expression: PrimaryExpression '&' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s & %s",temppri, primexp);  }
          |	PrimaryExpression '<' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s < %s",temppri, primexp);  }
          | PrimaryExpression '+' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s + %s",temppri, primexp);  }
          | PrimaryExpression '-' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s - %s",temppri, primexp);  }
          | PrimaryExpression '*' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s * %s",temppri, primexp);  }
          | PrimaryExpression '/' {strcpy(temppri, primexp); }  PrimaryExpression {sprintf(expr, "%s / %s",temppri, primexp);  }
          | PrimaryExpression '.' IDENTIFIER { sprintf(expr, "%s.%s", primexp, $3); }
          | PrimaryExpression '.' IDENTIFIER '(' {strcpy(temppri, primexp); } ExpressionList ')' {sprintf( expr, "%s.( %s)", temppri, $3, explist); }
          | ArrayExpression             { strcpy( expr, arrayexpr); }
          | IDENTIFIER '(' ExpressionList ')'/* Macro expr call */
          | Expression '+' PrimaryExpression { sprintf( expr, "%s + %s", expr, primexp); }
          | PrimaryExpression { strcpy( expr, primexp); }
          ;

ArrayExpression: PrimaryExpression '[' {strcpy(temppri, primexp);} PrimaryExpression ']' { sprintf( arrayexpr, "%s[%s]", temppri, primexp) ; }

PrimaryExpression: INTVAL           {sprintf(primexp, "%d", $1); }
                 | BOOLVAL          {strcpy(primexp, $1);} 
                 | IDENTIFIER       {strcpy(primexp, $1);} 
                 | THIS             {strcpy(primexp, "this\0"); }
                 | NEW INT '[' Expression ']' { sprintf(primexp, "new int [ %s ]", expr);}
                 | NEW IDENTIFIER '(' ')'     { sprintf(primexp, "new %s()",$2);}
                 | '!' Expression             { sprintf(primexp, "! %s ", expr);}
                 | '(' Expression ')'         { sprintf(primexp, "( %s )", expr);}
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
