package com.avinashpatil.app.automessage.ui.viewmodel;

import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository;
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository;
import com.avinashpatil.app.automessage.service.AutoMessagingManager;
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

  private final Provider<AutoMessagingManager> autoMessagingManagerProvider;

  public AutoReplyViewModel_Factory(Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<DataStoreRepository> dataStoreRepositoryProvider,
      Provider<AutoMessagingManager> autoMessagingManagerProvider) {
    this.autoReplyRepositoryProvider = autoReplyRepositoryProvider;
    this.dataStoreRepositoryProvider = dataStoreRepositoryProvider;
    this.autoMessagingManagerProvider = autoMessagingManagerProvider;
  }

  @Override
  public AutoReplyViewModel get() {
    return newInstance(autoReplyRepositoryProvider.get(), dataStoreRepositoryProvider.get(), autoMessagingManagerProvider.get());
  }

  public static AutoReplyViewModel_Factory create(
      Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<DataStoreRepository> dataStoreRepositoryProvider,
      Provider<AutoMessagingManager> autoMessagingManagerProvider) {
    return new AutoReplyViewModel_Factory(autoReplyRepositoryProvider, dataStoreRepositoryProvider, autoMessagingManagerProvider);
  }

  public static AutoReplyViewModel newInstance(AutoReplyRepository autoReplyRepository,
      DataStoreRepository dataStoreRepository, AutoMessagingManager autoMessagingManager) {
    return new AutoReplyViewModel(autoReplyRepository, dataStoreRepository, autoMessagingManager);
  }
}
