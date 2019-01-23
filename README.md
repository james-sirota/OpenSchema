# OpenSchema

This is a flexible schema designed for high-throughput streaming applications.  The design principles for the schema are as follows: (a) it needs to provide rigidity where appropriate, (b) it needs to be flexible where appropriate, (c) it needs to be extensible, (d) it needs to have multiple levels of enforcement

# Schema Structure


A [schema](https://github.com/james-sirota/OpenSchema/blob/master/Schemas/Common.schema) consists of 4 parts:

| Component        | Description                                                                                              | 
| -------------    |--------------------------------------------------------------------------------------------------------  |
| fields           | Can have a basic type (Integer, Long, String), be required or optional, or belong to a supertype         | 
| supertypes       | Global field types with special restrictions (regex patterns if string or numeric ranges if numeric      |
| restrictions     | A more restricted supertype that is enforced on per-sensor level    				      |
| traits           | A collection of fields.  If all fields of a trait are present in the message then it has this trait      | 
| ontologies       | Semantic relationships between different fields of a message defined by a verb                           |   
  
### Fields

```
<field name="srcIp" type="java.lang.String" supertype="ip" description="Source IP address of communication" required="false"/>
```
Fields have the following required elements:

- name: name of the field to which the schema is to be applied
- type: a basic type (Integer, Long, String, etc)
- supertype: a complex type that is enforced by a validation script

Multiple basic fields can belong to the same supertype.  For example:

```
<field name="srcIp" type="java.lang.String" supertype="ip" ...
<field name="dstIp" type="java.lang.String" supertype="ip" ...
```

Some fields may be designated as required and message validation will fail if they are not present.  A timestamp is a good example of that:

```
<field name="timestamp" type="java.lang.Long" supertype="epochTime" required="true" description="Epoch timestamp of message"/>
```

### Supertypes

A supertype is a special complex type that can have several script types associated with us.  It can have a validation script that has to return a boolean, true if validation is successful and false if it is not.  An example of a validation scrit is below
```
<supertype name="tcpPort">
	<script name="validatePort" type="validation">
		return value > 0
	</script>
```
It can also have an auto-tagging script.  An example can be seen below:

```
<supertype name="httpCode">
  <script name="validateHttpCode" type="validation">
    %THIS >= 100 || %THIS >= 600
  </script>
  <script name="tagHttpCode" type="autotag">
    if (%THIS >= 100 || %THIS > 200) 
    {
      %TAG = "Unusual Code";
    }
  </script>
</supertype>
 ```
This feature may be extended and other types of scripts may be supported in the future.  If a field has an associated supertype then a complex validation will be run via a script.  At present only JavaScript validation is supported, but it can easily be extended to other kinds of scripts

### Restrictions

Each supertype may have a restriction that is defined on a per-sensor basis.  An example of a restriction can be [seen here](https://github.com/james-sirota/OpenSchema/blob/master/Sensor-Specific/Restrictions.xml)

A restriction definition is defined in the supertype like so:

```
<script name="restrictToLocalIp" type="restriction">
	var IP_REGEXP = /(^127\.)|(^192\.168\.)|(^10\.)|(^172\.1[6-9]\.)|(^172\.2[0-9]\.)|(^172\.3[0-1]\.)|(^::1$)|(^[fF [cCdD])/;
	return IP_REGEXP.test(value)
</script>
```

And then mapped to a sensor like so:

```
<restriction parserName = "bro_test" fieldName = "srcIp" restrictionName="ip:restrictToLocalIp"/>
```

Here we can see a parser named bro_test that will be applying a restriction called restrictToLocalIp of the supertype ip to the field srcIp

### Traits

A trait is a collection of fields.  An example of a trait is shown below:
```
<trait name="communication" description="Basic communication">
  <element name="srcIp"/>
  <element name="dstIp"/>
  <element name="srcPort"/>
  <element name="dstPort"/>
  <element name="protocol"/>
</trait>
```
If a message has all the fields present in a trait definition then it is said to have that trat.  There can be multiple traits in a message

### Ontologies

A message can have several semantic relationships between different fields.  These relationships are captured via an ontology.  An example is shown below:

```
<ontology source="srcIp" edge="connectsTo" dest="dstIp"/>
```
Ontologies are designed to be ingested into large-scale graph databases.  They are directional and consist of a source node, a destination node, and an edge between them that is generally presented in a form of a verb

# Schema Extensions

There are two types of schemas supported: 

```
<schema type="common" version="0.1"
<schema type="extension" version="0.1"
```
A common schema is the basic core schema to be used by the system.  An n number of schema extension can be appended to the common schema as additional telemetries or enrichments are added.  Each can have its own schema extension.  Each extension can add additional fields, supertypes, traits, or ontologies to the common schema.  An example of a schema extension can be seen [here](https://github.com/james-sirota/OpenSchema/blob/master/Schemas/Protocol_HTTP.schema_extension)

# Schema Additions
Schema extensions are meant to be immutable, but schema additions are meant to be use-case specific and put under CM control.  Schema additions are additional attributes that can be added to each field.  Each schema extension may also have a schema addition.  An example of a schema addition can be seen [here](https://raw.githubusercontent.com/james-sirota/OpenSchema/master/Schemas/Common.lucene.schema_addition)

# Schema Mappers

Multiple telemetries or enrichments may have a variety of field names that should be mapped to a standard field defined by a schema.  These mappings can be defiled by a [mapping file](https://github.com/james-sirota/OpenSchema/blob/master/Mappers/Global.mapper)

A structure of a mapping entry is as fllows:

```
<mapping name="srcIp">
	<field>id.orig_h</field>
</mapping>
 ```
A mapping can have a list of raw message fields from multiple telemetries that will auto-map by the framework to a known field that has a defined schema

# Index Template Generators

It is possible to generate Solr and Elastic templates from the schema fields.  To do so you must implement the following optional attributes on a field you wish to be included in a template:

```
<field name="srcIp" ... required="false" indexed="true" stored="true" persisted="true" ...
```

### Solr Templates

A utility is provided generate a Solr template from the schema definition.  This can be done by:

```
SchemaConverter cnv = (SchemaConverter) new SolrConverter(parserConfig);
cnv.convert("./Output/Solr.schema");
```
A Solr template will be generated and writte out to a file.  An example Solr template is provided [here](https://github.com/james-sirota/OpenSchema/blob/master/Output/Solr.schema)

### Elastic Templates

A utility is provided generate a Elastic template from the schema definition.  This can be done by:

```
cnv = (SchemaConverter) new ElasticConverter(parserConfig);
cnv.convert("./Output/Elastic.schema");
```
A Solr template will be generated and writte out to a file.  An example Elastic template is provided [here](https://github.com/james-sirota/OpenSchema/blob/master/Output/Elastic.schema)

# Automatic Documentation Generation

It is possible to automatically generate the documentation for the schema in the markup format.  This can be done like so:

```
DocumentationGenerator dg = new DocumentationGenerator(parserConfig);
dg.generate("./Output/SchemaDocs.md");
```
With the example schemas provided this will generate the [following documentation](https://github.com/james-sirota/OpenSchema/blob/master/Output/SchemaDocs.md)

# Provenance 

It is possible to keep an audit log of all modifications that happened to the message from the time it was received by the parser.  The audit history is then signed to prevent unautorhized changes.  Here is how to setup this feature.  First a key pair needs to be created:

```
openssl genrsa -out private_key.pem 2048
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der
```
A sample set of keys is pre-generated and provided [here](https://github.com/james-sirota/OpenSchema/tree/master/Keys)

Once the keys have been generated a special parseWithProvenance method needs to be called like so:

```
PrivateKey key = cfr.readPrivateKey("./Keys/private_key.der");
ParsedResult result = bp.parseWithProvenance(strLine, key);
result.getProvenance()
```
Which will produce the provenance chain:

```
Event:1547110011388 : 192.168.86.248 : {"http":{"ts":1402307733473,"uid":"CTo78A11g7CYbbOHvj","id.orig_h":"192.249.113.37","id.orig_p":58808,"id.resp_h":"72.163.4.161","id.resp_p":80,"trans_depth":1,"method":"GET","host":"www.cisco.com","uri":"/","user_agent":"curl/7.22.0 (x86_64-pc-linux-gnu) libcurl/7.22.0 OpenSSL/1.0.1 zlib/1.2.3.4 libidn/1.23 librtmp/2.3","request_body_len":0,"response_body_len":25523,"status_code":200,"status_msg":"OK","tags":[],"resp_fuids":["FJDyMC15lxUn5ngPfd"],"resp_mime_types":["text/html"]}} : VB1h4cJpAH+4pc15xwPZPIWWuZiD54VIaVbvPlUKc4EgmQ6NtFoaD6YkKdHHF87/hYmTe/MKz+ltuLEAotvVvuft4TomAZItdVuSb07iCL1DIUq7/kRdAwj9sdWaF+mUXrmE+h1SR7KBWWoy67aQBWplIi3BUrRB112E/PZlMzS+q/217aul3tBDOxLKfluFGRpEUdXJgT/wpNIKhjyjwbNDtX8PeENmC/e48DlW3jdkD7sUffKnHV42d/E0mzH12uRY2YyingggCDJ13lVf7uOrxuGYI843ptXNAcXbP6QpegAguGI5Djw6IHf4LonHnkebLWIfzPEx9OWGNuhKLw==
Event:1547110011418 : 192.168.86.248 : {"id.orig_p":58808,"status_code":200,"method":"GET","request_body_len":0,"id.resp_p":80,"uri":"\/","tags":[],"uid":"CTo78A11g7CYbbOHvj","resp_mime_types":["text\/html"],"protocol":"http","trans_depth":1,"host":"www.cisco.com","status_msg":"OK","id.orig_h":"192.249.113.37","response_body_len":25523,"user_agent":"curl\/7.22.0 (x86_64-pc-linux-gnu) libcurl\/7.22.0 OpenSSL\/1.0.1 zlib\/1.2.3.4 libidn\/1.23 librtmp\/2.3","ts":1402307733473,"id.resp_h":"72.163.4.161","resp_fuids":["FJDyMC15lxUn5ngPfd"]} : ZxBEUWRlUOM3ZPkg7wW+bUkw+eixa6i35R9E52wi/lPLOMMN3zQi/l6Z/Ckp3p5DKVWAQXTJ1LTf0YAc1XQ+UFWLQjoofBTA5Ez3VES4dG+f9FQJTxSgxjKGD7oA1OQIogc8gRziUgOTINyqhvU0djDbg9n35/LnJYZ+CdPQ59mCmAaW8D1RTNRvK2kVghr/Miw/uZRIs2VVHTrqX30pWLwutsu81TZIBcJl1dXRImUaHsBT9bvO00pvZdtyhQfqIqk9ft+clfjMaPTaZhzRaOfVAQu5SJppjM8WJ/EXwuZSOpQqf6ajUyPoWUubit9ayNvFa+Ly10MSiPyoj/CHLw==
Event:1547110011556 : 192.168.86.248 : [{"op":"move","from":"/id.orig_p","path":"/srcPort"},{"op":"move","from":"/id.resp_p","path":"/dstPort"},{"op":"move","from":"/host","path":"/hostname"},{"op":"move","from":"/id.orig_h","path":"/srcIp"},{"op":"move","from":"/ts","path":"/timestamp"},{"op":"move","from":"/id.resp_h","path":"/dstIp"}] : rBkI1OQurLlfgE5Gf9oRvSzyF1lVJXHUjdeeAWa2/mrGlqpS7Dcx6nGFid+22QBg0W0s0NoXXkEpFoq4ACsKJVtRbH3eghO1OABp0ch/TEKGsaM/bDO9mgU5CJfUzpp/68FsXc6JQ9upxq3ZcL8ZyxpWsciZyUv4OKq9qwCFtgT3gOSZcjlPbatDyLPNReA09gHIn4xodoxSBTYblkU34PjDzoKKsJbnvg8Vvrw+wQQbW+W/kR8K2CCRfWLlpRcXXFOTPlRHbOwKCKdi36dzCJxjyb0BJGco9dkauR3DR4jFSMcjC16ch/xyoz53yJUIsb2dvxTHUqirZtjS0QIllA==


```

The system will capture the timestamp, ip of machine where the event was run, a summary of changes, and the signature via the key provided

# A Working Example

[An example Bro HTTP parser](https://github.com/james-sirota/OpenSchema/blob/master/src/main/java/common/Driver.java) is provided to show how a parser would use a schema and its features.

Given a raw bro HTTP message:

```
{"http":{"ts":1402307733473,"uid":"CTo78A11g7CYbbOHvj","id.orig_h":"192.249.113.37","id.orig_p":58808,"id.resp_h":"72.163.4.161","id.resp_p":80,"trans_depth":1,"method":"GET","host":"www.cisco.com","uri":"/","user_agent":"curl/7.22.0 (x86_64-pc-linux-gnu) libcurl/7.22.0 OpenSSL/1.0.1 zlib/1.2.3.4 libidn/1.23 librtmp/2.3","request_body_len":0,"response_body_len":25523,"status_code":200,"status_msg":"OK","tags":[],"resp_fuids":["FJDyMC15lxUn5ngPfd"],"resp_mime_types":["text/html"]}}
```
### Step 1 

Call a parser to parse it:

```
BroParser bp = new BroParser(parserConfig);
		
//overwrite config items for demo purposes
bp.setConfigItem("parse.normalizeEnable", "false");
bp.setConfigItem("parse.basicSchemaEnforceEnable", "false");
bp.setConfigItem("parse.supertypeEnforceEnable", "false");
bp.setConfigItem("parse.restrictionEnforce", "false");

ParsedResult result = bp.parseMessage(strLine);
System.out.println(result.getParsedMessage());

```
The parser produces the following JSON:

```
{"id.orig_p":58808,"status_code":200,"method":"GET","request_body_len":0,"id.resp_p":80,"uri":"\/","tags":[],"uid":"CTo78A11g7CYbbOHvj","resp_mime_types":["text\/html"],"protocol":"http","trans_depth":1,"host":"www.cisco.com","status_msg":"OK","id.orig_h":"192.249.113.37","response_body_len":25523,"user_agent":"curl\/7.22.0 (x86_64-pc-linux-gnu) libcurl\/7.22.0 OpenSSL\/1.0.1 zlib\/1.2.3.4 libidn\/1.23 librtmp\/2.3","ts":1402307733473,"id.resp_h":"72.163.4.161","resp_fuids":["FJDyMC15lxUn5ngPfd"]}
```
### Step 2 

Normalize the field names of a parsed message by running it through a mapper that maps them to known fields for which we have schema:

```
bp.setConfigItem("parse.normalizeEnable", "true");
result = bp.parseMessage(strLine);
```
Which produces the following output:

```
{"srcIp":"192.249.113.37","status_code":200,"method":"GET","request_body_len":0,"srcPort":58808,"uri":"\/","tags":[],"uid":"CTo78A11g7CYbbOHvj","resp_mime_types":["text\/html"],"protocol":"http","trans_depth":1,"hostname":"www.cisco.com","dstPort":80,"status_msg":"OK","dstIp":"72.163.4.161","response_body_len":25523,"user_agent":"curl\/7.22.0 (x86_64-pc-linux-gnu) libcurl\/7.22.0 OpenSSL\/1.0.1 zlib\/1.2.3.4 libidn\/1.23 librtmp\/2.3","resp_fuids":["FJDyMC15lxUn5ngPfd"],"timestamp":1402307733473}
```
Now we have a normalized message.  Notice timestamp, dstIp, dstPort, srcIp, srcPort, and hostname fields (for which schema names exist) are now present.   

### Step 3

We can now figure out to which fields in the message we can apply a schema:

```
System.out.println(result.getSchemadFields());
```
Which returns:
```
[srcIp, hostname, status_code, dstPort, method, request_body_len, srcPort, dstIp, response_body_len, timestamp]
```
For the fields above it is possible to enforce a schema.  Other fields in the message do not have an associated schema entry and will not be run through schema validation by the parser

### Step 4 

Now we can validate the basic types for the fields for which we have a schema:

```
bp.setConfigItem("parse.basicSchemaEnforceEnable", "true");
result = bp.parseMessage(strLine, key);
System.out.println(result.getValidatedFields());	
```
And we get a list of fields and the results of whether they were valid or not according to the schema:
```
{dstIp=true, dstPort=true, hostname=true, method=true, request_body_len=true, response_body_len=true, srcIp=true, srcPort=true, status_code=true, timestamp=true}
```
### Step 5

If a field belogs to a supertype we need to run an additional validation step to make sure it adheres to the restrictions of the supertype:

```
bp.setConfigItem("parse.supertypeEnforceEnable", "true");
result = bp.parseMessage(strLine, key);
System.out.println(result.getValidatedFields());
```
And we get a list of fields and the results of whether they were valid or not according to the schema:
```
{dstIp=true, dstPort=true, hostname=true, method=true, request_body_len=true, response_body_len=true, srcIp=true, srcPort=true, status_code=true, timestamp=true}
```
The boolean false means we do not enforce sensor-specific restrictions

### Step 6

To enforce sensor specific restrictions do:

```
bp.setConfigItem("parse.restrictionEnforce", "true");
result = bp.parseMessage(strLine, key);
System.out.println(result.getValidatedFields());
```
And we get a list of fields and the results of whether they were valid or not according to the schema:
```
{dstIp=true, dstPort=true, hostname=true, method=true, request_body_len=true, response_body_len=true, srcIp=false, srcPort=true, status_code=true, timestamp=true}
```

We can see that srcIp=false because it does not conform to a restriction of being a local IP

### Step 7

We now need to check if the message has any traits associated with it:

```
System.out.println(result.getTraits());
```
which returns all traits that match:
```
[communication, httpCommInbound, httpCommOutbound, httpCommTwoWay]
```

### Step 8

And now we can extract ontologies from the message:

```
System.out.println(result.getOntologies());
```
Which gives us the following result:
```
[dstIp -- resolvesTo --> hostname, srcIp -- connectsTo --> dstIp]
```
