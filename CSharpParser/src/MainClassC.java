import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class MainClassC extends CSharp4BaseListener{
	
	Vector<PropertyAttributes> propertyValue = new Vector<>(); //to add property attributes
	Vector<MethodAttributes> methodValue = new Vector<>(); //to add method attributes
	HashMap<String, RelationStructure> relationStructMap = new HashMap<>(); //to add relations
	
	CSharp4Parser parser;
	String currentClass;
	
	boolean isInterface = false;

	String methodOutput = "";
	String propOutput = "";
	
	public MainClassC(CSharp4Parser parser) {
		this.parser = parser;
	}
	
	//add a new relation structure
	private RelationStructure fetchRelStruc(String className) {
		if (className != null) {
			RelationStructure valuesRel = (RelationStructure) relationStructMap.get(className);
			if (valuesRel == null) {
				valuesRel = new RelationStructure();
				valuesRel.endClass = className;
				relationStructMap.put(className, valuesRel);
			}
			return valuesRel;
		}
		else {
			return null;
		}
	}
	
	//to check if there are multiple instances of same class/interface
	private boolean checkRelStruc(String className) {
		if (className != null) {
			RelationStructure valuesRel = (RelationStructure) relationStructMap.get(className);

			if (valuesRel == null) {
				return false;
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public void enterClass_definition(CSharp4Parser.Class_definitionContext ctx) {
		currentClass = ctx.getChild(1).getText(); //main class name
		isInterface = false;
		
		DiagramGenerator.classInterfaceOutput = DiagramGenerator.classInterfaceOutput + ctx.getChild(0).getText() + " "
				+ currentClass;
		
		if(ctx.class_base() != null)
		{
			if(ctx.class_base().class_type() != null) 
			{
			RelationStructure relStr = fetchRelStruc(ctx.class_base().class_type().type_name().namespace_or_type_name().identifier().get(0).getText());
			relStr.isExtends = true;
			}
		}
	}
	
	@Override
	public void enterInterface_type(CSharp4Parser.Interface_typeContext ctx) {
		RelationStructure relStr = fetchRelStruc(ctx.getText());
		relStr.isImplements = true;
	}
	
	@Override
	public void enterInterface_definition(CSharp4Parser.Interface_definitionContext ctx) {
		currentClass = ctx.getChild(1).getText();
		isInterface = true;
		DiagramGenerator.classInterfaceOutput = DiagramGenerator.classInterfaceOutput + ctx.getChild(0).getText() + " "
				+ currentClass;
	}
	
	@Override
	public void enterTyped_member_declaration(CSharp4Parser.Typed_member_declarationContext ctx) {
		String symbol = "";
		String accessModifier = ctx.getParent().getParent().getChild(0).getChild(0).getText();
		
		if (accessModifier.equals("public")) {
			symbol = "+";
		}
		if (accessModifier.equals("private")) {
			symbol = "-";
		}
		
	if (ctx.type() != null) {
			if (ctx.type().base_type().simple_type() != null && !(accessModifier.equals("protected")) && ctx.getParent().getParent().getChildCount() > 1) {
				propertyValue.add(new PropertyAttributes(symbol, ctx.getChild(1).getChild(0).getChild(0).getText(),
						ctx.getChild(0).getText()));
			}	
			
			if(ctx.type().base_type().class_type() != null){
				if (!checkClassPresent(ctx.type().base_type().class_type().getText()) && !(accessModifier.equals("protected")) && ctx.field_declaration2() != null && ctx.getParent().getParent().getChildCount() > 1 && !checkClassContains(ctx.type().base_type().class_type().getText())) {
				propertyValue.add(new PropertyAttributes(symbol, ctx.field_declaration2().variable_declarators().getText(),
							ctx.type().base_type().class_type().getText()));
				}
				
				if(ctx.type().base_type().class_type().type_name() !=  null) {
				//	if(ctx.type().base_type().class_type().type_name().namespace_or_type_name().identifier().get(0).getText().contains("ICollection")) {
					if (checkCollectionType(ctx.getChild(0).getChild(0).getText())) {
					String collClass = ctx.type().base_type().class_type().type_name().namespace_or_type_name().type_argument_list_opt().get(0).getText();
					String finalClass = collClass.substring(1, collClass.length()-1);
					RelationStructure relStr = fetchRelStruc(finalClass);
					relStr.isLocalVar = true;
					relStr.isCollection = true;
					}
					else
					{
						boolean isColl = false;
						if (checkRelStruc(ctx.type().base_type().class_type().type_name().namespace_or_type_name().identifier().get(0).getText())) {
							isColl = true;
						}
						RelationStructure relStr = fetchRelStruc(ctx.type().base_type().class_type().type_name().namespace_or_type_name().identifier().get(0).getText());
						relStr.isLocalVar = true;
						
						if (isColl) {
							relStr.isCollection = true;
						}
					}
					
				}
			}			
		}
	if(ctx.method_declaration2() != null) {
			if(symbol != null) {
				String containsStatic = "";
			if(symbol.equals("+")) {
				MethodAttributes methodAttributes;
				if(ctx.method_declaration2().formal_parameter_list() != null)
				{
					ArrayList<FormalParameters> formalParameterList = new ArrayList<>();
					for (int i = 0; i < ctx.method_declaration2().formal_parameter_list().fixed_parameters().getChildCount(); i = i + 2) {
						FormalParameters formalParameters = new FormalParameters();
						formalParameters.nameClass = ctx.method_declaration2().formal_parameter_list().fixed_parameters().getChild(i).getChild(0).getText();
						formalParameters.nameInstance = ctx.method_declaration2().formal_parameter_list().fixed_parameters().getChild(i).getChild(1).getText();
						formalParameterList.add(formalParameters);
					}
						methodAttributes = new MethodAttributes(containsStatic + "+", ctx.method_declaration2().method_member_name().getText(), ctx.type().getText(),
								formalParameterList);
				}
				else
				{
					methodAttributes = new MethodAttributes(containsStatic + "+", ctx.method_declaration2().method_member_name().getText(), ctx.type().getText(),
							null);
				}
				methodValue.add(methodAttributes);
			}
		}
	}
	}
	
	private boolean checkCollectionType(String className) 
	{
		boolean contains = false;
		
			if (className.contains("<") && className.contains(">") && className.indexOf("<") < className.indexOf(">")) 
			{
				contains = true;
			}
		
		return contains;
	}
	
	private boolean checkClassPresent(String className) 
	{
		return DiagramGenerator.classNames.contains(className);
	}
	
	private boolean checkClassContains(String className) 
	{
		boolean contains = false;
		
		for (String cName : DiagramGenerator.classNames) 
		{
			if (className.contains("<" + cName + ">")) 
			{
				contains = true;
				break;
			}
		}		
		return contains;
	}
			
	@Override
	public void enterFixed_parameter(CSharp4Parser.Fixed_parameterContext ctx) {
		if(ctx.type().base_type().class_type() != null) {
			if(ctx.type().base_type().class_type().type_name() != null) {
		RelationStructure relStr = fetchRelStruc(ctx.type().base_type().class_type().type_name().namespace_or_type_name().identifier().get(0).getText());
		relStr.isMethod = true;		
		}
		}
	}
	
	@Override
	public void enterLocal_variable_declaration(CSharp4Parser.Local_variable_declarationContext ctx) {
		if(ctx.local_variable_type().type().base_type().class_type().type_name() != null) {
		RelationStructure relStr = fetchRelStruc(ctx.local_variable_type().type().base_type().class_type().type_name().namespace_or_type_name().identifier().get(0).getText());
		relStr.isMethod = true;
		}
	}	
	
	@Override
	public void enterProperty_declaration2(CSharp4Parser.Property_declaration2Context ctx) {
			String accessModifier = "";
			String containsStatic = "";

			if((ctx.getParent().getParent().getParent().getChild(0).getText().contains("public"))) {
				accessModifier = "public";
			}
			
			if (accessModifier.equals("public")) {
				MethodAttributes methodAttributes;
					methodAttributes = new MethodAttributes(containsStatic + "+", ctx.member_name().interface_type().type_name().namespace_or_type_name().identifier().get(0).getText(), ctx.getParent().getChild(0).getChild(0).getText(),
								null);			
				
			methodValue.add(methodAttributes);				
			} 
	}
	
	@Override
	public void enterMethod_declaration2(CSharp4Parser.Method_declaration2Context ctx) {
		String accessModifier = "";
		String containsStatic = "";

		if((ctx.getParent().getParent().getChild(0).getText().contains("public"))) {
			 accessModifier = "public";
		}
		if (accessModifier.equals("public")) {
			MethodAttributes methodAttributes;
			if(ctx.formal_parameter_list() != null)
			{
				ArrayList<FormalParameters> formalParameterList = new ArrayList<>();
				for (int i = 0; i < ctx.formal_parameter_list().fixed_parameters().getChildCount(); i = i + 2) {
					FormalParameters formalParameters = new FormalParameters();
					formalParameters.nameClass = ctx.formal_parameter_list().fixed_parameters().getChild(i).getChild(0).getText();
					formalParameters.nameInstance = ctx.formal_parameter_list().fixed_parameters().getChild(i).getChild(1).getText();
					formalParameterList.add(formalParameters);
				}
					methodAttributes = new MethodAttributes(containsStatic + "+", ctx.method_member_name().getText(), ctx.getParent().getChild(0).getText(),
							formalParameterList);
			}
			else
			{
				methodAttributes = new MethodAttributes(containsStatic + "+", ctx.method_member_name().getText(), ctx.getParent().getChild(0).getText(),
						null);
			}
			methodValue.add(methodAttributes);
			
		}
	}
	
@Override
public void enterInterface_member_declaration(CSharp4Parser.Interface_member_declarationContext ctx) {

		MethodAttributes methodAttributes;
		if (ctx.formal_parameter_list() != null) {
			ArrayList<FormalParameters> formalParameterList = new ArrayList<>();
			for (int i = 0; i < ctx.formal_parameter_list().fixed_parameters().getChildCount(); i = i + 2) {
				FormalParameters formalParameters = new FormalParameters();
				formalParameters.nameClass = ctx.formal_parameter_list().fixed_parameters().getChild(i).getChild(0).getText();
				formalParameters.nameInstance = ctx.formal_parameter_list().fixed_parameters().getChild(i).getChild(1).getText();
				formalParameterList.add(formalParameters);
			}
			methodAttributes = new MethodAttributes("+", ctx.identifier().getText(), ctx.getChild(0).getText(),
					formalParameterList);
		}
		else {
			methodAttributes = new MethodAttributes("+", ctx.identifier().getText(), ctx.getChild(0).getText(), null);
		}
		methodValue.add(methodAttributes);
}

@Override
public void enterConstructor_declaration2(CSharp4Parser.Constructor_declaration2Context ctx) {
		String accessModifier = "";
		String containsStatic = "";

			if(ctx.getParent().getParent().getChild(0).getText().contains("public"))
				{
				accessModifier = "public";
				}
			MethodAttributes methodAttributes;
			if (accessModifier.equals("public")) {
				if(ctx.formal_parameter_list() != null) {
				ArrayList<FormalParameters> formalParameterList = new ArrayList<>();
				for (int i = 0; i < ctx.formal_parameter_list().fixed_parameters().getChildCount(); i = i + 2) {
				FormalParameters formalParameters = new FormalParameters();
				formalParameters.nameClass = ctx.formal_parameter_list().fixed_parameters().getChild(i).getChild(0).getText();
				formalParameters.nameInstance = ctx.formal_parameter_list().fixed_parameters().getChild(i).getChild(1).getText();
				formalParameterList.add(formalParameters);
				}
				methodAttributes = new MethodAttributes(containsStatic + "+", ctx.identifier().getText(), null,
						formalParameterList);			
				}
				else {
					methodAttributes = new MethodAttributes(containsStatic + "+", ctx.identifier().getText(), null,
							null);
						}
				methodValue.add(methodAttributes);
			}
}
	
	@Override
	public void exitInterface_definition(CSharp4Parser.Interface_definitionContext ctx) {
		DiagramGenerator.classInterfaceOutput += "{" + "\n";
		for (int i = 0; i < propertyValue.size(); i++) {
			DiagramGenerator.classInterfaceOutput += propertyValue.get(i).accessModifier + propertyValue.get(i).propertyName
					+ ":" + propertyValue.get(i).dataType + "\n";
		}

		for (int i = 0; i < methodValue.size(); i++) {
			MethodAttributes methodAttributes = methodValue.get(i);
			methodOutput += methodAttributes.methodFormat() + "\n";
		}

		DiagramGenerator.classInterfaceOutput += methodOutput;
		DiagramGenerator.classInterfaceOutput += "}" + "\n";
	}
	
	public static String decapitalizeString(String string) {
		return ((string == null) || (string.isEmpty()) ? ""
				: Character.toLowerCase(string.charAt(0)) + string.substring(1));
	}
	
	@Override
	public void exitClass_definition(CSharp4Parser.Class_definitionContext ctx) {
		
		ArrayList<String> variableList = new ArrayList<>();
		
		// removing getter setter methods if any and making the attribute public
		for (int i = 0; i < propertyValue.size(); i++) {
			variableList.add(propertyValue.get(i).propertyName);
		}
		Vector<MethodAttributes> methodValTemp = new Vector<>(methodValue);
		for (int i = 0; i < methodValTemp.size(); i++) {
				if (methodValTemp.get(i).methodName.startsWith("get")) {
					String getterName = methodValTemp.get(i).methodName.replaceFirst("get", "");
					String varName = decapitalizeString(getterName);
					String setterName = "set" + getterName;

					for (int j = 0; j < methodValTemp.size(); j++) {
						if (methodValTemp.get(j).methodName.equals(setterName)) {
							if (variableList.contains(varName)) {
								int index = variableList.indexOf(varName);
								propertyValue.get(index).accessModifier = "+";
								methodValue.remove(methodValTemp.get(j));
								methodValue.remove(methodValTemp.get(i));
								break;
							}
						}
					}
				}		
		}
		
		for (int i = 0; i < methodValue.size(); i++) {
			MethodAttributes methodAttributes = methodValue.get(i);
			methodOutput += methodAttributes.methodFormat() + "\n";
		}

		DiagramGenerator.classInterfaceOutput += "{" + "\n";
		for (int i = 0; i < propertyValue.size(); i++) {
			PropertyAttributes propertyAttributes = propertyValue.get(i);
			propOutput += propertyAttributes.propertyFormat() + "\n";
		}
		DiagramGenerator.classInterfaceOutput += propOutput;
		DiagramGenerator.classInterfaceOutput += methodOutput;
		DiagramGenerator.classInterfaceOutput += "}" + "\n";
	}

}
