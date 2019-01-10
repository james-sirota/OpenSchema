package common.utils.schema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigFileReader {
	

	public Map<String, String> getConfig(String filename) {
		
		Map<String, String> cnf = new LinkedHashMap<String, String>();
		
		try {
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if(line.charAt(0) != '#')
				{
					String[] parts = line.split(":");
					cnf.put(parts[0].trim(), parts[1].trim());
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return cnf;
	}

}
