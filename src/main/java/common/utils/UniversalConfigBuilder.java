package common.utils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.schematypes.Field;
import common.schematypes.Ontology;
import common.schematypes.SuperType;
import common.schematypes.Trait;

public class UniversalConfigBuilder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6042239916614015767L;

	protected static Map<String, SuperType> loadSupertypes(Element root) {

		Map<String, SuperType> stm = new LinkedHashMap<String, SuperType>();
		NodeList nodes = root.getChildNodes();
		NodeList superTypes = nodes.item(1).getChildNodes();

		for (int i = 0; i < superTypes.getLength(); i++) {
			if (superTypes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				String key = superTypes.item(i).getAttributes().getNamedItem("name").getNodeValue();
				// System.out.println(key);

				NodeList innerNodes = superTypes.item(i).getChildNodes();

				for (int j = 0; j < innerNodes.getLength(); j++) {
					SuperType st = new SuperType();
					st.setParentElement(key);

					if (innerNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
						String type = innerNodes.item(j).getAttributes().getNamedItem("type").getNodeValue();
						// System.out.println(type);
						String name = innerNodes.item(j).getAttributes().getNamedItem("name").getNodeValue();
						// System.out.println(name);
						st.setType(type);
						st.setName(name);
						st.setScript(innerNodes.item(j).getTextContent());
						// st.print();
						stm.put(key + type, st);
					}
				}
			}

		}

		return stm;

	}

	protected static Map<String, Trait> loadTraits(Element root) {

		Map<String, Trait> trs = new LinkedHashMap<String, Trait>();
		NodeList nodes = root.getChildNodes();
		NodeList traits = nodes.item(3).getChildNodes();

		for (int i = 0; i < traits.getLength(); i++) {
			if (traits.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Trait tr = new Trait();
				String name = traits.item(i).getAttributes().getNamedItem("name").getNodeValue();
				tr.setName(name);
				String description = traits.item(i).getAttributes().getNamedItem("description").getNodeValue();

				tr.setDescription(description);

				NodeList innerNodes = traits.item(i).getChildNodes();

				for (int j = 0; j < innerNodes.getLength(); j++) {
					if (innerNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
						tr.addTrait(innerNodes.item(j).getAttributes().getNamedItem("name").getNodeValue());
					}
				}
				trs.put(name, tr);

			}

		}

		return trs;
	}

	protected static Map<String, Field> loadFeilds(Element root) {

		Map<String, Field> trs = new LinkedHashMap<String, Field>();
		NodeList nodes = root.getChildNodes();
		NodeList fields = nodes.item(5).getChildNodes();

		for (int i = 0; i < fields.getLength(); i++) {
			if (fields.item(i).getNodeType() == Node.ELEMENT_NODE) {
				
				Field f = new Field();
				String name = fields.item(i).getAttributes().getNamedItem("name").getNodeValue();
				f.setName(name);
				f.setDescription(fields.item(i).getAttributes().getNamedItem("description").getNodeValue());
				f.setType(fields.item(i).getAttributes().getNamedItem("type").getNodeValue());
				
				if(fields.item(i).getAttributes().getNamedItem("supertype") != null)
					f.setSuperType(fields.item(i).getAttributes().getNamedItem("supertype").getNodeValue());
				
				if(fields.item(i).getAttributes().getNamedItem("required") != null)
				{
					boolean req = Boolean.parseBoolean(fields.item(i).getAttributes().getNamedItem("required").getNodeValue());
					f.setRequired(req);
				}
				
				trs.put(name, f);
			}
		}

		return trs;
	}
	
	protected static Map<String, String> loadMappers(Element root) {
		
		Map<String, String> map = new TreeMap<String, String>();
		
		NodeList nodes = root.getChildNodes();
		//NodeList fields = nodes.item(1).getChildNodes();
		
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) 
			{
				String value = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
				
				NodeList mappings = nodes.item(i).getChildNodes();
				
				for(int j = 0; j < mappings.getLength(); j++)
					if (nodes.item(i).getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) 
					{
						//System.out.println(nodes.item(i).getChildNodes().item(j).getFirstChild().getNodeValue());
						map.put(nodes.item(i).getChildNodes().item(j).getFirstChild().getNodeValue(), value);
					}
					
				
				
			}
		}
		
		return map;
		
	}
	
	protected static Map<String, Ontology> loadOntologies(Element root) {

		Map<String, Ontology> ontologies = new LinkedHashMap<String, Ontology>();
		NodeList nodes = root.getChildNodes();
		NodeList fields = nodes.item(7).getChildNodes();

				
		for (int i = 0; i < fields.getLength(); i++) {
			if (fields.item(i).getNodeType() == Node.ELEMENT_NODE) 
			{
				
				String edge = fields.item(i).getAttributes().getNamedItem("edge").getNodeValue();
				String from = fields.item(i).getAttributes().getNamedItem("source").getNodeValue();
				String to = fields.item(i).getAttributes().getNamedItem("dest").getNodeValue();
				
				
				Ontology ont = new Ontology(from, edge, to);
				ontologies.put(edge, ont);
			}
		}
		
		return ontologies;
	}

}
