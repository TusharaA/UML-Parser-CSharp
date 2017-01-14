
import java.util.HashMap;
import java.util.Map;

public class RelationParser {

	HashMap<String, HashMap<String, RelationStructure>> relParserMap = new HashMap<>(); //contains relation details
	HashMap<String, Boolean> interfaceMap = new HashMap<>(); //contains list of interfaces and classes

	private RelationStructure fetchRelStruc(String currentClassName, String endClassName) {
		if (currentClassName != null && endClassName != null) {
			HashMap<String, RelationStructure> valueMap = relParserMap.get(currentClassName);
			if (valueMap != null) {
				RelationStructure valuesRel = (RelationStructure) valueMap.get(endClassName);
				return valuesRel;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}

	public String parseRelMap() {
		String outputRel = "";

		for (Map.Entry<String, HashMap<String, RelationStructure>> valueHashP : relParserMap.entrySet()) {
			String currentClassName = valueHashP.getKey();
			HashMap<String, RelationStructure> valueMap = valueHashP.getValue();

			for (Map.Entry<String, RelationStructure> valueRelHashP : valueMap.entrySet()) {
				String endClassName = valueRelHashP.getKey();

				if (interfaceMap.get(endClassName) != null) {// check if this class name
																											// is present in our list,
																											// else don't display

					RelationStructure endClassRelStruct = valueRelHashP.getValue();
					//inheritance
					if (endClassRelStruct.isExtends && interfaceMap.containsKey(endClassRelStruct.endClass) && (interfaceMap.get(endClassRelStruct.endClass) == false)) {
						outputRel += currentClassName + "--|>" + endClassName + "\n";
					}
					//interface
				 if (endClassRelStruct.isImplements && interfaceMap.containsKey(endClassRelStruct.endClass)) {
						outputRel += currentClassName + "..|>" + endClassName + "\n";
					}
				//interface
					 if (endClassRelStruct.isExtends && interfaceMap.containsKey(endClassRelStruct.endClass) && (interfaceMap.get(endClassRelStruct.endClass) == true)) {
						outputRel += currentClassName + "..|>" + endClassName + "\n";
					}
					 //dependency
					if (endClassRelStruct.isMethod) {

						Boolean isEndClassInterface = interfaceMap.get(endClassName);
						Boolean isCurrentClassInterface = interfaceMap.get(currentClassName);

						if (isCurrentClassInterface == false && isEndClassInterface == true) {
							outputRel += currentClassName + "\"uses\"..>" + endClassName + "\n";
						}
					}

					RelationStructure currentClassRelStruct = fetchRelStruc(endClassName, currentClassName);
					//association
					if (endClassRelStruct.isLocalVar && endClassRelStruct.isCollection) {

						if (currentClassRelStruct != null && currentClassRelStruct.isLinkDrawn == false) {

							if (currentClassRelStruct.isCollection) {
								outputRel += currentClassName + " \"*\" -- \"*\" " + endClassName + "\n";
							}
							else {
								outputRel += currentClassName + " \"1\" -- \"*\" " + endClassName + "\n";
							}
							endClassRelStruct.isLinkDrawn = true;
						}
						else if (currentClassRelStruct == null) {
							outputRel += currentClassName + " -- \"*\" " + endClassName + "\n";
						}
					}
					else if (endClassRelStruct.isLocalVar) {

						if (currentClassRelStruct != null && currentClassRelStruct.isLinkDrawn == false) {

							if (currentClassRelStruct.isCollection) {
								outputRel += currentClassName + " \"*\" -- \"1\" " + endClassName + "\n";
							}
							else {
								outputRel += currentClassName + " \"1\" -- \"1\" " + endClassName + "\n";
							}

							endClassRelStruct.isLinkDrawn = true;
						}
						else if (currentClassRelStruct == null) {
							outputRel += currentClassName + " -- \"1\" " + endClassName + "\n";
						}
					}
				}
			}
		}

		return outputRel;
	}

}
