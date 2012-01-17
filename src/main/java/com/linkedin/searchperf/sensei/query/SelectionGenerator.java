package com.linkedin.searchperf.sensei.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.senseidb.search.client.json.req.Operator;
import com.senseidb.search.client.json.req.Selection;
import com.senseidb.search.client.json.req.filter.Filters;

public class SelectionGenerator {
  private final Map<String, Set<Object>> fieldValues;
  private Map<String, CircularIterator<Object>> iterators = new HashMap<String, SelectionGenerator.CircularIterator<Object>>();
  private SchemaMetadata metadata;
  public SelectionGenerator(Map<String, Set<Object>> values, SchemaMetadata metadata) {
    this.fieldValues = values;
    this.metadata = metadata;
  }

  public Selection createSimpleSelection(String fieldName) {
      if (fieldValues.size() < 4) {
        return Filters.term(fieldName, getIterator(fieldName).next().toString());
      }
      if (fieldValues.size() < 15) {
        int sampleNumber = (fieldValues.size() / 2);
        return Filters.terms(fieldName, getSampleValues(fieldName, sampleNumber - 1), getSampleValues(fieldName, (fieldValues.size() - sampleNumber) / 2), Operator.or);
      }
      return Filters.terms(fieldName, Collections.EMPTY_LIST, getSampleValues(fieldName, 5), null);
  }

  public Selection createPathSelection(String fieldName) {
    String path = getIterator(fieldName).next().toString();
    if (path.contains("/")) {
      path = path.substring(0, path.indexOf("/"));
    }
    if (path.contains("\\")) {
      path = path.substring(0, path.indexOf("\\"));
    }
    return Filters.path(fieldName, path, false, 0);
  }

  public Selection createRangeSelection(String fieldName) {
    String range = getIterator(fieldName, (List)metadata.getRangeFacetsWithRanges().get(fieldName)).next().toString();
    String[] split = null;
    if (range.contains(",")) {
      split = range.split(",");
    } else if (range.contains("-")) {
      split = range.split("-");
    } else {
      throw new IllegalStateException("Splitted range doesn't contain two values - " + range);
    }
    if (split.length != 2) {
      throw new IllegalStateException("Splitted range doesn't contain two values - " + range);
    }
    String start = split[0].trim();
    String end = split[1].trim();
    return Filters.range(fieldName, start, end);
  }

  public List<String> getSampleValues(String fieldName, int count) {
    CircularIterator<Object> iterator = getIterator(fieldName);
    List<String> ret = new ArrayList<String>(count);
    for(int i = 0; i < count; i++) {
      ret.add(iterator.next().toString());
    }
    return ret;
  }

  public CircularIterator<Object> getIterator(String fieldName) {
    return getIterator(fieldName, fieldValues.get(fieldName));
  }

  public CircularIterator<Object> getIterator(String fieldName, Collection<Object> values) {
    if (!iterators.containsKey(fieldName)) {
      if (values == null) {
        throw new IllegalStateException("Gathered data doesn't contain values for the field " + fieldName);
      }
      List<Object> list = new ArrayList<Object>(values);
      Collections.shuffle(list);
      iterators.put(fieldName, new CircularIterator<Object>(list));
    }
    return iterators.get(fieldName);
  }


  public static class CircularIterator<T> {
      private Iterator<T> iterator;
      private List<T> list;
      public CircularIterator(List<T> list) {
        this.list = list;
        if (list.isEmpty()) throw new IllegalArgumentException();
        iterator = list.iterator();
      }
      public T next() {
        if (!iterator.hasNext()) {
          iterator = list.iterator();
        }
        return iterator.next();
      }
    }
}
