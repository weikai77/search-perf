package com.linkedin.searchperf.query.sensei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SchemaMetadata {
  private Map<FacetType, List<String>> facetsPerType = new HashMap<FacetType, List<String>>();
  private Map<String, List<String>> rangeFacetsWithRanges = new LinkedHashMap<String, List<String>>();

  public void put(String facetName, String facetTypeStr) {
    FacetType facetType = FacetType.valueOf(facetTypeStr);
    if (facetType == null)
      throw new IllegalStateException();
    if (!facetsPerType.containsKey(facetType)) {
      facetsPerType.put(facetType, new ArrayList<String>());
    }
    facetsPerType.get(facetType).add(facetName);
  }

  public void putRange(String facetName, String range) {
    put(facetName, "range");
    if (!rangeFacetsWithRanges.containsKey(facetName)) {
      rangeFacetsWithRanges.put(facetName, new ArrayList<String>());
    }
    rangeFacetsWithRanges.get(facetName).add(range);
  }

  public Map<FacetType, List<String>> getFacetsPerType() {
    return facetsPerType;
  }

  public Map<String, List<String>> getRangeFacetsWithRanges() {
    return rangeFacetsWithRanges;
  }

}
