package common.parser;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Collection;
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
import common.schematypes.Restriction;
import common.schematypes.SuperType;
import common.schematypes.Trait;
import common.utils.schema.ParserConfig;

public abstract class AbstractSchemadParser extends ConfiguredParser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5677809136509623225L;
	private final Logger logger = LoggerFactory.getLogger(AbstractSchemadParser.class);

	private ParserConfig config;
	private ScriptEngineManager mgr = new ScriptEngineManager();
	private ScriptEngine engine;
	



	public AbstractSchemadParser(Map<String, String> cnf) {
		super(cnf);
		logger.debug("Initializing parser: " + getConfigItem("parserName"));

		config = new ParserConfig(cnf);

		logger.debug("Initializing script engine: " + getConfigItem("scriptType"));
		engine = mgr.getEngineByName(getConfigItem("scriptType"));
		initEngine();
		
	}

	protected abstract JSONObject parse(String message) throws ParseException;

	private void initEngine() {
		// build validation functions

		config.getSuperTypes().values().forEach(s -> {
			StringBuilder sb = new StringBuilder();

			sb.append("function " + s.getType() + "_");
			sb.append(s.getName());
			sb.append("(value, message){");
			sb.append(String.format("TYPE='%s';", s.getType()));
			sb.append(s.getScript());
			sb.append("}\n");
			logger.debug(String.format("Initialized script name: %s, with a script: \n%s", s.getName(), sb.toString()));

			try {
				engine.eval(sb.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public ParsedResult parseMessage(String msg) throws Exception
	{
		return parseMessage(msg, null);
	}
	
	public ParsedResult parseMessage(String msg, PrivateKey pKey) throws Exception {
		ParsedResult pr = new ParsedResult(pKey);

		pr.setOriginalMessage(msg);
		pr.setParsedMessage(parse(msg));
		
		if(Boolean.parseBoolean(getConfigItem("parse.normalizeEnable")))
			pr.setParsedMessage(normalize(pr.getParsedMessage()));
		
		pr.setSchemadFields(getSchemadFields(pr.getParsedMessage()));
		
		if(Boolean.parseBoolean(getConfigItem("parse.basicSchemaEnforceEnable")))
		{
			pr.setValidatedFields(schemaEnforce(pr.getSchemadFields(),pr.getParsedMessage()));
			
			if(Boolean.parseBoolean(getConfigItem("parse.supertypeEnforceEnable")))
				pr.setValidatedFields(supertypeEnforce(pr.getSchemadFields(),pr.getParsedMessage(), Boolean.parseBoolean(getConfigItem("parse.restrictionEnforce"))));
		}
		
		pr.setTraits(extractTraits(pr.getParsedMessage()));
		pr.setOntologies(getOntologies(pr.getParsedMessage()));

		return pr;

	}

	@SuppressWarnings("unchecked")
	private JSONObject normalize(JSONObject message) throws ParseException {

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

		JSONObject toReturn = new JSONObject();
		toReturn.putAll(message);

		toReturn.keySet().removeAll(toRemove);
		toReturn.putAll(tmp);

		logger.debug("Returning normalized message: " + toReturn + " in message " + message);
		return toReturn;

	}

	private Set<Object> getSchemadFields(JSONObject message) {

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
	private Map<Object, Boolean> schemaEnforce(Set<Object> foundFields, JSONObject message) {
		logger.debug("Looking at schemad message: " + message);

		Map<Object, Boolean> valid = new TreeMap<Object, Boolean>();

		for (Object s : foundFields) {

			Field f = config.getFields().get(s);

			if (f.getType().equals("Integer")) {
				try {
					message.put(s, Integer.parseInt(message.get(s).toString()));
					valid.put(s, true);
				} catch (Exception e) {
					valid.put(s, false);
				}
			} else if (f.getType().equals("Long")) {

				try {
					message.put(s, Long.parseLong(message.get(s).toString()));
					valid.put(s, true);
				} catch (Exception e) {
					valid.put(s, false);
				}
			}

			String javaClass = "java.lang." + f.getType();
			logger.debug("Comparing classes:" + s + " " + message.get(s).getClass() + " and " + javaClass);

			if (javaClass.equals(message.get(s).getClass().getName())) {
				logger.debug(s + " conforms to schema?: " + true);
				valid.put(s, true);
			} else {
				logger.debug(s + " conforms to schema?: " + false);
				valid.put(s, false);
			}

		}

		return valid;
	}

	private Map<Object, Boolean> supertypeEnforce(Set<Object> fieldsToCheck, JSONObject message,
			boolean withLocalizedRestrictions) throws ScriptException {
		logger.debug("Enforcing super types for message : " + message);

		Map<Object, Boolean> valid = new TreeMap<Object, Boolean>();

		config.getSuperTypes().values().forEach(s -> {

			for (Object f : fieldsToCheck) {
				Field foundField = config.getFields().get(f);

				logger.debug("Looking at field: " + f);

				if (foundField.getSuperType() != null && s.getParentElement().equals(foundField.getSuperType())) {
					logger.debug(String.format("Field: %s has supertype: %s: ", foundField.getName(),
							foundField.getSuperType()));

					if (s.getType().equals("validation")) {
						logger.debug(String.format("Validating: %s against the following script: %s: ",
								foundField.getName(), s.getScript()));

						boolean result = executeScript(s, f, message, "validation_");

						valid.put(f, result);

					}

					if (s.getType().equals("restriction") && withLocalizedRestrictions) {
						Collection<Restriction> restrictions = config.getRestrictions().values();

						restrictions.forEach(r -> {

							if (r.getFieldName().equals(foundField.getName())
									&& foundField.getSuperType().equals(r.getParentSupertype())
									&& r.getRestrictionName().equals(s.getName())) {
								logger.debug(String.format("Restricting: %s against the following script: %s: ",
										foundField.getName(), s.getScript()));
								boolean result = executeScript(s, f, message, "restriction_");
								valid.put(f, result);
							}
						});
					}
				}
			}
		});

		return valid;
	}

	private boolean executeScript(SuperType s, Object f, JSONObject message, String scriptExtension) {
		boolean result = false;
		try {
			Object obj = invocable().invokeFunction(scriptExtension + s.getName(), message.get(f), message.values());
			result = (boolean) obj;
			// valid.put(f, result);
			logger.debug(String.format("Value: %s evaluated to: %s: ", message.get(f), result));
		} catch (NoSuchMethodException e) {
			// valid.put(f, false);
			logger.debug(String.format("Value: %s evaluated to: %s: ", message.get(f), false));
			throw new IllegalStateException("Validation function does not exist in script engine, has it been inited?",
					e);
		} catch (ScriptException e) {
			e.printStackTrace();
		}

		return result;
	}

	private Invocable invocable() {
		return (Invocable) engine;
	}

	@SuppressWarnings("unchecked")
	private Set<String> extractTraits(JSONObject message) {

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

	private Set<String> getOntologies(JSONObject message) {

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
