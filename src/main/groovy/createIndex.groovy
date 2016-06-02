import groovy.json.JsonSlurper
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.FSDirectory

def load(File file) {
  new JsonSlurper().parseText(file.text)
}

def recipes = [:]
new File(args[0]).eachFile() { file ->
  if (file.name.contains(".json")) {
    recipes[file.name - '.json'] = load(file)
  }
}

def concatenateIngredients(ingredientList) {
  def sb = new StringBuilder();
  ingredientList.each({ groupName, ingredients ->
    ingredients.each {
      sb.append(it)
      sb.append(" ")
    }
  })
  return sb.toString()
}

def concatenateSteps(steps) {
  def sb = new StringBuilder();
  steps.each({ step ->
    sb.append(step)
    sb.append(" ")
  })
  return sb.toString()
}

def concatenateAll(recipe) {
  def sb = new StringBuilder()
  sb.append(recipe.title)
  sb.append(" ")
  sb.append(recipe.description)
  sb.append(" ")
  sb.append(concatenateIngredients(recipe.ingredients))
  sb.append(" ")
  sb.append(concatenateSteps(recipe.steps))
  return sb.toString()
}

StandardAnalyzer analyzer = new StandardAnalyzer()
IndexWriterConfig config = new IndexWriterConfig(analyzer)

def docs = recipes.collect { id, recipe ->
  Document doc = new Document()
  doc.add(new StringField("id", (String) id, Field.Store.YES))
  doc.add(new TextField("title", recipe.title, Field.Store.NO))
  doc.add(new StringField("vegetarian", (String) recipe.flags[0] ?: "", Field.Store.NO))
  doc.add(new StringField("author", (String) recipe.author.keySet()[0] ?: "", Field.Store.NO))
  doc.add(new StringField("show", (String) recipe.show.keySet()[0] ?: "", Field.Store.NO))
  doc.add(new StringField("cusine", (String) recipe.cusine ?: "", Field.Store.NO))
  doc.add(new TextField("dishes", recipe.dishes?.join(" ") ?: "", Field.Store.NO))

  doc.add(new TextField("all", concatenateAll(recipe), Field.Store.NO))
  return doc
}

def index = FSDirectory.open(new File("index").toPath())

IndexWriter w = new IndexWriter(index, config)
w.addDocuments(docs)
w.close()

//TODO Add tokenizer that removes food related words(e.g. fry, bake, roll)
