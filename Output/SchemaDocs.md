#Fields: 

##Field Name:	 timestamp 

*Description: Epoch timestamp of message* 

	type: Long 

	supertype: epochTime 

Extended keys:

	luc:indexed:true

	luc:persisted:true

	luc:required:false

	luc:stored:true

Mappers to this field:

	ts: timestamp 
##Field Name:	 srcIp 

*Description: Source IP address of communication* 

	type: String 

	supertype: ip 

Extended keys:

	luc:indexed:true

	luc:persisted:true

	luc:required:false

	luc:stored:true

Mappers to this field:

	id.orig_h: srcIp 
##Field Name:	 dstIp 

*Description: Destination IP address of communication* 

	type: String 

	supertype: ip 

Extended keys:

	luc:indexed:true

	luc:persisted:true

	luc:required:false

	luc:stored:true

Mappers to this field:

	id.resp_h: dstIp 
##Field Name:	 srcPort 

*Description: Source tcp port of communication* 

	type: Integer 

	supertype: tcpPort 

Extended keys:

	luc:indexed:true

	luc:persisted:true

	luc:required:false

	luc:stored:true

Mappers to this field:

	id.orig_p: srcPort 
##Field Name:	 dstPort 

*Description: Destination tcp port of communication* 

	type: Integer 

	supertype: tcpPort 

Extended keys:

	luc:indexed:true

	luc:persisted:true

	luc:required:false

	luc:stored:true

Mappers to this field:

	id.resp_p: dstPort 
##Field Name:	 proto 

*Description: Protocol of communication* 

	type: String 

	supertype: NA 

Extended keys:

	luc:indexed:true

	luc:persisted:true

	luc:stored:true

Mappers to this field:

##Field Name:	 userName 

*Description: Username* 

	type: String 

	supertype: NA 

Extended keys:

	luc:indexed:true

	luc:persisted:true

	luc:stored:true

Mappers to this field:

##Field Name:	 method 

*Description: Http Method* 

	type: String 

	supertype: httpMethod 

Extended keys:

Mappers to this field:

##Field Name:	 hostname 

*Description: Hostname of the domain* 

	type: String 

	supertype: domain 

Extended keys:

Mappers to this field:

	host: hostname 
##Field Name:	 request_body_len 

*Description: Length of request* 

	type: Integer 

	supertype: httpcomm 

Extended keys:

Mappers to this field:

##Field Name:	 response_body_len 

*Description: Length of response* 

	type: Integer 

	supertype: httpcomm 

Extended keys:

Mappers to this field:

##Field Name:	 status_code 

*Description: Length of response* 

	type: Integer 

	supertype: httpCode 

Extended keys:

Mappers to this field:

#Supertypes: 

###supertype: ip:validateIP 


```
var IP_REGEXP = /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$|^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/;
				return IP_REGEXP.test(value)
```

###supertype: ip:restrictToLocalIp 


```
var IP_REGEXP = /(^127\.)|(^192\.168\.)|(^10\.)|(^172\.1[6-9]\.)|(^172\.2[0-9]\.)|(^172\.3[0-1]\.)|(^::1$)|(^[fF][cCdD])/;
				return IP_REGEXP.test(value)
```

###supertype: ip:restrictToIpv4 


```
var IP_REGEXP = /^(?=\d+\.\d+\.\d+\.\d+$)(?:(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\.?){4}$/;
				return IP_REGEXP.test(value)
```

###supertype: tcpPort:validatePort 


```
return value > 0
```

###supertype: epochTime:validateEpochTimestamp 


```
return value > 0
```

###supertype: httpMethod:validateHttpMethod 


```
return ("PUT".localeCompare(value) == 0) || ("GET".localeCompare(value) == 0)
```

###supertype: domain:validateDomain 


```
var DOMAIN_REGEXP = /^[a-zA-Z0-9][a-zA-Z0-9-_]{0,61}[a-zA-Z0-9]{0,1}\.([a-zA-Z]{1,6}|[a-zA-Z0-9-]{1,30}\.[a-zA-Z]{2,3})$/;
				return DOMAIN_REGEXP.test(value)
```

###supertype: httpcomm:validateHttpcomm 


```
return value >= 0
```

###supertype: httpCode:validateHttpCode 


```
return value >= 100 || value >= 600
```

###supertype: httpCode:tagHttpCode 


```
if (value >= 100 || value > 200) 
				{
  					TAG = "Unusual Code";
				}
```


