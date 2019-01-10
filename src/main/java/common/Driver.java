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

		while ((strLine = br.readLine()) != null) {

			System.out.println("Raw message: ");
			System.out.println(strLine);
			System.out.println();

			System.out.println("Parsed message: ");
			JSONObject message = bp.parse(strLine);
			System.out.println(message);
			System.out.println();

			System.out.println("Normalized message: ");
			JSONObject normalizedMessage = bp.normalize(message);
			System.out.println(normalizedMessage);
			System.out.println();

			System.out.println("Schemad Fields: ");
			Set<Object> schemadFields = bp.getSchemadFields(normalizedMessage);
			System.out.println(schemadFields);
			System.out.println();

			System.out.println("Valid schemad Fields: ");
			Map<Object, Boolean> valid = bp.schemaEnforce(schemadFields, normalizedMessage);
			System.out.println(valid);
			System.out.println();

			System.out.println("Enforce supertype Fields: ");
			valid = bp.supertypeEnforce(schemadFields, normalizedMessage, false);
			System.out.println(valid);
			System.out.println();

			System.out.println("Enforce restrictions: ");
			valid = bp.supertypeEnforce(schemadFields, normalizedMessage, true);
			System.out.println(valid);
			System.out.println();

			System.out.println("Extracted traits: ");
			Set<String> traits = bp.extractTraits(normalizedMessage);
			System.out.println(traits);
			System.out.println();

			System.out.println("Extracted ontologies: ");
			Set<String> ontologies = bp.getOntologies(normalizedMessage);
			System.out.println(ontologies);
			System.out.println();
			
			
		
			SchemaConverter cnv = (SchemaConverter) new SolrConverter(parserConfig);
			cnv.convert("./Output/Solr.schema");
			
			cnv = (SchemaConverter) new ElasticConverter(parserConfig);
			cnv.convert("./Output/Elastic.schema");
			
			PrivateKey key = cfr.readPrivateKey("./Keys/private_key.der");
			ParsedResult result = bp.parseWithProvenance(strLine, key);

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
