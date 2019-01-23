package common.schematypes;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class Field implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1590731854777527027L;
	private String name;
	private String type;
	private String superType = "NA";
	private String description;
	Map<String, Object> extended;
	
	public Field()
	{
		extended = new TreeMap<String, Object>();
	}
	
	public void setExtended(Map<String, Object> ext)
	{
		ext.forEach((key, value) -> {setExtended(key, value);});
	}
	public void setExtended(String key, Object value)
	{
		if(extended.containsKey(key))
			throw new IllegalArgumentException(String.format("Unable to add additional attribute: %, % because attribute %, % already exists ", key, value, key, extended.get(key)));
		
		this.extended.put(key, value);
	}
	public boolean extendedKeyExists(String key)
	{
		return extended.containsKey(key);
	}
	public Map<String, Object> getAllExtendedKeys()
	{
		return this.extended;
	}
	public Object getExtendedFieldByKey(String key)
	{
		return this.extended.get(key);
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

	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Name: " + name + "\t");
		sb.append("Type: " + type+ "\t");
		sb.append("SuperType: " + superType+ "\t");
		sb.append("Description: " + description+ "\t");
		extended.forEach((key, value) -> { sb.append(key + ": " + extended.get(key)+ "\t");});		
		return sb.toString();
	}

}
