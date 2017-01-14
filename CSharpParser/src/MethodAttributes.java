
import java.util.ArrayList;

public class MethodAttributes {

	String methodAccessModifier;
	String methodName;
	String returnType; 
	ArrayList<FormalParameters> formalParameterList;

	public MethodAttributes(String methodAccessModifier, String methodName, String returnType,
			ArrayList<FormalParameters> formalParameterList) {

		this.methodAccessModifier = methodAccessModifier;
		this.methodName = methodName;
		this.returnType = returnType;
		this.formalParameterList = formalParameterList;
	}

	public String methodFormat() {

		String aMethodName = methodName;

		String mDescr = methodAccessModifier + aMethodName + "(";

		if (formalParameterList != null) {
			for (int i = 0; i < formalParameterList.size(); i++) {
				if (i != 0) {
					mDescr += ",";
				}
				mDescr += formalParameterList.get(i).description();
			}
		}

		if (returnType == null) //for constructor
			mDescr += ")";
		else
			mDescr += "):" + returnType;

		return mDescr;
	}
}
