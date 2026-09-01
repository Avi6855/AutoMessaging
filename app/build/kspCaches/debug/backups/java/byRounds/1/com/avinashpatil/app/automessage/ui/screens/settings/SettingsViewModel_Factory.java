package com.avinashpatil.app.automessage.ui.screens.settings;

import android.content.Context;
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
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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

  private final Provider<AutoReplyRepository> autoReplyRepositoryProvider;

  private final Provider<AutoMessagingManager> autoMessagingManagerProvider;

  private final Provider<Context> contextProvider;

  public SettingsViewModel_Factory(Provider<DataStoreRepository> dataStoreRepositoryProvider,
      Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<AutoMessagingManager> autoMessagingManagerProvider,
      Provider<Context> contextProvider) {
    this.dataStoreRepositoryProvider = dataStoreRepositoryProvider;
    this.autoReplyRepositoryProvider = autoReplyRepositoryProvider;
    this.autoMessagingManagerProvider = autoMessagingManagerProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(dataStoreRepositoryProvider.get(), autoReplyRepositoryProvider.get(), autoMessagingManagerProvider.get(), contextProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<DataStoreRepository> dataStoreRepositoryProvider,
      Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<AutoMessagingManager> autoMessagingManagerProvider,
      Provider<Context> contextProvider) {
    return new SettingsViewModel_Factory(dataStoreRepositoryProvider, autoReplyRepositoryProvider, autoMessagingManagerProvider, contextProvider);
  }

  public static SettingsViewModel newInstance(DataStoreRepository dataStoreRepository,
      AutoReplyRepository autoReplyRepository, AutoMessagingManager autoMessagingManager,
      Context context) {
    return new SettingsViewModel(dataStoreRepository, autoReplyRepository, autoMessagingManager, context);
  }
}
