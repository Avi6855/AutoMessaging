package com.avinashpatil.app.automessage.di;

import com.avinashpatil.app.automessage.data.database.AutoMessageDatabase;
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository;
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
public final class AppModule_ProvideAutoReplyRepositoryFactory implements Factory<AutoReplyRepository> {
  private final Provider<AutoMessageDatabase> databaseProvider;

  public AppModule_ProvideAutoReplyRepositoryFactory(
      Provider<AutoMessageDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AutoReplyRepository get() {
    return provideAutoReplyRepository(databaseProvider.get());
  }

  public static AppModule_ProvideAutoReplyRepositoryFactory create(
      Provider<AutoMessageDatabase> databaseProvider) {
    return new AppModule_ProvideAutoReplyRepositoryFactory(databaseProvider);
  }

  public static AutoReplyRepository provideAutoReplyRepository(AutoMessageDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAutoReplyRepository(database));
  }
}
