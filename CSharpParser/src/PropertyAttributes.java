
public class PropertyAttributes {

	String accessModifier;
	String propertyName;
	String dataType;

	public PropertyAttributes(String accessModifier, String propertyName, String dataType) {
		this.propertyName = propertyName;
		this.accessModifier = accessModifier;
		this.dataType = dataType;
	}

	public String propertyFormat() {

		String pDescr = accessModifier + propertyName + ":" + dataType;
		return pDescr;
	}

}
