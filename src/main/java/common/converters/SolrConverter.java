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

				if (f.getIndexed()) {
					Element field = dom.createElement("field");

					if (f.getStored() != null)
						field.setAttribute("stored", f.getStored().toString());

					if (f.getRequired() != null)
						field.setAttribute("required", f.getRequired().toString());

					if (f.getIndexed() != null)
						field.setAttribute("indexed", f.getIndexed().toString());

					field.setAttribute("name", f.getName());
					field.setAttribute("type", f.getType());
					rootEle.appendChild(field);
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
