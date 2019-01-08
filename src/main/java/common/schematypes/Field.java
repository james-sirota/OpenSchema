package common.schematypes;

import java.io.Serializable;

public class Field implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1590731854777527027L;
	private String name;
	private String type;
	private String superType = "NA";
	private String description;
	boolean required = false;

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

	public String getSuperType() {
		return superType;
	}

	public void setSuperType(String superType) {
		this.superType = superType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public void print()
	{
		System.out.println("Name	  	: " + name);
		System.out.println("Type	  	: " + type);
		System.out.println("SuperType 	: " + superType);
		System.out.println("Description	: " + description);
		System.out.println("Required	: " + required);
	}

}
