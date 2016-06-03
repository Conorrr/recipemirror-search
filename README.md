# RecipeMirror - search



___

### Only proceed if you are familar with web application development

**The following is a WIP and is likely to contain bugs**

## Requirements to run
* Groovy
* Gradle

## How to install
1. [Download recipe data](https://github.com/Conorrr/recipemirror-data)
1. Create index
   1. run `gradle createIndex` this will load the recipes from data and create a lucene index (you can specify the location of the recipe directory by adding `-PrecipePath=../data/recipes`)
1. Make any required changes to the property file
1. Run the application using `gradle run`

### Properties
Properties are stored in `src/ratpack/application.yaml`


### Endpoints
 GET `/search/{query}/{page}?`
 	Does a simple search and returns 

 GET `/recipe/{recipeId}`
 	Returns a full recipe


#### TODO:
* Add printlns to createIndex
* Add endpoint to return full recipes
* Add endpoints to return chef data
* Add better error handling
* Improve indexing (Ignore some tokens)

# Contact
If you have any questions feel free to contact me via email: recipemirror at restall.io
