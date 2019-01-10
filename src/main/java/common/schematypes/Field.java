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
	Boolean required = null;
	Boolean stored = null;
	Boolean indexed = null;
	Boolean presisted = null;

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
	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getStored() {
		return stored;
	}

	public void setStored(Boolean stored) {
		this.stored = stored;
	}

	public Boolean getIndexed() {
		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}

	public Boolean getPresisted() {
		return presisted;
	}

	public void setPresisted(Boolean presisted) {
		this.presisted = presisted;
	}

	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Name	  	: " + name);
		sb.append("Type	  	: " + type);
		sb.append("SuperType 	: " + superType);
		sb.append("Description	: " + description);
		sb.append("Required	: " + required);
		sb.append("stored		: " + stored);
		sb.append("indexed		: " + indexed);
		sb.append("presisted	: " + presisted);
		
		return sb.toString();
	}

}
