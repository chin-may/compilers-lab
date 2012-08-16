%{
#include "macrojava.tab.h"
#include <stdlib.h>
%}
%%
\/\/[^\r\n]* ;
\/\*(?:[^\*]|\*[^/])*\*\/ ;
[0-9]+ {yylval.ival = atoi(yytext); return INT;}
true|false {yylval.bval = strdup(yytext); return BOOLEAN;}
[-!+=/\*\%\|\&<>] {yylval.op = strdup(yytext); return OPERATOR;}
super|throw|throws|try|catch|case|switch|return|else|this|static|void|new|class|int|boolean|if|while|public|private|protected|define|char {yylval.kw = strdup(yytext); return KEYWORD;}
[a-zA-Z][a-zA-Z0-9_]* {yylval.id = strdup(yytext); return IDENTIFIER;}
[#\.\,\(\)\[\]\;\{\}:] {yylval.symbol = strdup(yytext); return SYMBOL;}
[ \n\r\t] ;
%%
