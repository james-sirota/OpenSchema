package common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import common.converters.ElasticConverter;
import common.converters.SchemaConverter;
import common.converters.SolrConverter;
import common.parser.HistoryEvent;
import common.parser.ParsedResult;
import common.parser.sensors.BroParser;
import common.utils.docs.DocumentationGenerator;
import common.utils.schema.ConfigFileReader;

public class Driver {

	public static void main(String args[]) throws Exception {

		String fileName = "./src/test/resources/BroExampleOutput";
		FileInputStream fstream = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;

		ConfigFileReader cfr = new ConfigFileReader();
		Map<String, String> parserConfig = cfr.getConfig("Schema.conf");

		BroParser bp = new BroParser(parserConfig);
		
		//overwrite config items for demo purposes
		bp.setConfigItem("parse.normalizeEnable", "false");
		bp.setConfigItem("parse.basicSchemaEnforceEnable", "false");
		bp.setConfigItem("parse.supertypeEnforceEnable", "false");
		bp.setConfigItem("parse.restrictionEnforce", "false");
		
	
		PrivateKey key = cfr.readPrivateKey("./Keys/private_key.der");
		

		while ((strLine = br.readLine()) != null) {
			
			ParsedResult result = bp.parseMessage(strLine);

			System.out.println("Raw message: ");
			System.out.println(result.getOriginalMessage());
			System.out.println();

			System.out.println("Parsed message: ");			
			System.out.println(result.getParsedMessage());
			System.out.println();

			System.out.println("Normalized message: ");			
			bp.setConfigItem("parse.normalizeEnable", "true");
			result = bp.parseMessage(strLine, key);
			System.out.println(result.getParsedMessage());
			
			System.out.println();

			System.out.println("Schemad Fields: ");
			System.out.println(result.getSchemadFields());
			System.out.println();

			System.out.println("Valid schemad Fields: ");
			bp.setConfigItem("parse.basicSchemaEnforceEnable", "true");
			result = bp.parseMessage(strLine, key);
			System.out.println(result.getValidatedFields());			
			System.out.println();

			bp.setConfigItem("parse.supertypeEnforceEnable", "true");
			result = bp.parseMessage(strLine, key);
			System.out.println(result.getValidatedFields());
			System.out.println();

			System.out.println("Enforce restrictions: ");			
			bp.setConfigItem("parse.restrictionEnforce", "true");
			result = bp.parseMessage(strLine, key);
			System.out.println(result.getValidatedFields());
			
			System.out.println();

			System.out.println("Extracted traits: ");
			System.out.println(result.getTraits());
			System.out.println();

			System.out.println("Extracted ontologies: ");
			System.out.println(result.getOntologies());
			System.out.println();
			
			
		
			SchemaConverter cnv = (SchemaConverter) new SolrConverter(parserConfig);
			cnv.convert("./Output/Solr.schema");
			
			cnv = (SchemaConverter) new ElasticConverter(parserConfig);
			cnv.convert("./Output/Elastic.schema");
			
			DocumentationGenerator dg = new DocumentationGenerator(parserConfig);
			dg.generate("./Output/SchemaDocs.md");
			

			
			System.out.println("Provenance: ");
			for(HistoryEvent he : result.getProvenance())
			{
				System.out.println("Event:" + he);
			}
			System.out.println();
		}

		fstream.close();

	}
	
	
	

}
