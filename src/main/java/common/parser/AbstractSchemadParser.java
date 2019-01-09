package common.parser;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.schematypes.Field;
import common.schematypes.Ontology;
import common.schematypes.SuperType;
import common.schematypes.Trait;
import common.utils.ParserConfig;

public abstract class AbstractSchemadParser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5677809136509623225L;
	private final Logger logger = LoggerFactory.getLogger(AbstractSchemadParser.class);

	private ParserConfig config = new ParserConfig("./Schemas", "./Schemas", "./Mappers");
	private ScriptEngineManager mgr = new ScriptEngineManager();
	private ScriptEngine engine = mgr.getEngineByName("JavaScript");

	public abstract JSONObject parse(String message) throws ParseException;
	
	public void initEngine() {
		// build validation functions
		
		config.getSuperTypes().values().forEach(s -> {
			StringBuilder sb = new StringBuilder();
			sb.append("function validate_");
			sb.append(s.getName());
			sb.append("(value, message){");
			sb.append(String.format("TYPE='%s';", s.getType())); 
			sb.append(s.getScript());
			sb.append("}\n");
			try {
				engine.eval(sb.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject normalize(JSONObject message) throws ParseException {

		logger.debug("Looking at raw message: " + message);

		JSONObject tmp = new JSONObject();
		Set<Object> toRemove = new HashSet<Object>();

		for (Object key : message.keySet()) {
			logger.debug("Looking at key: " + key);

			if (config.getMappers().keySet().contains(key)) {
				logger.debug("Found schema for key: " + key);

				Object removed = message.get(key);
				logger.debug("Saving value prior to key removal: " + removed);

				tmp.put(config.getMappers().get(key), removed);
				toRemove.add(key);
			}
		}

		message.keySet().removeAll(toRemove);
		message.putAll(tmp);

		logger.debug("Returning normalized message: " + message + " in message " + message);
		return message;

	}

	public Set<Object> getSchemadFields(JSONObject message) {

		logger.debug("Looking at normalized message: " + message);

		Set<Object> foundFields = new HashSet<Object>();

		for (Object s : message.keySet()) {
			logger.debug("Looking schemad field: " + s);

			if (config.getFields().keySet().contains(s)) {
				logger.debug("Found schemad field: " + s);
				foundFields.add(s);
			}
		}


		return foundFields;
	}

	@SuppressWarnings("unchecked")
	public Map<Object, Boolean> schemaEnforce(JSONObject message) {
		logger.debug("Looking at schemad message: " + message);

		Set<Object> foundFields = getSchemadFields(message);
		Map<Object, Boolean> valid = new TreeMap<Object, Boolean>();

		for (Object s : foundFields) {

			Field f = config.getFields().get(s);

			if (f.getType().equals("java.lang.Integer")) {
				message.put(s, Integer.parseInt(message.get(s).toString()));
			} else if (f.getType().equals("java.lang.Long")) {
				message.put(s, Long.parseLong(message.get(s).toString()));
			}

			logger.debug(s + " " + message.get(s).getClass() + " " + f.getType());

			if (f.getType().equals(message.get(s).getClass().getName())) {
				logger.debug(s + " conforms to schema?: " + true);
				valid.put(s, true);
			} else {
				logger.debug(s + " conforms to schema?: " + false);
				valid.put(s, false);
			}

		}

		return valid;
	}

	public Map<Object, Boolean> supertypeEnforce(JSONObject message) throws ScriptException {
		logger.debug("Enforcing super types for message : " + message);

		Set<Object> foundFields = getSchemadFields(message);
		Map<Object, Boolean> valid = new TreeMap<Object, Boolean>();

		for (Object s : foundFields) {
			String f = config.getFields().get(s).getSuperType();

			if (f != null) {
				String key = f + "validation";
				logger.debug("Evaluating key: " + key);

				SuperType st = config.getSuperTypes().get(key);

				logger.debug("Loading script for: " + s + " " + message.get(s).getClass() + " " + st.getScript());

				boolean result;
				try {
					Object obj = invocable().invokeFunction("validate_" + st.getName(), message.get(s), message.values());
					result = (boolean) obj; 
					valid.put(s, result);
				} catch (NoSuchMethodException e) {
					valid.put(s, false);
					throw new IllegalStateException("Validation function does not exist in script engine, has it been inited?", e);
				}
			}

		}

		return valid;
	}

	private Invocable invocable() {
		 return (Invocable) engine;
	}

	@SuppressWarnings("unchecked")
	public Set<String> extractTraits(JSONObject message) {

		logger.debug("Looking for traits in: " + message);

		Map<String, Trait> traits = config.getTraits();
		Set<String> traitsFound = new TreeSet<String>();

		for (String s : traits.keySet()) {
			boolean match = traits.get(s).matchTrate(message.keySet());

			if (match) {
				logger.debug("Found trait: " + traits.get(s).getName() + " in message " + message);
				traitsFound.add(traits.get(s).getName());
			}
		}

		return traitsFound;
	}

	public Set<String> getOntologies(JSONObject message) {

		logger.debug("Looking for Ontologies in: " + message);

		Map<String, Ontology> ontologies = config.getOntologies();
		Set<String> ontologiesFound = new TreeSet<String>();

		for (String s : ontologies.keySet()) {
			String key1 = ontologies.get(s).getFrom();
			String key2 = ontologies.get(s).getTo();

			if (message.keySet().contains(key1) && message.keySet().contains(key2)) {
				logger.debug("Found ontology: " + ontologies.get(s).getOntology() + " in message " + message);
				ontologiesFound.add(ontologies.get(s).getOntology());
			}
		}

		return ontologiesFound;
	}
}
