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
	
	public void print()
	{
		System.out.println("Parent : " + parentElement);
		System.out.println("Name   : " + name);
		System.out.println("Type   : " + type);
		System.out.println("Script : " + script);
	}

}
