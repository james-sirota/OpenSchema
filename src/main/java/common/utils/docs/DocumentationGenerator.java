package common.utils.docs;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

import common.utils.schema.ParserConfig;

public class DocumentationGenerator {
	
	private ParserConfig config;
	
	public DocumentationGenerator(Map<String, String> cnf)
	{
		config = new ParserConfig(cnf);

	}
	
	public void generate(String flename) throws FileNotFoundException
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("#Fields: \n\n");
		
		config.getFields().values().forEach(field -> {
			sb.append(String.format("##Field Name:\t %s \n\n", field.getName()));
			sb.append(String.format("*Description: %s* \n", field.getDescription()));
			sb.append("\n");
			sb.append(String.format("\ttype: %s \n", field.getType()));
			sb.append("\n");
			sb.append(String.format("\tsupertype: %s \n", field.getSuperType()));
			sb.append("\n");
			sb.append("Extended keys:");
			sb.append("\n\n");
			field.getAllExtendedKeys().forEach((k,v) -> {
				sb.append("\t" + k +":"+ v+"\n\n" );
			
			});
			
			sb.append("Mappers to this field:");
			sb.append("\n\n");
			config.getMappers().forEach((s, v) -> {
				if(v.equals(field.getName()))
				{
					sb.append(String.format("\t%s: %s \n", s, v));
				}
			});
		});
		
		
		
		sb.append("#Supertypes: \n\n");
		
		config.getSuperTypes().values().forEach(st ->{
			sb.append(String.format("###supertype: %s:%s \n", st.getParentElement(), st.getName()));
			sb.append("\n\n");
			sb.append("```\n");
			sb.append(st.getScript().trim());
			sb.append("\n```");
			sb.append("\n\n");
			
		});
		
		PrintWriter out = new PrintWriter(flename);
		out.println(sb.toString());
		out.flush();
		out.close();
	}
	

}
