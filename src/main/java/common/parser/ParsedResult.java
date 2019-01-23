package common.parser;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

public class ParsedResult {

	private JSONObject parsedMessage;
	private Map<Object, Boolean> validatedFields;
	private Set<Object> schemadFields;
	private Set<String> traits;
	private Set<HistoryEvent> provenance = new LinkedHashSet<HistoryEvent>();
	private PrivateKey pKey;
	private Set<String> ontologies;

	private String originalMessage;

	public ParsedResult(PrivateKey pKey) {
		this.pKey = pKey;
	}

	public String getOriginalMessage() {
		return originalMessage;
	}

	public void setOriginalMessage(String originalMessage) throws NoSuchAlgorithmException, UnknownHostException {
		this.originalMessage = originalMessage;

		if(pKey != null)
		{
			HistoryEvent he = new HistoryEvent(originalMessage, this.pKey);
			provenance.add(he);
		}
	}

	public Set<String> getTraits() {
		return traits;
	}

	public void setTraits(Set<String> traits) {
		this.traits = traits;
	}

	public Set<HistoryEvent> getProvenance() {
		return provenance;
	}

	public void setProvenance(Set<HistoryEvent> provenance) {
		this.provenance = provenance;
	}

	public Set<Object> getSchemadFields() {
		return schemadFields;
	}

	public void setSchemadFields(Set<Object> schemadFields) {
		this.schemadFields = schemadFields;
	}

	public JSONObject getParsedMessage() {
		return parsedMessage;
	}

	public void setParsedMessage(JSONObject parsedMessage) throws IOException, NoSuchAlgorithmException {

		if (this.parsedMessage == null) {

			if(pKey != null)
			{
				HistoryEvent he = new HistoryEvent(parsedMessage.toString(), this.pKey);
				provenance.add(he);
			}
		} else {

			ObjectMapper jacksonObjectMapper = new ObjectMapper();
			JsonNode beforeNode = jacksonObjectMapper.readTree(this.parsedMessage.toJSONString());
			JsonNode afterNode = jacksonObjectMapper.readTree(parsedMessage.toString());
			JsonNode patch = JsonDiff.asJson(beforeNode, afterNode);
			String diffs = patch.toString();

			if(pKey != null)
			{
				HistoryEvent he = new HistoryEvent(diffs, this.pKey);
				provenance.add(he);
			}
		}

		this.parsedMessage = parsedMessage;

	}

	public Map<Object, Boolean> getValidatedFields() {
		return validatedFields;
	}

	public Set<String> getOntologies() {
		return ontologies;
	}

	public void setOntologies(Set<String> ontologies) {
		this.ontologies = ontologies;
	}

	public void setValidatedFields(Map<Object, Boolean> map) {

		if (this.validatedFields == null)
			this.validatedFields = map;
		else
			for (Object o : map.keySet()) {
				if (validatedFields.get(o).equals(true)) {
					validatedFields.put(o, map.get(o));
				}
			}

	}

}
