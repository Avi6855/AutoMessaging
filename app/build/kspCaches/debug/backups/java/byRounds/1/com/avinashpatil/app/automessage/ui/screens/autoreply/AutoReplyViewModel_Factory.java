package com.avinashpatil.app.automessage.ui.screens.autoreply;

import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository;
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
public final class AutoReplyViewModel_Factory implements Factory<AutoReplyViewModel> {
  private final Provider<AutoReplyRepository> autoReplyRepositoryProvider;

  private final Provider<DataStoreRepository> dataStoreRepositoryProvider;

  public AutoReplyViewModel_Factory(Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<DataStoreRepository> dataStoreRepositoryProvider) {
    this.autoReplyRepositoryProvider = autoReplyRepositoryProvider;
    this.dataStoreRepositoryProvider = dataStoreRepositoryProvider;
  }

  @Override
  public AutoReplyViewModel get() {
    return newInstance(autoReplyRepositoryProvider.get(), dataStoreRepositoryProvider.get());
  }

  public static AutoReplyViewModel_Factory create(
      Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<DataStoreRepository> dataStoreRepositoryProvider) {
    return new AutoReplyViewModel_Factory(autoReplyRepositoryProvider, dataStoreRepositoryProvider);
  }

  public static AutoReplyViewModel newInstance(AutoReplyRepository autoReplyRepository,
      DataStoreRepository dataStoreRepository) {
    return new AutoReplyViewModel(autoReplyRepository, dataStoreRepository);
  }
}
