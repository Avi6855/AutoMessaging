package com.avinashpatil.app.automessage.ui.viewmodel;

import com.avinashpatil.app.automessage.data.repository.DataStoreRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<DataStoreRepository> dataStoreRepositoryProvider;

  public SettingsViewModel_Factory(Provider<DataStoreRepository> dataStoreRepositoryProvider) {
    this.dataStoreRepositoryProvider = dataStoreRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(dataStoreRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<DataStoreRepository> dataStoreRepositoryProvider) {
    return new SettingsViewModel_Factory(dataStoreRepositoryProvider);
  }

  public static SettingsViewModel newInstance(DataStoreRepository dataStoreRepository) {
    return new SettingsViewModel(dataStoreRepository);
  }
}
