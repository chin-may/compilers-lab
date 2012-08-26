all: macrojava.l macrojava.y macrojava 

macrojava: macrojava.tab.c macrojava.tab.h lex.yy.c
	gcc macrojava.tab.c lex.yy.c -lfl -o macrojava

macrojava.tab.c macrojava.tab.h: macrojava.y
	bison -dtv macrojava.y

lex.yy.c: macrojava.tab.c macrojava.tab.h macrojava.l
	flex macrojava.l

clean:
	rm -f macrojava macrojava.tab.* lex.yy.c
