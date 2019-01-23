package common.utils.schema;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import common.parser.AbstractSchemadParser;
import common.schematypes.Field;
import common.schematypes.Ontology;
import common.schematypes.Restriction;
import common.schematypes.SuperType;
import common.schematypes.Trait;

public class ParserConfig implements Serializable {

	private final Logger logger = LoggerFactory.getLogger(AbstractSchemadParser.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 7753414227667679078L;
	private String schemaDirectory;
	private String schemaExtensionDirectory;
	private String mapperDirectory;
	private String restrictionsDirectory;

	private String schemaFile_Extension;
	private String schemaExtension_FileExtension;
	private String schemaAddition_FileExtension;
	private String mapper_FileExtension;
	private String restriction_FileExtension;

	private Map<String, SuperType> superTypes;
	private Map<String, Trait> traits;
	private Map<String, Field> fields;
	private Map<String, String> mappers;
	private Map<String, Ontology> ontologies;
	private Map<String, Restriction> restrictions;

	public ParserConfig(Map<String, String> config) {
	

		this.schemaDirectory = config.get("schemaDirectory");
		this.schemaExtensionDirectory = config.get("schemaExtensionDirectory");
		this.mapperDirectory = config.get("mapperDirectory");
		this.restrictionsDirectory = config.get("restrictionsDirectory");
		this.schemaFile_Extension = config.get("schemaFile_Extension");
		this.schemaExtension_FileExtension = config.get("schemaExtension_FileExtension");
		this.schemaAddition_FileExtension = config.get("schemaAddition_FileExtension");
		this.mapper_FileExtension = config.get("mapper_FileExtension");
		this.restriction_FileExtension = config.get("restriction_FileExtension");

		logger.debug(String.format(
				"Initializing parser with schema directory: %s, extensions directory: %s, mapper directory: %s, restrictions directory: %s",
				schemaDirectory, schemaExtensionDirectory, mapperDirectory, restrictionsDirectory));

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

	private void loadConfig() throws SAXException, IOException, ParserConfigurationException {
		// First load the required schema

		File[] fileList = getExtensionSchemaFileNames(schemaDirectory, schemaFile_Extension);

		if (fileList.length > 1)
			throw new IllegalArgumentException("Error, there can only be one common schema");

		File commonSchema = fileList[0];
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(commonSchema);
		doc.getDocumentElement().normalize();

		Element root = doc.getDocumentElement();

		logger.debug("Found schema        : " + commonSchema);
		logger.debug("Schema type         : " + root.getAttribute("type"));
		logger.debug("Schema version      : " + root.getAttribute("version"));
		logger.debug("Schema description  : " + root.getAttribute("description"));

		logger.debug("==========================");

		superTypes = UniversalConfigBuilder.loadSupertypes(root);
		traits = UniversalConfigBuilder.loadTraits(root);
		fields = UniversalConfigBuilder.loadSchemaFields(root);
		ontologies = UniversalConfigBuilder.loadOntologies(root);

		// Then load the schema extensions

		fileList = getExtensionSchemaFileNames(schemaExtensionDirectory, schemaExtension_FileExtension);

		for (int i = 0; i < fileList.length; i++) {
			Document extension_doc = dBuilder.parse(fileList[i]);
			extension_doc.getDocumentElement().normalize();
			Element extensionRoot = extension_doc.getDocumentElement();

			logger.debug("Found schema        : " + fileList[i]);
			logger.debug("Schema type         : " + extensionRoot.getAttribute("type"));
			logger.debug("Schema version      : " + extensionRoot.getAttribute("version"));
			logger.debug("Schema description  : " + extensionRoot.getAttribute("description"));
			logger.debug("==========================");

			superTypes.putAll(UniversalConfigBuilder.loadSupertypes(extensionRoot));
			traits.putAll(UniversalConfigBuilder.loadTraits(extensionRoot));
			fields.putAll(UniversalConfigBuilder.loadSchemaFields(extensionRoot));
			ontologies.putAll(UniversalConfigBuilder.loadOntologies(extensionRoot));
		}

		logger.debug("Number of supertypes loaded: " + superTypes.size());

		for (String key : superTypes.keySet()) {
			logger.debug("Retrieve key: " + key);
			logger.debug(superTypes.get(key).toString());
		}
		logger.debug("==========================");

		logger.debug("Number of traits loaded: " + traits.size());

		for (String key : traits.keySet()) {
			logger.debug("Retrieve key: " + key);
			logger.debug(traits.get(key).toString());
		}
		logger.debug("==========================");

		logger.debug("Number of fields loaded: " + fields.size());

		for (String key : fields.keySet()) {
			logger.debug("Retrieve key: " + key);
			logger.debug(fields.get(key).toString());
		}
		logger.debug("==========================");

		logger.debug("Number of ontologies loaded: " + ontologies.size());

		for (String s : ontologies.keySet()) {
			logger.debug(ontologies.get(s).toString());
		}
		logger.debug("==========================");

		fileList = getExtensionSchemaFileNames(mapperDirectory, mapper_FileExtension);

		mappers = new TreeMap<String, String>();

		for (int i = 0; i < fileList.length; i++) {
			Document mapper_doc = dBuilder.parse(fileList[i]);
			mapper_doc.getDocumentElement().normalize();
			Element mapperRoot = mapper_doc.getDocumentElement();

			mappers.putAll(UniversalConfigBuilder.loadMappers(mapperRoot));
		}

		logger.debug("Number of mappers loaded: " + mappers.size());

		for (String s : mappers.keySet()) {
			logger.debug(mappers.get(s));
		}
		logger.debug("==========================");

		fileList = getExtensionSchemaFileNames(restrictionsDirectory, restriction_FileExtension);
		restrictions = new TreeMap<String, Restriction>();

		for (int i = 0; i < fileList.length; i++) {
			Document restriction_doc = dBuilder.parse(fileList[i]);
			restriction_doc.getDocumentElement().normalize();
			Element mapperRoot = restriction_doc.getDocumentElement();

			restrictions.putAll(UniversalConfigBuilder.loadRestrictions(mapperRoot));
		}

		logger.debug("Number of restrictions loaded: " + restrictions.size());

		for (String s : restrictions.keySet()) {
			logger.debug(restrictions.get(s).toString());
		}
		logger.debug("==========================");
		
		
		File[] additionFiles = getExtensionSchemaFileNames(schemaDirectory, schemaAddition_FileExtension);
		
		for(File f : additionFiles)
		{
			Document addition_doc = dBuilder.parse(f);
			addition_doc.getDocumentElement().normalize();
			Element additionRoot = addition_doc.getDocumentElement();
			
			this.fields = UniversalConfigBuilder.loadSchemaAdditions(additionRoot, this.fields);

		}


		//fields.forEach((key, field) -> {System.out.println(field.toString());});
	}

	private File[] getExtensionSchemaFileNames(String dirString, final String extension) {
		logger.debug(dirString);
		File dir = new File(dirString);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(extension);
			}
		});

		return files;
	}
}