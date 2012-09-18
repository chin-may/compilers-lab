import java.io.FileInputStream;
import java.io.InputStream;

import syntaxtree.*;
import visitor.*;

public class Main {
	public static void main(String[] args) {
		try {
			// use the following line only in IDE
			 Node root = new MiniJavaParser(new
			 FileInputStream("/home/chinmay/Downloads/Test.java")).Goal();

			// Final submission file should take input from command line
			//Node root = new MiniJavaParser(System.in).Goal();

			System.out.println("Program parsed successfully");
			GJNoArguDepthFirst gjv = new GJNoArguDepthFirst();
			root.accept(gjv); // Your assignment part is
													// invoked here.
			gjv = null;
			

		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
