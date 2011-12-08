package com.linkedin.searchperf.common;

import java.nio.charset.Charset;

public interface IndexLoader {
  public static Charset UTF8 = Charset.forName("UTF-8");
  void loadData() throws Exception;
}
