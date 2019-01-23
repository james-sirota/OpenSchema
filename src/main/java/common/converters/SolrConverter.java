package common.converters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import common.schematypes.Field;
import common.utils.schema.ParserConfig;

public class SolrConverter implements SchemaConverter {

	private ParserConfig config;
	private final String PREFIX = "luc";

	public SolrConverter(Map<String, String> cnf) {
		config = new ParserConfig(cnf);
	}

	@Override
	public void convert(String filename) {
		Document dom;
		Element e = null;

		// instance of a DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// use factory to get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// create instance of DOM
			dom = db.newDocument();

			// create the root element
			Element rootEle = dom.createElement("fields");

			for (Field f : config.getFields().values()) {
				
				//System.out.println("looking at field: " + f.toString());
				
				String prefixed_key = PREFIX + ":" + "indexed";
				
				if(f.getAllExtendedKeys().containsKey(prefixed_key))
				{

				Boolean indexed = Boolean.parseBoolean(f.getExtendedFieldByKey(prefixed_key).toString());
				
				if (indexed != null && indexed) {
					Element field = dom.createElement("field");
					
					Map<String, Object> extended = f.getAllExtendedKeys();
					
					extended.forEach((key, value) -> {
						
						if(key.startsWith(PREFIX))
							//System.out.println("Looking at key: " + key);
							field.setAttribute(key.split(":")[1], value.toString());
						});
					

					field.setAttribute("name", f.getName());
					field.setAttribute("type", f.getType());
					rootEle.appendChild(field);
				}
				}
			}

			dom.appendChild(rootEle);

			try {
				Transformer tr = TransformerFactory.newInstance().newTransformer();
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
				tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

				File yourFile = new File(filename);
				yourFile.createNewFile();
				// send DOM to file
				tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(filename)));

			} catch (TransformerException te) {
				System.out.println(te.getMessage());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		} catch (ParserConfigurationException pce) {
			System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
		}
	}

}
