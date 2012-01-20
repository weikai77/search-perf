package com.linkedin.searchperf.runner.impl;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.mlt.MoreLikeThisRequest;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.action.count.CountRequestBuilder;
import org.elasticsearch.client.action.delete.DeleteRequestBuilder;
import org.elasticsearch.client.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.client.action.get.GetRequestBuilder;
import org.elasticsearch.client.action.get.MultiGetRequestBuilder;
import org.elasticsearch.client.action.index.IndexRequestBuilder;
import org.elasticsearch.client.action.mlt.MoreLikeThisRequestBuilder;
import org.elasticsearch.client.action.percolate.PercolateRequestBuilder;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.client.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.internal.InternalClient;
import org.elasticsearch.threadpool.ThreadPool;

public class MockClient implements InternalClient {

  @Override
  public AdminClient admin() {

    return null;
  }

  @Override
  public ActionFuture<BulkResponse> bulk(BulkRequest arg0) {

    return null;
  }

  @Override
  public void bulk(BulkRequest arg0, ActionListener<BulkResponse> arg1) {

  }

  @Override
  public void close() {

  }

  @Override
  public ActionFuture<CountResponse> count(CountRequest arg0) {

    return null;
  }

  @Override
  public void count(CountRequest arg0, ActionListener<CountResponse> arg1) {

  }

  @Override
  public ActionFuture<DeleteResponse> delete(DeleteRequest arg0) {

    return null;
  }

  @Override
  public void delete(DeleteRequest arg0, ActionListener<DeleteResponse> arg1) {

  }

  @Override
  public ActionFuture<DeleteByQueryResponse> deleteByQuery(DeleteByQueryRequest arg0) {

    return null;
  }

  @Override
  public void deleteByQuery(DeleteByQueryRequest arg0, ActionListener<DeleteByQueryResponse> arg1) {

  }

  @Override
  public ActionFuture<GetResponse> get(GetRequest arg0) {

    return null;
  }

  @Override
  public void get(GetRequest arg0, ActionListener<GetResponse> arg1) {

  }

  @Override
  public ActionFuture<IndexResponse> index(IndexRequest arg0) {

    return null;
  }

  @Override
  public void index(IndexRequest arg0, ActionListener<IndexResponse> arg1) {

  }

  @Override
  public ActionFuture<SearchResponse> moreLikeThis(MoreLikeThisRequest arg0) {

    return null;
  }

  @Override
  public void moreLikeThis(MoreLikeThisRequest arg0, ActionListener<SearchResponse> arg1) {

  }

  @Override
  public ActionFuture<MultiGetResponse> multiGet(MultiGetRequest arg0) {

    return null;
  }

  @Override
  public void multiGet(MultiGetRequest arg0, ActionListener<MultiGetResponse> arg1) {

  }

  @Override
  public ActionFuture<PercolateResponse> percolate(PercolateRequest arg0) {

    return null;
  }

  @Override
  public void percolate(PercolateRequest arg0, ActionListener<PercolateResponse> arg1) {

  }

  @Override
  public BulkRequestBuilder prepareBulk() {

    return null;
  }

  @Override
  public CountRequestBuilder prepareCount(String... arg0) {

    return null;
  }

  @Override
  public DeleteRequestBuilder prepareDelete() {

    return null;
  }

  @Override
  public DeleteRequestBuilder prepareDelete(String arg0, String arg1, String arg2) {

    return null;
  }

  @Override
  public DeleteByQueryRequestBuilder prepareDeleteByQuery(String... arg0) {

    return null;
  }

  @Override
  public GetRequestBuilder prepareGet() {

    return null;
  }

  @Override
  public GetRequestBuilder prepareGet(String arg0, String arg1, String arg2) {

    return null;
  }

  @Override
  public IndexRequestBuilder prepareIndex() {

    return null;
  }

  @Override
  public IndexRequestBuilder prepareIndex(String arg0, String arg1) {

    return null;
  }

  @Override
  public IndexRequestBuilder prepareIndex(String arg0, String arg1, String arg2) {

    return null;
  }

  @Override
  public MoreLikeThisRequestBuilder prepareMoreLikeThis(String arg0, String arg1, String arg2) {

    return null;
  }

  @Override
  public MultiGetRequestBuilder prepareMultiGet() {

    return null;
  }

  @Override
  public PercolateRequestBuilder preparePercolate(String arg0, String arg1) {

    return null;
  }

  @Override
  public SearchRequestBuilder prepareSearch(String... arg0) {

    return null;
  }

  @Override
  public SearchScrollRequestBuilder prepareSearchScroll(String arg0) {

    return null;
  }

  @Override
  public ActionFuture<SearchResponse> search(SearchRequest arg0) {

    return null;
  }

  @Override
  public void search(SearchRequest arg0, ActionListener<SearchResponse> arg1) {

  }

  @Override
  public ActionFuture<SearchResponse> searchScroll(SearchScrollRequest arg0) {

    return null;
  }

  @Override
  public void searchScroll(SearchScrollRequest arg0, ActionListener<SearchResponse> arg1) {

  }

  @Override
  public ThreadPool threadPool() {
    // TODO Auto-generated method stub
    return null;
  }

}
