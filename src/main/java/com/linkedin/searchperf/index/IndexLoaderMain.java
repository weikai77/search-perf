package com.linkedin.searchperf.index;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import com.linkedin.searchperf.common.util.HttpClient;
import com.linkedin.searchperf.runner.AbstractConcurrentRunner;

public class IndexLoaderMain {
  private static HttpClient httpClient = new HttpClient();
  private static long sentCount;
  private static long readCount;

  public static void loadData(AbstractConcurrentRunner indexLoader, int numberOfThreads) {
  
    try {
      indexLoader.init(numberOfThreads, Long.MAX_VALUE / 2);
      indexLoader.run();
      sentCount = indexLoader.sentCount.get();
      readCount = indexLoader.readCount.get();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      if (indexLoader != null) {
        indexLoader.getExecutors().shutdown();
        try {
          indexLoader.getExecutors().awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
   org.apache.log4j.PropertyConfigurator.configure("configs/log4j.properties");
   if (args.length < 4) {
     System.out.println(help());
     System.exit(1);
   }
   String type = args[0];
   String url = args[1];
   String filePath = args[2];
   int numOfThreads = Integer.parseInt(args[3]);
   AbstractConcurrentRunner indexLoader = null;
   InputStream input = null;   
   try {
   input = new BufferedInputStream(new FileInputStream(new File(filePath)));
   LineIterator lineIterator = IOUtils.lineIterator(input, "UTF8");
   if (type.equalsIgnoreCase("solr")) {
     indexLoader = new SolrIndexLoader(lineIterator, url, httpClient);
   } else if (type.toLowerCase().contains("elastic")) {
     indexLoader = new ElasticIndexLoader(lineIterator, url, httpClient);
   } else {
     throw new UnsupportedOperationException("Only solr and elastic are currently supported");
   }
   long time = System.currentTimeMillis();
   loadData(indexLoader, numOfThreads);
   System.out.println("Elapsed time : " + (System.currentTimeMillis() - time) + ", sentCount = " + sentCount + ", readCount =" + readCount);
     } finally {
       IOUtils.closeQuietly(input);
     }
   }

  public static String help() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Usage: [solr, elastic] url filePath  numOfTHreads\n");
    buffer.append("====================================\n");
    buffer.append("Examples:\nsolr http://localhost:8983/solr/update/json data/cars.json 5\n");
    buffer.append("elastic http://localhost:9200/cars/car data/cars.json 4\n");
    // buffer.append("sensei http://localhost:8080/sensei data/cars.json 3\n");
    buffer.append("Please specify these 4 params!\n");
    buffer.append("====================================\n");
    return buffer.toString();
  }

  public static void setHttpClient(HttpClient httpClient) {
    IndexLoaderMain.httpClient = httpClient;
  }
  
}
