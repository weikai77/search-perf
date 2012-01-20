package com.linkedin.searchperf.query.solr;

import java.util.ArrayList;
import java.util.List;

import com.linkedin.searchperf.query.sensei.SenseiQueryProducer;
import com.senseidb.search.client.json.req.Range;
import com.senseidb.search.client.json.req.Selection;
import com.senseidb.search.client.json.req.Selection.Path;
import com.senseidb.search.client.json.req.SenseiClientRequest;
import com.senseidb.search.client.json.req.Terms;

public class SolrQueryProducer {
  private SenseiQueryProducer senseiQueryProducer;
  public static class SolrQuery {
    public String query;
    public List<String> facets;
    public SolrQuery(String query, List<String> facets) {      
      this.query = query;
      this.facets = facets;
    }  
  }
  
  public SolrQueryProducer(SenseiQueryProducer senseiQueryProducer) {
    this.senseiQueryProducer = senseiQueryProducer;
  }

  public SolrQuery createSolrQuery(boolean includeFacets, int simpleSelections, int rangeSelections, int pathSelections) {
    SenseiClientRequest clientRequest = senseiQueryProducer.createQuery(includeFacets, simpleSelections,
        rangeSelections, pathSelections);
    List<String> solrSelections = new ArrayList<String>();
    for (Selection selection : clientRequest.getSelections()) {
      if (selection instanceof Terms) {
        solrSelections.add(transformTermsSelection((Terms) selection));
      }
      if (selection instanceof Range) {
        Range range = (Range) selection;
        solrSelections.add(range.getField() + ":[" + range.getFrom() + " TO " + range.getTo() + "]");
      }
      if (selection instanceof Path) {
        Path path = (Path) selection;

        solrSelections.add(path.getField() + ":" + path.getValue() + "*");
      }
    }
   String query = groupIntoExpression("OR", solrSelections);
   return new SolrQuery(query, new ArrayList<String>(clientRequest.getFacets().keySet()));
  }

  private String transformTermsSelection(Terms selection) {
    String field = selection.getField();
    List<String> includes = new ArrayList<String>(selection.getValues().size());
    for (String include : selection.getValues()) {
      includes.add(term(field, include, false));
    }
    List<String> excludes = new ArrayList<String>(selection.getExcludes().size());
    for (String exclude : selection.getExcludes()) {
      excludes.add(term(field, exclude, true));
    }
    if (excludes.isEmpty())
      return groupIntoExpression(selection.getOperator().toString(), includes);
    if (includes.isEmpty())
      return groupIntoExpression("AND", excludes);
    return groupIntoExpression("AND", groupIntoExpression(selection.getOperator().toString(), includes),
        groupIntoExpression("AND", excludes));
  }

  private String term(String field, String value, boolean not) {
    String ret = field + ":" + value;
    if (not)
      ret = "!" + ret;
    return ret;
  }

  private String groupIntoExpression(String operator, String... expressions) {
    if (expressions.length == 0) {
      throw new IllegalArgumentException("Expressions shouldn't be empty");
    }
    if (expressions.length == 1) {
      return expressions[0];
    }
    StringBuilder ret = new StringBuilder("(");
    ret.append(expressions[0]);
    for (int i = 1; i < expressions.length; i++) {
      ret.append(" " + operator + " " + expressions[i]);
    }
    ret.append(")");
    return ret.toString();
  }

  private String groupIntoExpression(String operator, List<String> expressions) {
    return groupIntoExpression(operator, expressions.toArray(new String[expressions.size()]));
  }
}
