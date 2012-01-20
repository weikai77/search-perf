package com.linkedin.searchperf.query.sensei;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.json.JSONObject;


public class DataCollector {
  public  Map<String, Set<Object>> collectValues(InputStream dataFile) throws Exception {
    Map<String, Set<Object>> ret = new HashMap<String, Set<Object>>();
    LineIterator lineIterator = IOUtils.lineIterator(dataFile, "UTF-8");
    while (lineIterator.hasNext()) {
      String nextLine = lineIterator.nextLine();
      if (nextLine.indexOf("{") < 0) {
        continue;
      }
      JSONObject line = new JSONObject(nextLine);
      Iterator keys = line.keys();
      while(keys.hasNext()) {
        String key = (String) keys.next();
        add(ret, key, line.opt(key));
      }
    }
    return ret;
  }
  public  void postProcessMultiValues(String facetName, Map<String, Set<Object>> values) {
    Set<Object> facetValues = values.get(facetName);
    if (facetValues == null) {
      throw new IllegalArgumentException("No values for the facet with name " + facetName);
    }
    Set<Object> processedValues = new HashSet<Object>();
    for (Object object : facetValues) {
      String val = object.toString();
      if (val.contains(",")) {
        processedValues.addAll(Arrays.asList(val.split(",")));
      } else {
        processedValues.add(val);
      }
    }
    values.put(facetName, processedValues);
  }
  private  void add(Map<String, Set<Object>> ret, String key, Object obj) {
    if (!ret.containsKey(key)) {
      ret.put(key, new HashSet<Object>());
    }
    ret.get(key).add(obj);
  }
}
