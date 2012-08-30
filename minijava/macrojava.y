%{
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
extern FILE* yyin;

void yyerror(const char *s);
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

char* typedecl;
char* typedec;
char* vardecl;
char* methoddeclst;
char* methoddec;
char* typest;
char* param;
char* paraml;
char* mainclst;
char* midparam;

char* buff;
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

        typedecl = malloc( 1000 );
        typedec = malloc( 1000 );
        vardecl = malloc( 1000 );
        methoddeclst = malloc(1000 );
        methoddec = malloc( 1000 );
        typest = malloc( 1000 );
        param = malloc( 1000 );
        paraml = malloc( 1000 );
        mainclst = malloc( 1000 );
        midparam = malloc( 1000 );
        
        buff = malloc( 100000 );

        typedecl[0] = '\0';
        typedec[0] = '\0';
        vardecl[0] = '\0';
        methoddeclst[0] = '\0';
        methoddec[0] = '\0';
        typest[0] = '\0';
        param[0] = '\0';
        paraml[0] = '\0';
        mainclst[0] = '\0';
        midparam[0] = '\0';

        primexp[0] = '\0';
        temppri[0] = '\0';
        expr[0] = '\0';
        midexp[0] = '\0';
        arrayexpr[0] = '\0';
        explist[0] = '\0';

        buff[0] = '\0';

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


%type <id> TypeDeclaration
%type <id> VarDeclarationList
%type <id> Param
%type <id> ParamList
%type <id> MidParam
%type <id> MethodDeclarationList
%type <id> MethodDeclaration
%type <id> StatementList
%type <id> MidStatement
%type <id> Type
%type <id> ArrayExpression

%type <id> MidExpression



%% 
// Grammar section.  Add your rules here.
// Example rule to parse empty classes. 
//macrojava: CLASS IDENTIFIER '{' '}' { printf ("Parsed the empty class successfully!");}
Goal: MacroDefinitionList MainClass TypeDeclarationList { printf( "%s %s %s", "macros go here\n", $2, $3 ); }

MacroDefinitionList: /*empty*/
                   | MacroDefinitionList MacroDefinition
                   ;


TypeDeclarationList: /*empty*/  { strcpy( buff , " \0" ); $$ = strdup(buff); }
                   | TypeDeclarationList TypeDeclaration
                       { sprintf( buff ,"%s %s ", $1, $2 ); $$ = strdup(buff);  }
                   ;

TypeDeclaration: CLASS IDENTIFIER '{' VarDeclarationList  MethodDeclarationList '}'
        {
            sprintf( buff , "class %s { \n %s %s \n} ", $2, $4, $5 );
            $$ = strdup(buff); 
        }

               | CLASS IDENTIFIER EXTENDS IDENTIFIER '{' VarDeclarationList MethodDeclarationList '}'
        {
            sprintf( buff , "class %s extends %s { \n %s %s \n} ", $2, $4, $6, $7 ); 
            $$ = strdup(buff); 
        }

               ;

VarDeclarationList: /*empty*/ { $$ = strdup(" \0"); }
                  | VarDeclarationList  Type IDENTIFIER ';'
                      {
                          sprintf( buff , "%s %s %s ;\n", $1, $2, $3 );
                          $$ = strdup(buff);  
                      }
                  | VarDeclarationList  IDENTIFIER IDENTIFIER ';' 
                      { 
                          sprintf( buff , "%s %s %s ;\n", $1, $2, $3 );
                          $$ = strdup(buff);  
                      }
                  ;

Param: Type IDENTIFIER       { sprintf(buff, " %s %s ", $1, $2); $$ = strdup(buff); }
     | IDENTIFIER IDENTIFIER { sprintf(buff, " %s %s ", $1, $2); $$ = strdup(buff); }
     ;

ParamList: /*empty*/            { strcpy(buff, " \0");  $$ = strdup(buff);}
         | Param                { strcpy(buff, $1);  $$ = strdup(buff);}
         | MidParam ',' Param   { sprintf(buff, "%s , %s", $1, $3); $$ = strdup(buff); }
         ;

MidParam: Param                 { strcpy(buff, $1);  $$ = strdup(buff);}
        | MidParam ',' Param    { sprintf(buff, "%s , %s", $1, $3); $$ = strdup(buff); }
        ;

MethodDeclarationList:/*empty*/ 
                     {
                         strcpy(buff, " \0"); 
                         $$ = strdup(buff); 
                     }
                     | MethodDeclarationList  MethodDeclaration
                     { 
                         sprintf(buff, "%s %s", $1, $2); 
                         $$ = strdup(buff); 
                     }
                     ;

MainClass: CLASS IDENTIFIER '{' PUBLIC STATIC VOID IDENTIFIER '(' STRING '['']' 
         IDENTIFIER')' '{' IDENTIFIER '.'IDENTIFIER'.'IDENTIFIER '(' Expression ')' ';' '}' '}'
{
    if( strcmp( $15, "System" ) || strcmp( $17, "out" ) || strcmp( $19, "println" ) ) 
        yyerror( "System.out.println not found" );
    sprintf( buff, "class %s { public static void main ( String[] %s ) { System.out.println( %s ); } }", $2, $12, $21 ); 
    $$ = strdup(buff); 
} 
         ;


StatementList: /*empty*/ { strcpy( buff, " " ); $$ = strdup(buff); }
             | Statement { strcpy(buff, $1); $$ = strdup(buff); }
             | MidStatement Statement { strcpy(buff, $1 ); strcat(buff, $2);}
             ;

MidStatement: Statement { strcpy(buff, $1); $$ = strdup(buff); }
            | MidStatement Statement { sprintf(buff, "%s %s", $1, $2); }
            ;

Type: INT '['']' { strcpy( buff, "int[]\0" ); $$ = strdup(buff); }
    | BOOLEAN    { strcpy( buff, "boolean\0" );  $$ = strdup(buff);}
    | INT        { strcpy( buff, "int\0" );  $$ = strdup(buff);}
    ;

MethodDeclaration: PUBLIC IDENTIFIER IDENTIFIER '(' ParamList ')' '{'  VarDeclarationList StatementList RETURN Expression ';' '}'
{
    sprintf( buff, "public %s %s ( %s ) {\n %s %s return %s;\n }", $2, $3, $5, $8, $9, $11 ); 
}

                 | PUBLIC Type IDENTIFIER '(' ParamList ')' '{'  VarDeclarationList StatementList RETURN Expression ';' '}'
{ 
    sprintf( buff, "public %s %s ( %s ) {\n %s %s return %s;\n }", $2, $3, $5, $8, $9, $11 ); 
}
                 ;
Statement: '{' StatementList '}'
         {
             sprintf( buff, "\n{\n %s \n}\n", $2 ); $$ = strdup(buff); 
         }
         | IDENTIFIER '=' Expression ';'    
         {
             sprintf( buff, "%s = %s ;\n", $1, $3);
             $$ = strdup(buff);
         }
         | ArrayExpression '=' Expression ';'
         { 
             sprintf( buff, "%s = %s ;\n", $1, $3);  
             $$ = strdup(buff);
         }
         | IF '(' Expression ')' Statement
         {
             sprintf( buff, "if ( %s ) %s", $3, $5 );  
             $$ = strdup(buff);
         }
         | IF '(' Expression ')' Statement ELSE Statement 
         {
             sprintf( buff, "if ( %s ) %s else %s", expr, tempstm, temp );
             $$ = strdup(buff);
         }
         | WHILE '(' Expression ')' Statement 
         { 
             sprintf( buff, "while ( %s ) %s", $3, $5  );
             $$ = strdup(buff); 
         }
         | IDENTIFIER '(' ExpressionList ')' ';' //Macro call
         | Expression '.' IDENTIFIER  '(' ExpressionList ')' ';' { sprintf( buff, "%s.%s( %s )", $1, $3, $5 ); $$ = strdup(buff); }
         ;

ExpressionList: /*empty*/ { $$ = strdup(" \0");}
              | Expression { $$ = strdup($1); }
              | MidExpression ',' Expression 
              {
              strcpy(buff, $1); 
              strcat(buff, ", ");  
              strcat(buff, $3); 
              $$ = strdup(buff);
              } 
              ;

MidExpression: Expression { $$ = strdup($1); }
             | MidExpression ',' Expression 
             {
                 sprintf( buff, "%s , %s", $1, $3);
                 $$ = strdup(buff);
             }
             ;

Expression: PrimaryExpression '&'   PrimaryExpression 
          {
              sprintf(buff, "%s & %s", $1, $3);
              $$ = strdup(buff); 
          }
          |	PrimaryExpression '<'   PrimaryExpression {sprintf(buff, "%s < %s", $1, $3);  
              $$ = strdup(buff); }
          | PrimaryExpression '+'   PrimaryExpression {sprintf(buff, "%s + %s", $1, $3);  
              $$ = strdup(buff); }
          | PrimaryExpression '-'   PrimaryExpression {sprintf(buff, "%s - %s", $1, $3);  
              $$ = strdup(buff); }
          | PrimaryExpression '*'   PrimaryExpression {sprintf(buff, "%s * %s", $1, $3);  
              $$ = strdup(buff); }
          | PrimaryExpression '/'   PrimaryExpression {sprintf(buff, "%s / %s", $1, $3);  
              $$ = strdup(buff); }
          | PrimaryExpression '.' IDENTIFIER 
          { 
              sprintf(buff, "%s.%s", $1, $3); 
              $$ = strdup(buff); 
          }
          | PrimaryExpression '.' IDENTIFIER '('  ExpressionList ')' 
          {
              sprintf( buff, "%s.( %s)", $1, $3, $5);
              $$ = strdup(buff);
          }
          | ArrayExpression             { $$ = strdup($1); }
          | IDENTIFIER '(' ExpressionList ')'/* Macro expr call */
          | Expression '+' PrimaryExpression 
          {
              sprintf( buff, "%s + %s", $1, $3);
              $$ = strdup(buff);
          }
          | PrimaryExpression 
          {
              strcpy( buff, $1);
              $$ = strdup(buff);
          }
          ;

ArrayExpression: PrimaryExpression '[' PrimaryExpression ']' { sprintf( buff, "%s[%s]", $1, $3) ; $$ = strdup(buff); }

PrimaryExpression: INTVAL           {sprintf(buff, "%d", $1); $$ = strdup(buff); }
                 | BOOLVAL          {strcpy(buff, $1); $$ = strdup(buff);} 
                 | IDENTIFIER       {strcpy(buff, $1); $$ = strdup(buff);} 
                 | THIS             {strcpy(buff, "this\0");  $$ = strdup(buff);}
                 | NEW INT '[' Expression ']' { sprintf(buff, "new int [ %s ]", $4); $$ = strdup(buff);}
                 | NEW IDENTIFIER '(' ')'     { sprintf(buff, "new %s()",$2); $$ = strdup(buff);}
                 | '!' Expression             { sprintf(buff, "! %s ", $2); $$ = strdup(buff);}
                 | '(' Expression ')'         { sprintf(buff, "( %s )", $2); $$ = strdup(buff);}
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
