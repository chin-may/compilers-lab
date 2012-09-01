%{
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
extern FILE* yyin;

void yyerror(const char *s);
char* replace(char* sentence, char* word, char* altword);
extern int yyparse();
extern int yylineno;
int yydebug = 1;

char* temp;
char* buff;

int currMac;
int currident;
char* macros[100];
char* macroname[100];
int macroNum;
int currExpMac[10];
int currExpParam[10];
char* expParams[10][100];

char* macIdents[50][10];
int* macIdentNum;
int depth;
%}


%initial-action{   
       buff = malloc( 100000 );
       buff[0] = '\0';
       temp = malloc( 100000 );
       temp[0] = '\0';
       currMac = 0;
       currident = 0;
       macIdentNum = calloc(100,1);
       macroNum = 0;
       depth = -1;
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


%type <id> MacParamList
%type <id> MidMacExpression
%type <id> MacStatementList
%type <id> MacMidStatement
%type <id> MacStatement
%type <id> MacMidExpression
%type <id> MacExpression
%type <id> MacArrayExpression
%type <id> MacPrimaryExpression
%type <id> MacExpressionList

%% 
Goal: MacroDefinitionList MainClass TypeDeclarationList 
    {
        printf("// Macrojava code parsed and minijava code generated successfully.\n");
        printf( "%s\n %s", $2, $3 ); 
    }

MacroDefinitionList: /*empty*/
                   | MacroDefinitionList MacroDefinition
                   {
                     macroNum++;
                   }
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
             | MidStatement Statement { strcpy(buff, $1 ); strcat(buff, $2); $$ = strdup(buff); }
             ;

MidStatement: Statement { strcpy(buff, $1); $$ = strdup(buff); }
            | MidStatement Statement { sprintf(buff, "%s %s", $1, $2); $$ = strdup(buff);}
            ;

Type: INT '['']' { strcpy( buff, "int[]\0" ); $$ = strdup(buff); }
    | BOOLEAN    { strcpy( buff, "boolean\0" );  $$ = strdup(buff);}
    | INT        { strcpy( buff, "int\0" );  $$ = strdup(buff);}
    ;

MethodDeclaration: PUBLIC IDENTIFIER IDENTIFIER '(' ParamList ')' '{'  VarDeclarationList StatementList RETURN Expression ';' '}'
        {
            sprintf( buff, "public %s %s ( %s ) {\n %s %s return %s;\n }", $2, $3, $5, $8, $9, $11 ); 
            $$ = strdup( buff );
        }

        | PUBLIC Type IDENTIFIER '(' ParamList ')' '{'  VarDeclarationList StatementList RETURN Expression ';' '}'
        { 
            sprintf( buff, "public %s %s ( %s ) {\n %s %s return %s;\n }", $2, $3, $5, $8, $9, $11 ); 
            $$ = strdup( buff );
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
             sprintf( buff, "if ( %s ) %s else %s", $3, $5, $7 );
             $$ = strdup(buff);
         }
         | WHILE '(' Expression ')' Statement 
         { 
             sprintf( buff, "while ( %s ) %s", $3, $5  );
             $$ = strdup(buff); 
         }
         | IDENTIFIER  CodeNonTerm '(' MacParamList ')' ';' //Macro call
         {  
            int i;
            char* expandedMac;
            char* prevexp;
            expandedMac = strdup(macros[currExpMac[depth]]);
            for(i = 0; i < currExpParam[depth]; i++){
                sprintf(temp, "$MacIdent$%d", i);
                prevexp = expandedMac;
                expandedMac = replace(expandedMac, temp, expParams[depth][i]);
                free(prevexp);
            }
            $$ = strdup(expandedMac);
            depth--;
             
         }

         | Expression '.' IDENTIFIER  '(' ExpressionList ')' ';' 
         {
             if((strcmp($1, "System.out") != 0) || (strcmp($3, "println") != 0))
                yyerror("println wrong");
             else{
             sprintf( buff, "%s.%s( %s );\n", $1, $3, $5 );
             $$ = strdup(buff);
             }
         }
         ;

CodeNonTerm: /*empty*/
            {
                depth++;
                currExpMac[depth] = macroFind($<id>0); 
                if(currExpMac[depth] < 0) yyerror("Macro doesnt exist");
                currExpParam[depth] = 0;
            }

MacParamList: /*empty*/ { $$ = strdup(" ");}
              | Expression 
              {
                  $$ = strdup($1); 
                  expParams[depth][currExpParam[depth]] = strdup($1);
                  currExpParam[depth]++;
              }
              | MidMacExpression ',' Expression 
              {
                  strcpy(buff, $1); 
                  strcat(buff, ", ");  
                  strcat(buff, $3); 
                  $$ = strdup(buff);
                  expParams[depth][currExpParam[depth]] = strdup($3);
                  currExpParam[depth]++;
              } 
              ;


MidMacExpression: Expression
              {
                  $$ = strdup($1); 
                  expParams[depth][currExpParam[depth]] = strdup($1);
                  currExpParam[depth]++;
              }
             | MidMacExpression ',' Expression 
             {
                 sprintf( buff, "%s , %s", $1, $3);
                 $$ = strdup(buff);
                 expParams[depth][currExpParam[depth]] = strdup($3);
                 currExpParam[depth]++;
             }
             ;
ExpressionList: /*empty*/ { $$ = strdup(" ");}
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
          |	PrimaryExpression '<'   PrimaryExpression 
          {
              sprintf(buff, "%s < %s", $1, $3);  
              $$ = strdup(buff);
          }
          | PrimaryExpression '+'   PrimaryExpression 
          {
              sprintf(buff, "%s + %s", $1, $3);  
              $$ = strdup(buff);
          }
          | PrimaryExpression '-'   PrimaryExpression 
          {
              sprintf(buff, "%s - %s", $1, $3);  
              $$ = strdup(buff);
          }
          | PrimaryExpression '*'   PrimaryExpression 
          {
              sprintf(buff, "%s * %s", $1, $3);  
              $$ = strdup(buff);
          }
          | PrimaryExpression '/'   PrimaryExpression 
          {
              sprintf(buff, "%s / %s", $1, $3);  
              $$ = strdup(buff);
          }
          | PrimaryExpression '.' IDENTIFIER 
          { 
              sprintf(buff, "%s.%s", $1, $3); 
              $$ = strdup(buff); 
          }
          | PrimaryExpression '.' IDENTIFIER '('  ExpressionList ')' 
          {
              sprintf( buff, "%s.%s( %s)", $1, $3, $5);
              $$ = strdup(buff);
          }
          | ArrayExpression             { $$ = strdup($1); }
          | IDENTIFIER  CodeNonTerm '(' MacParamList ')'/* Macro expr call */

         {  
            int i;
            char* expandedMac;
            char* prevexp;
            expandedMac = strdup(macros[currExpMac[depth]]);
            
            for(i = 0; i < currExpParam[depth]; i++){
                sprintf(temp, "$MacIdent$%d", i);
                prevexp = expandedMac;
                expandedMac = replace(expandedMac, temp, expParams[depth][currExpParam[depth]]);
                free(prevexp);
            }
            $$ = strdup(expandedMac);
            depth--;
             
         }
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

MacroDefStatement: '#' DEFINE IDENTIFIER '(' IdentifierList ')' '{' MacStatementList '}'
                 {
                    macroname[currMac] = strdup($3);
                    macros[currMac] = strdup($8);
                    currMac++;
                 }
                 ;
MacroDefExpression: '#' DEFINE IDENTIFIER '(' IdentifierList ')' '(' Expression ')'
                  {
                    macroname[currMac] = strdup($3);
                    macros[currMac] = strdup($8);
                    currMac++;
                  }
                  ;

IdentifierList: /*empty*/
              {
                macIdentNum[currMac] = 0;
              }
              | IDENTIFIER
              {
                macIdents[currMac][currident] = strdup($1);
                currident++;
                macIdentNum[currMac]++;
              }
              | MidIdentifier ',' IDENTIFIER
              {
                macIdents[currMac][currident] = strdup($3);
                currident++;
                macIdentNum[currMac]++;
              }
              ;

MidIdentifier: IDENTIFIER
              {
                macIdents[currMac][currident] = strdup($1);
                currident++;
                macIdentNum[currMac]++;
              }
             | MidIdentifier ',' IDENTIFIER
              {
                macIdents[currMac][currident] = strdup($3);
                currident++;
                macIdentNum[currMac]++;
              }
             ;

MacStatementList: /*empty*/ { strcpy( buff, " " ); $$ = strdup(buff); }
             | MacStatement { strcpy(buff, $1); $$ = strdup(buff); }
             | MacMidStatement MacStatement { strcpy(buff, $1 ); strcat(buff, $2); $$ = strdup(buff); }
             ;

MacMidStatement: MacStatement { strcpy(buff, $1); $$ = strdup(buff); }
            | MacMidStatement MacStatement { sprintf(buff, "%s %s", $1, $2); $$ = strdup(buff);}
            ;

MacStatement: '{' MacStatementList '}'
         {
             sprintf( buff, "\n{\n %s \n}\n", $2 ); $$ = strdup(buff); 
         }
         | IDENTIFIER '=' MacExpression ';'    
         {
             int check;
             check = replaceCheck($1);
             if(check>=0){
                 sprintf( buff, "$MacIdent$%d = %s ;\n", check, $3);
                 $$ = strdup(buff);
             }
             else{
                 sprintf( buff, "%s = %s ;\n", $1, $3);
                 $$ = strdup(buff);
             }
         }
         | MacArrayExpression '=' MacExpression ';'
         { 
             sprintf( buff, "%s = %s ;\n", $1, $3);  
             $$ = strdup(buff);
         }
         | IF '(' MacExpression ')' MacStatement
         {
             sprintf( buff, "if ( %s ) %s", $3, $5 );  
             $$ = strdup(buff);
         }
         | IF '(' MacExpression ')' MacStatement ELSE MacStatement 
         {
             sprintf( buff, "if ( %s ) %s else %s", $3, $5, $7 );
             $$ = strdup(buff);
         }
         | WHILE '(' MacExpression ')' MacStatement 
         { 
             sprintf( buff, "while ( %s ) %s", $3, $5  );
             $$ = strdup(buff); 
         }
         | IDENTIFIER '(' MacExpressionList ')' ';' //Macro call
         {  
            int i;
            char* expandedMac;
            char* prevexp;
            expandedMac = strdup(macros[currExpMac[depth]]);
            for(i = 0; i < currExpParam[depth]; i++){
                sprintf(temp, "$MacIdent$%d", i);
                prevexp = expandedMac;
                expandedMac = replace(expandedMac, temp, expParams[depth][i]);
                free(prevexp);
            }
            $$ = strdup(expandedMac);
            depth--;
             
         }

         | MacExpression '.' IDENTIFIER  '(' MacExpressionList ')' ';' 
         {
             if((strcmp($1, "System.out") != 0) || (strcmp($3, "println") != 0))
                yyerror("println wrong");
             else{
             sprintf( buff, "%s.%s( %s );\n", $1, $3, $5 );
             $$ = strdup(buff);
             }
         }
         ;


MacExpressionList: /*empty*/ { $$ = strdup(" ");}
              | MacExpression { $$ = strdup($1); }
              | MacMidExpression ',' MacExpression 
              {
                  strcpy(buff, $1); 
                  strcat(buff, ", ");  
                  strcat(buff, $3); 
                  $$ = strdup(buff);
              } 
              ;

MacMidExpression: MacExpression { $$ = strdup($1); }
             | MacMidExpression ',' MacExpression 
             {
                 sprintf( buff, "%s , %s", $1, $3);
                 $$ = strdup(buff);
             }
             ;

MacExpression: MacPrimaryExpression '&'   MacPrimaryExpression 
          {
              sprintf(buff, "%s & %s", $1, $3);
              $$ = strdup(buff); 
          }
          |	MacPrimaryExpression '<'   MacPrimaryExpression 
          {
              sprintf(buff, "%s < %s", $1, $3);  
              $$ = strdup(buff);
          }
          | MacPrimaryExpression '+'   MacPrimaryExpression 
          {
              sprintf(buff, "%s + %s", $1, $3);  
              $$ = strdup(buff);
          }
          | MacPrimaryExpression '-'   MacPrimaryExpression 
          {
              sprintf(buff, "%s - %s", $1, $3);  
              $$ = strdup(buff);
          }
          | MacPrimaryExpression '*'   MacPrimaryExpression 
          {
              sprintf(buff, "%s * %s", $1, $3);  
              $$ = strdup(buff);
          }
          | MacPrimaryExpression '/'   MacPrimaryExpression 
          {
              sprintf(buff, "%s / %s", $1, $3);  
              $$ = strdup(buff);
          }
          | MacPrimaryExpression '.' IDENTIFIER 
          { 
              sprintf(buff, "%s.%s", $1, $3); 
              $$ = strdup(buff); 
          }
          | MacPrimaryExpression '.' IDENTIFIER '('  MacExpressionList ')' 
          {
              sprintf( buff, "%s.%s( %s)", $1, $3, $5);
              $$ = strdup(buff);
          }
          | MacArrayExpression             { $$ = strdup($1); }
          | IDENTIFIER '(' MacExpressionList ')'/* Macro expr call */

           {  
            int i;
            char* expandedMac;
            char* prevexp;
            expandedMac = strdup(macros[currExpMac[depth]]);
            
            for(i = 0; i < currExpParam[depth]; i++){
                sprintf(temp, "$MacIdent$%d", i);
                prevexp = expandedMac;
                expandedMac = replace(expandedMac, temp, expParams[depth][currExpParam[depth]]);
                free(prevexp);
            }
            $$ = strdup(expandedMac);
            depth--;
            }
             
    
          | MacExpression '+' MacPrimaryExpression 
          {
              sprintf( buff, "%s + %s", $1, $3);
              $$ = strdup(buff);
          }
          | MacPrimaryExpression 
          {
              strcpy( buff, $1);
              $$ = strdup(buff);
          }
          ; 

MacArrayExpression: MacPrimaryExpression '[' MacPrimaryExpression ']' { sprintf( buff, "%s[%s]", $1, $3) ; $$ = strdup(buff); }

MacPrimaryExpression: INTVAL           
                    {
                        sprintf(buff, "%d", $1);
                        $$ = strdup(buff);
                    }
                     | BOOLVAL          
                     {
                         strcpy(buff, $1);
                         $$ = strdup(buff);
                     } 
                     | IDENTIFIER       
                     {
                         int check;
                         check = replaceCheck($1);
                         if(check>=0){
                             sprintf( buff, "$MacIdent$%d", check);
                             $$ = strdup(buff);
                         }
                         else{
                             strcpy(buff, $1);
                             $$ = strdup(buff);
                         }
                     } 
                     | THIS             
                     {
                         strcpy(buff, "this\0");
                         $$ = strdup(buff);
                     }
                     | NEW INT '[' MacExpression ']' 
                     {
                         sprintf(buff, "new int [ %s ]", $4);
                         $$ = strdup(buff);
                     }
                     | NEW IDENTIFIER '(' ')'     
                     {
                         sprintf(buff, "new %s()",$2);
                         $$ = strdup(buff);
                     }
                     | '!' MacExpression             
                     {
                         sprintf(buff, "! %s ", $2);
                         $$ = strdup(buff);
                     }
                     | '(' MacExpression ')'         
                     {
                         sprintf(buff, "( %s )", $2);
                         $$ = strdup(buff);
                     }
                     ;

%%
main(){
	// parse through the input until there is no more.
	do {
		yyparse();
	} while (!feof(yyin));
}

void yyerror(const char *s){
	//printf ("Parse error: %s\t%d\n" , s, yylineno)	;
    printf("// Failed to parse macrojava code.\n");
    exit(1);
}


char* replace(char* sentence, char* word, char* altword) {
    char* dest = malloc(2 * strlen(sentence));
    dest[0] = '\0';
    char* currpos;
    char* prevpos;
    int wordlen = strlen(word);
    int sentlen = strlen(sentence);
    currpos = strstr(sentence, word);
    prevpos = sentence;
    while(currpos){
        strncat(dest, prevpos, currpos - prevpos );
        strcat(dest, altword);
        currpos += wordlen;
        if(currpos >= sentence + sentlen) return dest;
        prevpos = currpos;
        currpos = strstr(currpos, word);
    }
    strcat(dest, prevpos);
    return dest;
}

int replaceCheck(char* ident){
    int i;
    for(i = 0; i < macIdentNum[currMac]; i++){
        if(strcmp(ident, macIdents[currMac][i]) == 0 )
            return i;
    }
    return -1;
}

int macroFind(char* ident){
    int i;
    for(i = 0; i < macroNum; i++){
        if(strcmp(ident, macroname[i]) == 0 )
            return i;
    }
    return -1;
}
