package common.utils.schema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
				if (line.charAt(0) != '#') {
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

	public PublicKey readPublicKey(String filename) throws Exception {

		    byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

		    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		    KeyFactory kf = KeyFactory.getInstance("RSA");
		    return kf.generatePublic(spec);
	}
	
	public PrivateKey readPrivateKey(String filename) throws Exception {

	    byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
	    
	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(spec);
        
        return privKey;
}

}
