import com.recipemirror.recipes.RecipeConfig
import com.recipemirror.recipes.RecipeModule
import com.recipemirror.recipes.RecipeService
import com.recipemirror.search.SearchConfig
import com.recipemirror.search.SearchModule
import com.recipemirror.search.SearchService

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {
  serverConfig {
    yaml("application.yaml")
    require("/search", SearchConfig)
    require("/recipe", RecipeConfig)
  }
  bindings {
    module SearchModule
    module RecipeModule
  }
  handlers { SearchService searchService, RecipeService recipeService ->
    prefix("search") {
      get(":query/:page?") {
        def terms = pathTokens.query.tokenize(" ")
        def recipeIds = searchService.search(terms, pathTokens.page?.toInteger() ?: 1)
        render(json(recipeService.getRecipeSummaries(recipeIds as String[])))
      }
    }
  }
}