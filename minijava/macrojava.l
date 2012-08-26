%{
#include<stdio.h>
#include<string.h>
#include "macrojava.tab.h"
%}

%%
[ \n\t] ;
(\/\/.*\n) ;
(\/\*(.|\n)*\*\/) ;
this 	return THIS;
new	return NEW;
return 	return RETURN;
class	return CLASS;
define	return DEFINE;
public	return PUBLIC;
static	return STATIC;
void	return VOID;
int	return INT;
boolean return BOOLEAN;
if	return IF;
else	return ELSE;
while	return WHILE;
extends	return EXTENDS;
String	return STRING;
(false|true) yylval.bval = strdup(yytext); return BOOLVAL;
[a-zA-Z_][a-zA-Z0-9_]* yylval.id = strdup(yytext); return IDENTIFIER;
[#;{}\(\)\[\]\.]  return yytext[0];
[%-\/<=!\+\*\&] yylval.op = strdup(yytext); return yylval.op[0];
([+-]?[1-9][0-9]*|0) yylval.ival = atoi(yytext); return INTVAL;
%%
