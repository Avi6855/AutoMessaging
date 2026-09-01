package com.avinashpatil.app.automessage.di;

import com.avinashpatil.app.automessage.data.database.AutoMessageDatabase;
import com.avinashpatil.app.automessage.data.repository.GroupRepository;
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
public final class AppModule_ProvideGroupRepositoryFactory implements Factory<GroupRepository> {
  private final Provider<AutoMessageDatabase> databaseProvider;

  public AppModule_ProvideGroupRepositoryFactory(Provider<AutoMessageDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public GroupRepository get() {
    return provideGroupRepository(databaseProvider.get());
  }

  public static AppModule_ProvideGroupRepositoryFactory create(
      Provider<AutoMessageDatabase> databaseProvider) {
    return new AppModule_ProvideGroupRepositoryFactory(databaseProvider);
  }

  public static GroupRepository provideGroupRepository(AutoMessageDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideGroupRepository(database));
  }
}
