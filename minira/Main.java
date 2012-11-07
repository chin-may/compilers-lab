import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import syntaxtree.*;
import visitor.*;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, ParseException {
	//	try {
			// use the following line only in IDE
			 Node root = new microIRParser(new
			 FileInputStream("/home/chinmay/dev/compilers-lab/temp/ra/MoreThan4.microIR")).Goal();

			// Final submission file should take input from command line
			//Node root = new MiniIRParser(System.in).Goal();

		//	System.out.println("Program parsed successfully");
			 root.accept(new SucSetter());
			root.accept(new Generator()); 
			
	//	} //catch (Throwable e) {
		//	System.out.println(e.toString());
		//}
	}
}
