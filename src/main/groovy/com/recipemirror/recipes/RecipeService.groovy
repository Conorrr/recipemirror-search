package com.recipemirror.recipes

import com.google.inject.Inject
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RecipeService {

  @Inject
  private RecipeConfig config

  private Logger log = LoggerFactory.getLogger(RecipeService)

  private def recipes = [:]
  private def authors = [:]

  public void loadRecipes() {
    log.info("loading recipes from <{}>", config.recipeDirectory)
    new File((String) config.recipeDirectory).eachFile() { file ->
      if (file.name.contains(".json")) {
        def recipeId = file.name - '.json'
        recipes[recipeId] = [id: recipeId] + load(file)
      }
    }
    log.info("loaded {} recipes", recipes.size())
  }

  public void loadAuthors() {
    log.info("loading authors from <{}>", config.recipeDirectory)
    new File((String) config.authorDirectory).eachFile() { file ->
      if (file.name.contains(".json")) {
        def authorId = file.name - '.json'
        authors[authorId] = [id: authorId] + load(file)
      }
    }
    log.info("loaded {} authors", authors.size())
  }

  public getRecipes(List<String> ids) {
    ids.collect({ recipes.get(it) })
  }

  public getRecipeSummaries(List<String> ids) {
    def fullRecipes = getRecipes(ids)

    fullRecipes.collect({
      [id             : it.id,
       title          : it.title,
       description    : it.description,
       recommendations: it.recommendations,
       author         : it.author,
       cookTime       : it.cookTime,
       prepTime       : it.prepTime,
       serves         : it.serves,
      ]
    })
  }

  public getAuthors(String ids) {
    ids.collect({ authors.get(it) })
  }

  private static load(File file) {
    new JsonSlurper().parseText(file.text)
  }

}
