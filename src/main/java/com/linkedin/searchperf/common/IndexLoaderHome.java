package com.linkedin.searchperf.common;

import java.io.File;

public interface IndexLoaderHome {
  IndexLoader getIndexLoader(File dataFile,String host,int port);
}
