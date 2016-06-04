import com.recipemirror.recipes.RecipeConfig
import com.recipemirror.recipes.RecipeModule
import com.recipemirror.recipes.RecipeService
import com.recipemirror.search.SearchConfig
import com.recipemirror.search.SearchModule
import com.recipemirror.search.SearchService

import java.nio.file.Paths

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
  handlers { RecipeService recipeService ->
    get("search") { SearchService searchService ->
      def page = request.queryParams.page?.toInteger() ?: 1
      def terms = request.queryParams.query?.tokenize(" ") ?: [""]
      def searchResult = searchService.search(terms, page)
      response.contentType("application/json;charset=utf-8")
      render(json(searchResult))
    }
    prefix("recipe") {
      get(":id") {
        response.contentType("application/json;charset=utf-8")
        render(json(recipeService.getRecipes([pathTokens.id])[0]))
      }
      get(':id/image') { RecipeConfig recipeConfig ->
        render(Paths.get("${recipeConfig.recipeImageDirectory}/${pathTokens.id}.jpg"))
      }
    }

    prefix("author") {
      get(":id") {
        response.contentType("application/json;charset=utf-8")
        render(json(recipeService.getAuthors([pathTokens.id])[0]))
      }
      get(':id/image') { RecipeConfig recipeConfig ->
        render(Paths.get("${recipeConfig.authorImageDirectory}/${pathTokens.id}.jpg"))
      }
    }
  }
}