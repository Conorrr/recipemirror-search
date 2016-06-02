package com.recipemirror.search

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Scopes
import ratpack.guice.ConfigurableModule
import ratpack.service.Service
import ratpack.service.StartEvent

class SearchModule extends ConfigurableModule<SearchConfig> {


  @Override
  protected void configure() {
    bind(SearchService).in(Scopes.SINGLETON)
    bind(Startup)
  }

  private static class Startup implements Service {

    private final Injector injector

    @Inject
    public Startup(Injector injector) {
      this.injector = injector
    }

    @Override
    public void onStart(StartEvent event) {
      injector.getInstance(SearchService).loadDirectory()
    }
  }
}
