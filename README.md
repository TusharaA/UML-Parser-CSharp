# UML-Parser-CSharp

Technologies and Tools: Java, ANTLR4, PlantUML

Converting C# source code to class diagram: 
1. The input is converted to a Abstract Syntax Tree(AST) using ANTLR4 parser. 
2. The AST is parsed by the the code and an intermedite output is generated. 
3. This intermediate input is fed to PlantUML in a proper format to generate the class diagram.
