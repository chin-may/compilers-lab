%{
#include "macrojava.tab.h"
#include <stdlib.h>
%}
DIGIT [0-9]+ 
OPERATOR [-!+=/\*\%\|\&<>]
KEYWORD  abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|define|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while
BOOLEAN true|false
SYMBOL [#\.\,\(\)\[\]\;\{\}:]
IDENTIFIER [a-zA-Z_$][a-zA-Z0-9_]*
%%
\/\/[^\r\n]* ;
\/\*(?:[^\*]|\*[^/])*\*\/ ; //For ignoring comments
{DIGIT} {yylval.ival = atoi(yytext); 
    return INT;}
{BOOLEAN} {yylval.bval = strdup(yytext);
    return BOOLEAN;}
{OPERATOR} {yylval.op = strdup(yytext);
    return OPERATOR;}
{KEYWORD}  {yylval.kw = strdup(yytext);
    return KEYWORD;}
{IDENTIFIER} {yylval.id = strdup(yytext);
    return IDENTIFIER;}
{SYMBOL} {yylval.symbol = strdup(yytext);
    return SYMBOL;}
[ \n\r\t] ;
%%
