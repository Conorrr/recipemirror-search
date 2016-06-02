package com.recipemirror.search

import com.google.inject.Inject
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.store.IOContext
import org.apache.lucene.store.RAMDirectory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SearchService {

  @Inject
  private SearchConfig config

  private Logger log = LoggerFactory.getLogger(SearchService)

  private Directory index
  private IndexReader reader
  private IndexSearcher searcher

  public void loadDirectory() {
    log.info("loading index from <{}>", config.directoryLocation)
    index = new RAMDirectory(FSDirectory.open(new File(config.directoryLocation).toPath()), IOContext.READONCE)
    log.info("finished loading index")

    reader = DirectoryReader.open(index)
    searcher = new IndexSearcher(reader)
  }

  public List<String> search(List<String> terms, Integer page, String field = "all") {
    log.info("searching for <{}> in <{}> page {}", terms, field, page)
    TopDocs results = searcher.search(buildQuery(terms, field), config.maxResults)
    ScoreDoc[] hits = results.scoreDocs
    return paginate(hits.collect { searcher.doc(it.doc).id }, page)
  }

  private static Query buildQuery(List<String> terms, String field) {
    BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder()
    terms.each { booleanQuery.add(new TermQuery(new Term(field, it)), BooleanClause.Occur.MUST) }
    return booleanQuery.build()
  }

  private List<String> paginate(List<String> results, int page) {
    def from = (page - 1) * config.resultsPerPage
    def to = Math.min(page * config.resultsPerPage - 1, results.size() - 1)

    return results[from..to]
  }

}
