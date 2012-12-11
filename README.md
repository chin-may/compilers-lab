compilers-lab
=============

A compiler for the MiniJava language.
Can also expand macros.

Written for the CS3310 course at IIT Madras
Assignment questions are from:  
www.cs.ucla.edu/~palsberg/course/cs132/project.html  
www.cse.iitm.ac.in/~krishna/cs3300/cs3310.html

Each folder has a program that takes input and produces the next stage in the
compilation.
All input output uses standard input output.  
To produce MIPS assembly code from macrotest.java :  

macrojava/macrojava < macrotest.java > test.java

java miniir/Simplify < test.java > test.miniir

java microir/Simplify2 < test.miniir > test.microir

java minira/RegAlloc < test.microir > test.minira

java mips/Codegen < test.minira > test.s
