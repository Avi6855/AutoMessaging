package com.avinashpatil.app.automessage.di;

import com.avinashpatil.app.automessage.data.database.AutoMessageDatabase;
import com.avinashpatil.app.automessage.data.repository.ContactRepository;
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
public final class AppModule_ProvideContactRepositoryFactory implements Factory<ContactRepository> {
  private final Provider<AutoMessageDatabase> databaseProvider;

  public AppModule_ProvideContactRepositoryFactory(Provider<AutoMessageDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ContactRepository get() {
    return provideContactRepository(databaseProvider.get());
  }

  public static AppModule_ProvideContactRepositoryFactory create(
      Provider<AutoMessageDatabase> databaseProvider) {
    return new AppModule_ProvideContactRepositoryFactory(databaseProvider);
  }

  public static ContactRepository provideContactRepository(AutoMessageDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideContactRepository(database));
  }
}
