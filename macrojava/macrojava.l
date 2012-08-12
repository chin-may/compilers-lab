%{
#include "macrojava.tab.h"
#include <stdlib.h>
%}
%%
\/\/[^\r\n]* ;
[0-9]+ {yylval.ival = atoi(yytext); return INT;}
true|false {yylval.bval = strdup(yytext); return BOOLEAN;}
[-!+=/\*\%\|\&<>] {yylval.op = strdup(yytext); return OPERATOR;}
return|else|this|static|void|new|class|int|boolean|if|while|public|private|define|char {yylval.kw = strdup(yytext); return KEYWORD;}
[a-zA-Z][a-zA-Z0-9_]* {yylval.id = strdup(yytext); return IDENTIFIER;}
[#\.\,\(\)\[\]\;\{\}:] {yylval.symbol = strdup(yytext); return SYMBOL;}
[ \n\r\t] ;
%%
