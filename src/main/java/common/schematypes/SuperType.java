package common.schematypes;

import java.io.Serializable;

public class SuperType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7081611781220821338L;
	private String name;
	private String type;
	private String script;
	private String parentElement;
	
	public String getParentElement() {
		return parentElement;
	}

	public void setParentElement(String parentElement) {
		this.parentElement = parentElement;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
	public String toString()
	{
		return ("Parent : " + parentElement + "Name   : " + name + "Type   : " + type + "Script : " + script);
	}

}
