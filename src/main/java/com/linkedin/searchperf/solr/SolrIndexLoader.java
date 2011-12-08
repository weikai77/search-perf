package com.linkedin.searchperf.solr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.linkedin.searchperf.common.IndexLoader;

public class SolrIndexLoader implements IndexLoader {

  private final File _dataFile;
  private final String _solrUrl;
  SolrIndexLoader(File dataFile,String solrHost,int solrPort){
    _dataFile = dataFile;
    _solrUrl = "http://"+solrHost+":"+solrPort+"/solr/";
  }
  
  @Override
  public void loadData() throws Exception {
    CommonsHttpSolrServer solr = new CommonsHttpSolrServer(_solrUrl);
    Iterator<SolrInputDocument> iter = new Iterator<SolrInputDocument>()
    {
      private final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_dataFile),UTF8));
      private boolean done;
      private String next;
      
      @Override
      public boolean hasNext()
      {
        if (done)
          return false;
        if (next != null)
          return true;

        try
        {
          next = br.readLine();
          if (next == null)
          {
            done = true;
            br.close();
            return false;
          }
          else
            return true;
        }
        catch (Exception ex)
        {
          throw new RuntimeException(ex);
        }
      }

      @Override
      public SolrInputDocument next()
      {
        if (!hasNext())
          throw new NoSuchElementException();
        
        SolrInputDocument doc = new SolrInputDocument();
        JSONObject json = (JSONObject) JSONValue.parse(next);
        for (Entry<String,Object> entry : (Set<Entry<String,Object>>) json.entrySet())
        {
          if (entry.getKey().equals("job_functions"))
          {
            doc.addField("job_function", Arrays.asList( ((String)entry.getValue()).split(",")) );
          }
          else
          {
            doc.addField(entry.getKey(), entry.getValue());
          }
        }
        next = null;
        return doc;
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };

    solr.add(iter, 10000);
    solr.commit();
  }

}
