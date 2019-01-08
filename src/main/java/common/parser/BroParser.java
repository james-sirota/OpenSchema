package common.parser;

import java.io.Serializable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroParser extends AbstractSchemadParser implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2034152009487505395L;
	private final Logger logger = LoggerFactory.getLogger(BroParser.class);

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject parse(String message) throws ParseException {
		
		logger.debug("Looking at raw bro message: " + message);
		
		JSONObject parsed = new JSONObject();
		
		 JSONParser parser = new JSONParser();
		 JSONObject jsonObject = (JSONObject) parser.parse(message);
		 
		 parsed.put("protocol", jsonObject.keySet().toArray()[0]);
		 
		 JSONObject innerObject = (JSONObject) jsonObject.get(jsonObject.keySet().toArray()[0]);
		 
		 for(Object s :  innerObject.keySet())
			 parsed.put(s.toString(), innerObject.get(s));
		 
		 logger.debug("Parsed raw message: " + message + " to " + parsed);
		 
		return parsed;
	}

}
