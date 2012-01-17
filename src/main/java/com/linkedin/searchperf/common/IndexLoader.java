package com.linkedin.searchperf.common;

import java.io.InputStream;
import java.nio.charset.Charset;

public interface IndexLoader {
  public static Charset UTF8 = Charset.forName("UTF-8");
  void loadData(String url, InputStream data);
}
