%{
	#include <stdlib.h>
	#include "macrojava.tab.h"
%}

%%

[ \n\t] ;
(\/\/.*\n) ;
(\/\*(.|\n)*\*\/) ;
(this|new|return|class|define|public|static|void|int|boolean|if|else|while|extends) yylval.kw = yytext; return KEYWORD;
(false|true) yylval.bval = yytext; return BOOLEAN;
[a-zA-Z_][a-zA-Z0-9_]* yylval.id = yytext; return IDENTIFIER;
[#;{}\(\)\[\]\.] yylval.symbol = yytext; return SYMBOL;
[%-\/<=!\+\*\&] yylval.op = yytext; return OPERATOR;
([+-]?[1-9][0-9]*|0) yylval.ival = atoi(yytext); return INT;

%%
