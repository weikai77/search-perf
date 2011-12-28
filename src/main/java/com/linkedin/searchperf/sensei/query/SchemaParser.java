package com.linkedin.searchperf.sensei.query;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class SchemaParser {
  private  ThreadLocal<String> rangeName = new ThreadLocal<String>();

  public  SchemaMetadata parse(InputStream inputStream) throws Exception {
    SchemaMetadata queryMetadata = new SchemaMetadata();
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(inputStream);
    while (reader.hasNext()) {
      int eventType = reader.next();
      if (eventType == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("facet")) {
        Map<String, String> attributeValues = getAttributeValues(reader);
        String facetName = attributeValues.get("name");
        String facetType = attributeValues.get("type");
        if (!facetType.equals("range")) {
          queryMetadata.put(facetName, facetType);
        } else {
          rangeName.set(facetName);
        }
      }
      if (eventType == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("param")) {
        if (rangeName.get() == null) {
          continue;
        }
        Map<String, String> attributeValues = getAttributeValues(reader);
        queryMetadata.putRange(rangeName.get(), attributeValues.get("value"));
      }
      if (eventType == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("facet")) {
        rangeName.set(null);
      }
    }
    reader.close();
    return queryMetadata;
  }

  private  Map<String, String> getAttributeValues(XMLStreamReader reader) {
    Map<String, String> ret = new HashMap<String, String>(reader.getAttributeCount());
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      ret.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
    }
    return ret;
  }
}
