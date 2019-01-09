package common.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import common.schematypes.Field;
import common.schematypes.Ontology;
import common.schematypes.Restriction;
import common.schematypes.SuperType;
import common.schematypes.Trait;
import common.utils.UniversalConfigBuilder;

public class ParserConfig implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7753414227667679078L;
	private  String schemaDirectory;
	private  String schemaExtensionDirectory;
	private  String mapperDirectory;
	private  String restrictionsDirectory;
	
	private  String schemaFile_Extension = ".schema";
	private  String schemaExtension_FileExtension = ".schema_extension";
	private  String mapper_FileExtension = ".mapper";
	private  String restriction_FileExtension = ".xml";
	
	private  Map<String, SuperType> superTypes;
	private  Map<String, Trait> traits;
	private  Map<String, Field> fields;
	private  Map<String, String> mappers;
	private  Map<String, Ontology> ontologies;
	private  Map<String, Restriction> restrictions;
	



	public ParserConfig(String schemaDirectory,  String schemaExtensionDirectory, String mapperDirectory, String restrictionsDirectory){
    	
       	this.schemaDirectory = schemaDirectory;
    	this.schemaExtensionDirectory = schemaExtensionDirectory;
    	this.mapperDirectory = mapperDirectory;
    	this.restrictionsDirectory = restrictionsDirectory;
    	
       	try {
    			loadConfig();
    		} catch (Exception e) {
    			e.printStackTrace();
    		} 
    }
	
	
	public Map<String, SuperType> getSuperTypes() {
		return superTypes;
	}

	public void setSuperTypes(Map<String, SuperType> superTypes) {
		this.superTypes = superTypes;
	}

	public Map<String, Trait> getTraits() {
		return traits;
	}

	public void setTraits(Map<String, Trait> traits) {
		this.traits = traits;
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	public void setFields(Map<String, Field> fields) {
		this.fields = fields;
	}

	public Map<String, String> getMappers() {
		return mappers;
	}

	public void setMappers(Map<String, String> mappers) {
		this.mappers = mappers;
	}

	public Map<String, Ontology> getOntologies() {
		return ontologies;
	}

	public void setOntologies(Map<String, Ontology> ontologies) {
		this.ontologies = ontologies;
	}

    public Map<String, Restriction> getRestrictions() {
		return restrictions;
	}


	public void setRestrictions(Map<String, Restriction> restrictions) {
		this.restrictions = restrictions;
	}
    


    
    private void loadConfig() throws SAXException, IOException, ParserConfigurationException
    {
    	// First load the required schema 
		
    				File[] fileList = getExtensionSchemaFileNames(schemaDirectory, schemaFile_Extension);
    				
    				if(fileList.length > 1)
    					throw new IllegalArgumentException("Error, there can only be one common schema");
    				
    				File commonSchema = fileList[0];
    				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    				Document doc = dBuilder.parse(commonSchema);
    				doc.getDocumentElement().normalize();
    				
    				Element root = doc.getDocumentElement();

    				System.out.println("Found schema        : " + commonSchema);
    				System.out.println("Schema type         : " + root.getAttribute("type"));
    				System.out.println("Schema version      : " + root.getAttribute("version"));
    				System.out.println("Schema description  : " + root.getAttribute("description"));
    				

    				System.out.println("==========================");
    				
    				superTypes = UniversalConfigBuilder.loadSupertypes(root);
    				traits = UniversalConfigBuilder.loadTraits(root);
    				fields = UniversalConfigBuilder.loadFeilds(root);
    				ontologies = UniversalConfigBuilder.loadOntologies(root);
    		
    				
    				
    				// Then load the schema extensions
    				
    				fileList = getExtensionSchemaFileNames(schemaExtensionDirectory, schemaExtension_FileExtension);
    				
    				for(int i =0; i < fileList.length; i++)
    				{
    					Document extension_doc = dBuilder.parse(fileList[i]);
    					extension_doc.getDocumentElement().normalize();
    					Element extensionRoot = extension_doc.getDocumentElement();
    					
    					
    					System.out.println("Found schema        : " + fileList[i]);
    					System.out.println("Schema type         : " + extensionRoot.getAttribute("type"));
    					System.out.println("Schema version      : " + extensionRoot.getAttribute("version"));
    					System.out.println("Schema description  : " + extensionRoot.getAttribute("description"));
    					System.out.println("==========================");
    					
    					superTypes.putAll(UniversalConfigBuilder.loadSupertypes(extensionRoot));
    					traits.putAll(UniversalConfigBuilder.loadTraits(extensionRoot));
    					fields.putAll(UniversalConfigBuilder.loadFeilds(extensionRoot));
    					ontologies.putAll(UniversalConfigBuilder.loadOntologies(extensionRoot));
    				}
    				
    				System.out.println("Number of supertypes loaded: " + superTypes.size());
    				
    				for(String key : superTypes.keySet())
    				{
    					System.out.println("Retrieve key: " + key);
    					superTypes.get(key).print();
    				}
    				System.out.println("==========================");
    				
    				System.out.println("Number of traits loaded: " + traits.size());
    				
    				for(String key : traits.keySet())
    				{
    					System.out.println("Retrieve key: " + key);
    					traits.get(key).print();
    				}
    				System.out.println("==========================");
    				
    				System.out.println("Number of fields loaded: " + fields.size());
    				
    				for(String key : fields.keySet())
    				{
    					System.out.println("Retrieve key: " + key);
    					fields.get(key).print();
    				}
    				System.out.println("==========================");
    				
    				System.out.println("Number of ontologies loaded: " + ontologies.size());
    				
    				for(String s: ontologies.keySet())
    				{
    					ontologies.get(s).print();
    				}
    				System.out.println("==========================");
    				
    				fileList = getExtensionSchemaFileNames(mapperDirectory, mapper_FileExtension);
    				
    				
    				mappers = new TreeMap<String, String>();
    				
    				for(int i =0; i < fileList.length; i++)
    				{
    					Document mapper_doc = dBuilder.parse(fileList[i]);
    					mapper_doc.getDocumentElement().normalize();
    					Element mapperRoot = mapper_doc.getDocumentElement();
    					
    					mappers.putAll(UniversalConfigBuilder.loadMappers(mapperRoot));
    				}
    				
    				System.out.println("Number of mappers loaded: " + mappers.size());
    				
    				for(String s: mappers.keySet())
    				{
    					System.out.println(mappers.get(s));
    				}
    				System.out.println("==========================");

    				
    				fileList = getExtensionSchemaFileNames(restrictionsDirectory, restriction_FileExtension);
    				restrictions = new TreeMap<String, Restriction>();
    				
    				for(int i =0; i < fileList.length; i++)
    				{
    					Document restriction_doc = dBuilder.parse(fileList[i]);
    					restriction_doc.getDocumentElement().normalize();
    					Element mapperRoot = restriction_doc.getDocumentElement();
    					
    					restrictions.putAll(UniversalConfigBuilder.loadRestrictions(mapperRoot));
    				}
    				
    				
    				System.out.println("Number of restrictions loaded: " + restrictions.size());
    				
    				for(String s: restrictions.keySet())
    				{
    					restrictions.get(s).print();
    				}
    				System.out.println("==========================");
    				
    				
    }
    
    private  File[] getExtensionSchemaFileNames(String dirString, final String extension)
	{
		System.out.println(dirString);
		File dir = new File(dirString);
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(extension);
		    }
		});
		
		return files;
	}
}