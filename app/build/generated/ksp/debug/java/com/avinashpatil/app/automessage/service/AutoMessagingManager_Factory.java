package com.avinashpatil.app.automessage.service;

import android.content.Context;
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AutoMessagingManager_Factory implements Factory<AutoMessagingManager> {
  private final Provider<Context> contextProvider;

  private final Provider<DataStoreRepository> dataStoreRepositoryProvider;

  public AutoMessagingManager_Factory(Provider<Context> contextProvider,
      Provider<DataStoreRepository> dataStoreRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.dataStoreRepositoryProvider = dataStoreRepositoryProvider;
  }

  @Override
  public AutoMessagingManager get() {
    return newInstance(contextProvider.get(), dataStoreRepositoryProvider.get());
  }

  public static AutoMessagingManager_Factory create(Provider<Context> contextProvider,
      Provider<DataStoreRepository> dataStoreRepositoryProvider) {
    return new AutoMessagingManager_Factory(contextProvider, dataStoreRepositoryProvider);
  }

  public static AutoMessagingManager newInstance(Context context,
      DataStoreRepository dataStoreRepository) {
    return new AutoMessagingManager(context, dataStoreRepository);
  }
}
