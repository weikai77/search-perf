package com.linkedin.searchperf.query.solr;

import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.json.JSONObject;

import com.linkedin.searchperf.query.sensei.SenseiQueryProducer;
import com.linkedin.searchperf.runner.impl.MockClient;
import com.senseidb.search.client.json.req.Operator;
import com.senseidb.search.client.json.req.Range;
import com.senseidb.search.client.json.req.Selection;
import com.senseidb.search.client.json.req.Selection.Path;
import com.senseidb.search.client.json.req.SenseiClientRequest;
import com.senseidb.search.client.json.req.Terms;

public class ElasticQueryProducer {
  
    private SenseiQueryProducer senseiQueryProducer;
   
    
    public ElasticQueryProducer(SenseiQueryProducer senseiQueryProducer) {
      this.senseiQueryProducer = senseiQueryProducer;
    }

    public String createQuery(boolean includeFacets, int simpleSelections, int rangeSelections, int pathSelections) {
      SenseiClientRequest clientRequest = senseiQueryProducer.createQuery(includeFacets, simpleSelections,
          rangeSelections, pathSelections);
      
        FilterBuilder[] filters = new FilterBuilder[clientRequest.getSelections().size()];
        for (int i = 0; i < filters.length; i++) {
          Selection selection = clientRequest.getSelections().get(i);
          if (selection instanceof Terms) {
            filters[i] = createFilterFromTerm((Terms) selection);
          }
          if (selection instanceof Range) {
            Range range = (Range) selection;
            RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter(range.getField());
            filters[i] = rangeFilter;
            if (!"*".equals(range.getFrom())) rangeFilter.from(range.getFrom());
            if (!"*".equals(range.getTo())) rangeFilter.to(range.getTo());
            
          }
          if (selection instanceof Path) {
            Path path = (Path) selection;
            filters[i] = FilterBuilders.prefixFilter(path.getField(), path.getValue());
          }
        }
        FilterBuilder selections = FilterBuilders.orFilter(filters);
        SearchRequestBuilder requestBuilder = new SearchRequestBuilder(new MockClient());
        requestBuilder.setFilter(selections);
        try {
          JSONObject request = new JSONObject(requestBuilder.toString());
          JSONObject facets = new JSONObject();
          for (Selection selection : clientRequest.getSelections()) {
            if (selection instanceof Terms) {
              String field = selection.getField();
              facets.put(field, new JSONObject("{\"terms\": {\"field\":\"" + field + "\", \"size\" : 300}}"));
            }
          }
          request.put("facets", facets);
          return request.toString();
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }

      }
    private FilterBuilder createFilterFromTerm(Terms selection) {
      FilterBuilder[] includes = new FilterBuilder[selection.getValues().size()];
      int i = 0;
      for (String value : selection.getValues()) {
        includes[i] = FilterBuilders.termFilter(selection.getField(), value);
        i++;
      }
      FilterBuilder[] excludes = new FilterBuilder[selection.getExcludes().size()];
      i = 0;
      for (String value : selection.getExcludes()) {
        excludes[i] = FilterBuilders.termFilter(selection.getField(), value);
        i++;
      }
      if (includes.length == 0) {
        return FilterBuilders.notFilter(FilterBuilders.orFilter(excludes));
      }
      FilterBuilder include = null;
      if (selection.getOperator() == Operator.or) {
        include = FilterBuilders.orFilter(includes);
      } else {
        include = FilterBuilders.andFilter(includes);
      }
      if (excludes.length == 0) {
        return include;
      }
      return FilterBuilders.andFilter(include, FilterBuilders.notFilter(FilterBuilders.orFilter(excludes)));
    }
    
}
