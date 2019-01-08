package common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import common.parser.BroParser;

public class Driver {

	public static void main(String args[]) throws IOException, ParseException, ScriptException 
	{

		String fileName = "./src/test/resources/BroExampleOutput";
		FileInputStream fstream = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;

		BroParser bp = new BroParser();
		
		while ((strLine = br.readLine()) != null) 
		{

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
			Map<Object, Boolean> valid = bp.schemaEnforce(normalizedMessage);
			System.out.println(valid);
			System.out.println();

			System.out.println("Valid supertype Fields: ");
			valid = bp.supertypeEnforce(normalizedMessage);
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
		}

		fstream.close();

	}

}
