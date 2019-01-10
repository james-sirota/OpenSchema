package common.converters;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import common.schematypes.Field;
import common.utils.schema.ParserConfig;

public class ElasticConverter implements SchemaConverter{
	
	private ParserConfig config;

	public ElasticConverter(Map<String, String> cnf) {
		config = new ParserConfig(cnf);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void convert(String filename) {
		JSONObject jo = new JSONObject();
		
		for(Field f: config.getFields().values())
		{
			JSONObject type = new JSONObject();
			
			type.put("type", f.getType());
			jo.put(f.getName(), type);
		}
		
		JSONObject indexname = new JSONObject();
		indexname.put("_doc", jo);
		
		JSONObject mappings = new JSONObject();
		mappings.put("mappings", indexname);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(mappings.toString());
		String prettyJsonString = gson.toJson(je);
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename);
			writer.println(prettyJsonString);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
