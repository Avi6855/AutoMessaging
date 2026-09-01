package com.avinashpatil.app.automessage.data.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class DataStoreRepositoryImpl_Factory implements Factory<DataStoreRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public DataStoreRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public DataStoreRepositoryImpl get() {
    return newInstance(dataStoreProvider.get());
  }

  public static DataStoreRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new DataStoreRepositoryImpl_Factory(dataStoreProvider);
  }

  public static DataStoreRepositoryImpl newInstance(DataStore<Preferences> dataStore) {
    return new DataStoreRepositoryImpl(dataStore);
  }
}
