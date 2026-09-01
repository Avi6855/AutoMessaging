package com.avinashpatil.app.automessage.di;

import android.app.Application;
import com.avinashpatil.app.automessage.data.database.AutoMessageDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideAutoMessageDatabaseFactory implements Factory<AutoMessageDatabase> {
  private final Provider<Application> appProvider;

  public AppModule_ProvideAutoMessageDatabaseFactory(Provider<Application> appProvider) {
    this.appProvider = appProvider;
  }

  @Override
  public AutoMessageDatabase get() {
    return provideAutoMessageDatabase(appProvider.get());
  }

  public static AppModule_ProvideAutoMessageDatabaseFactory create(
      Provider<Application> appProvider) {
    return new AppModule_ProvideAutoMessageDatabaseFactory(appProvider);
  }

  public static AutoMessageDatabase provideAutoMessageDatabase(Application app) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAutoMessageDatabase(app));
  }
}
