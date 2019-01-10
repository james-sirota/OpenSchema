package common.parser;

import java.util.Map;
import java.util.TreeMap;

public abstract class ConfiguredParser {
	
	private Map<String, String> configuration = new TreeMap<String, String> ();
	
	public ConfiguredParser(Map<String, String> config)
	{
		this.configuration = config;
	}
	
	public void setConfigMap(Map<String, String> config)
	{
		this.configuration = config;
	}

	public void setConfigItem(String key, String value)
	{
		configuration.put(key, value);
	}
	
	public Map<String, String> getConfig()
	{
		return configuration;
	}
	
	public String getConfigItem(String key)
	{
		return configuration.get(key);
	}
}
