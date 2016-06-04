package com.recipemirror.search

import com.google.inject.Inject
import com.recipemirror.recipes.RecipeService
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

import static java.lang.Math.min

class SearchService {

  @Inject
  private SearchConfig config

  @Inject
  private RecipeService recipeService

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

  public search(List<String> terms, Integer page, String field = "all") {
    log.info("searching for <{}> in <{}> page {}", terms, field, page)
    TopDocs results = searcher.search(buildQuery(terms, field), config.maxResults)
    ScoreDoc[] hits = results.scoreDocs
    def paginatedResults = paginate(hits.collect { searcher.doc(it.doc).id }, page)
    def recipes = recipeService.getRecipeSummaries(paginatedResults)
    return [
        page        : page,
        totalResults: results.totalHits,
        results     : recipes,
    ]
  }

  private static Query buildQuery(List<String> terms, String field) {
    BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder()
    terms.each { booleanQuery.add(new TermQuery(new Term(field, it)), BooleanClause.Occur.MUST) }
    return booleanQuery.build()
  }

  private List<String> paginate(List<String> results, int page) {
    if (results.isEmpty()) {
      return []
    }
    def from = (page - 1) * config.resultsPerPage
    def to = min(page * config.resultsPerPage - 1, results.size() - 1)

    return results[from..to]
  }

}
