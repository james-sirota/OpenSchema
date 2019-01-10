package common.schematypes;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Trait implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3249677811075730138L;
	private Set<Object> tr = new HashSet<Object>();
	private String name;
	private String description;
	
	public Trait()
	{

	}
	
	public Trait(Set<String> traits)
	{
		tr.addAll(traits);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public void addTrait(String trait)
	{
		tr.add(trait);
	}
	
	public void removeTrait(String trait)
	{
		tr.remove(trait);
	}

	public boolean matchTrate(Set<String> fields)
	{
		return fields.containsAll(tr);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Name        : " + name  + "Description : " + description + "Elements    : ");
		
		for(Object s : tr)
		{
			sb.append("\t" + s + " ");
		}
		
		return sb.toString();
	}
}
