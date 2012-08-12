%{
#include<stdio.h>

extern FILE* yyin;

extern int yyparse();
%}

%union{
	int ival; // integers
	char *bval;// true and false
	char  *kw; // such as class, int, boolean etc
	char *symbol; // Such as (, ), {, }, and so on
	char *op; // such as +, -, * etc
	char *id; // identifiers
}

%token <ival> INT
%token <bval> BOOLEAN
%token <kw> KEYWORD
%token <symbol> SYMBOL
%token <op> OPERATOR
%token <id> IDENTIFIER

%% 
// Grammar section. Right now it is just a 
// dummy rule to parse all the macrojava text
macrojava:

	macrojava  INT {printf ("<%s %d>", "INT", $2); }
	| macrojava BOOLEAN {printf ("<%s %s>", "BOOLEAN", $2); }
	| macrojava KEYWORD {printf ("<%s %s>", "KEYWORD", $2); }
	| macrojava SYMBOL {printf ("<%s %s>", "SYMBOL", $2); }
	| macrojava OPERATOR{printf ("<%s %s>", "OPERATOR", $2); }
	| macrojava IDENTIFIER {printf ("<%s %s>", "IDENTIFIER", $2); }
	| INT {printf ("<%s %d>", "INT", $1); }
	| BOOLEAN {printf ("<%s %s>", "BOOLEAN", $1); }
	| KEYWORD{printf ("<%s %s>", "KEYWORD", $1); }
	| SYMBOL{printf ("<%s %s>", "SYMBOL", $1); }
	| OPERATOR{printf ("<%s %s>", "OPERATOR", $1); }
	| IDENTIFIER {printf ("<%s %s>", "IDENTIFIER", $1); }
%%
main(){
	// parse through the input until there is no more.
	do {
		yyparse();
	} while (!feof(yyin));
}

void yyerror(const char *s){
	printf ("Parse error: %s\n" , s)	;
}
