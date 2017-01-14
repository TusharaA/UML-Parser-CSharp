
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiagramGenerator {

	static InputStream inputStream;
	static String classInterfaceOutput = "";
	static ArrayList<String> classNames = new ArrayList<>();

	public static void main(String[] args) throws Exception {

		RelationParser relParser = new RelationParser();  //to add relations
		
		File folder = new File(args[0]);
		File[] listOfFiles = folder.listFiles();
		
		 for (File file : listOfFiles) {
			if (file.isFile() && file.getName().contains(".cs")) {
			String name = file.getName().substring(0, file.getName().length() - 3);
			classNames.add(name);
			}
		}
	
		for (File file : listOfFiles){
			if (file.isFile() && file.getName().contains(".cs")) {
			inputStream = new FileInputStream(file); 
			ANTLRInputStream input = new ANTLRInputStream(inputStream);

			CSharp4Lexer lexer = new CSharp4Lexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			CSharp4Parser parser = new CSharp4Parser(tokens);
			ParseTree tree = parser.compilation_unit(); 

			ParseTreeWalker walker = new ParseTreeWalker();
			MainClassC extractor = new MainClassC(parser);
			walker.walk(extractor, tree);

			relParser.interfaceMap.put(extractor.currentClass, extractor.isInterface);
			relParser.relParserMap.put(extractor.currentClass, extractor.relationStructMap);
		}
		}	

		String aRelation = relParser.parseRelMap();
		String inputToPlantUml = "@startuml\n" + classInterfaceOutput + aRelation + "@enduml\n";
//		System.out.println(inputToPlantUml);

		try {
			String imageName =  args[1] + ".jpg";
			OutputStream finalImage = new FileOutputStream(imageName);
			SourceStringReader reader = new SourceStringReader(inputToPlantUml);
			System.out.println("Generating Image " + imageName);
			reader.generateImage(finalImage); //generates image
			System.out.println("Opening Image " + imageName);
		}
		catch (Exception e) {

		}
	}
}