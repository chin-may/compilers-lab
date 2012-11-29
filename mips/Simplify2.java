import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import syntaxtree.*;
import visitor.*;

public class Simplify2 {
	public static void main(String[] args) throws FileNotFoundException, ParseException {
	//	try {
			// use the following line only in IDE
			 Node root = new MiniRAParser(new
			 FileInputStream("/home/chinmay/dev/compilers-lab/temp/Factorial.minira")).Goal();

			// Final submission file should take input from command line
			//Node root = new MiniIRParser(System.in).Goal();

		//	System.out.println("Program parsed successfully");
			 //root.accept(new OrigGJ());
			GJNoArguDepthFirst gjv = new GJNoArguDepthFirst();
			root.accept(gjv); 
			
	//	} //catch (Throwable e) {
		//	System.out.println(e.toString());
		//}
	}
}
