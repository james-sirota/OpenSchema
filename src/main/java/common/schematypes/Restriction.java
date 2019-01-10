package common.schematypes;

public class Restriction {
	
	private String parserName;
	private String fieldName;
	private String restrictionName;
	private String parentSupertype;
	
	public Restriction(String parserName, String fieldName, String restrictionName, String parentSupertype)
	{
		this.parserName = parserName;
		this.fieldName = fieldName;
		this.restrictionName = restrictionName;
		this.parentSupertype = parentSupertype;
	}
	
	public String getParserName() {
		return parserName;
	}
	public void setParserName(String parserName) {
		this.parserName = parserName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getRestrictionName() {
		return restrictionName;
	}
	public void setRestrictionName(String restrictionName) {
		this.restrictionName = restrictionName;
	}
	public String getParentSupertype() {
		return parentSupertype;
	}
	public void setParentSupertype(String parentSupertype) {
		this.parentSupertype = parentSupertype;
	}

	public String toString()
	{
		return (String.format("parserName: %s, fieldName: %s, restrictionName: %s, parentSupertype: %s", parserName, fieldName, restrictionName, parentSupertype));
	}
}
