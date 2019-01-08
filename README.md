# OpenSchema

This is a flexible schema designed for high-throughput streaming applications.  The design principles for the schema are as follows: (a) it needs to provide rigidity where appropriate, (b) it needs to be flexible where appropriate, (c) it needs to be extensible, (d) it needs to have multiple levels of enforcement

# Schema Structure


A [schema](https://github.com/james-sirota/OpenSchema/blob/master/Schemas/Common.schema) consists of 4 parts:

| Component        | Description                                                                                              | 
| -------------    |--------------------------------------------------------------------------------------------------------  |
| fields           | Can have a basic type (Integer, Long, String), be required or optional, or belong to a supertype         | 
| supertypes       | Field types with special restrictions (regex patterns if string or numeric ranges if numeric             |   
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
<supertype name="ip">
  <script name="validateIP" type="validation">
    var IP_REGEXP = /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$|^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/;
    IP_REGEXP.test("%THIS")
  </script>
</supertype>
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

# Schema Mappers

Multiple telemetries or enrichments may have a variety of field names that should be mapped to a standard field defined by a schema.  These mappings can be defiled by a [mapping file](https://github.com/james-sirota/OpenSchema/blob/master/Mappers/Global.mapper)

A structure of a mapping entry is as fllows:

```
<mapping name="srcIp">
		<field>id.orig_h</field>
</mapping>
 ```
A mapping can have a list of raw message fields from multiple telemetries that will auto-map by the framework to a known field that has a defined schema
