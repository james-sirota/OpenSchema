package common.parser;

import java.net.InetAddress;
import java.security.PrivateKey;
import java.security.Signature;
import java.time.Instant;

import org.apache.commons.codec.binary.Base64;

public class HistoryEvent {

	Long timestamp;
	String ip;
	String userKey;
	String event;

	public HistoryEvent(String event, PrivateKey key)  
	{
		try {
		this.timestamp = Instant.now().toEpochMilli();
		this.event = event;
		this.ip = InetAddress.getLocalHost().getHostAddress();

		
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(key);
		sig.update(event.getBytes("UTF8"));
		byte[] signatureBytes = sig.sign();
		
		this.userKey = new String(Base64.encodeBase64(signatureBytes));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		return(timestamp + " : " + ip + " : "  + event + " : " + userKey);
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

}
