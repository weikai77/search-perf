package com.linkedin.searchperf.common;

import com.linkedin.searchperf.es.ElasticSearchSearcher;
import com.linkedin.searchperf.sensei.SenseiSearcher;
import com.linkedin.searchperf.solr.SolrSearcher;

public class SearchRunner {

  static Searcher getSearcher(String type,String host,int port) throws Exception{
    if ("sensei".equals(type)){
      return new SenseiSearcher(host,port);
    }
    else if ("solr".equals(type)){
      return new SolrSearcher(host,port);
    }
    else if ("es".equals(type)){
      return new ElasticSearchSearcher(host, port);
    }
    else{
      throw new Exception("invalid type: "+type);
    }
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception{
    String type = args[0];
    String host = args[1];
    int port = Integer.parseInt(args[2]);
    
    int numIters = 100; // Integer.parse(args[3]);
    int numThreads = 20; // Integer.parse(args[4]);

    Searcher searcher = getSearcher(type,host,port);
    SearchThread[] threads = new SearchThread[numThreads];
    for (int i = 0;i<threads.length;++i){
      threads[i] = new SearchThread(searcher,numIters);
    }
    
    for (int i = 0;i<threads.length;++i){
      threads[i].start();
    }
    
    for (int i = 0;i<threads.length;++i){
      threads[i].join();
    }
    
    searcher.shutdown();
  }

  private static class SearchThread extends Thread{
    final Searcher _searcher;
    final int _numIters;
    
    SearchThread(Searcher searcher,int numIters){
      _searcher = searcher;
      _numIters = numIters;
    }
    
    
    public void run(){
      for (int i=0; i<_numIters; i++)
      {
        try{
          _searcher.doSearch(null);
        }
        catch(Exception e){
          e.printStackTrace();
        }
      }
    }
  }
}
