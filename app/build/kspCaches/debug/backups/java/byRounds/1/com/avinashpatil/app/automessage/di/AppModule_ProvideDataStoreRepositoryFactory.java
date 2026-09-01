package com.avinashpatil.app.automessage.di;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository;
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
public final class AppModule_ProvideDataStoreRepositoryFactory implements Factory<DataStoreRepository> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public AppModule_ProvideDataStoreRepositoryFactory(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public DataStoreRepository get() {
    return provideDataStoreRepository(dataStoreProvider.get());
  }

  public static AppModule_ProvideDataStoreRepositoryFactory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new AppModule_ProvideDataStoreRepositoryFactory(dataStoreProvider);
  }

  public static DataStoreRepository provideDataStoreRepository(DataStore<Preferences> dataStore) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDataStoreRepository(dataStore));
  }
}
