package com.linkedin.searchperf.common;

import org.json.JSONObject;

public interface Searcher {
  void doSearch(JSONObject req) throws Exception;
  void shutdown() throws Exception;
}
