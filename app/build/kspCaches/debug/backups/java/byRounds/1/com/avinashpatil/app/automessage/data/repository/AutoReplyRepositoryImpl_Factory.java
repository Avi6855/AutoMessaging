package com.avinashpatil.app.automessage.data.repository;

import com.avinashpatil.app.automessage.data.dao.AutoReplyLogDao;
import com.avinashpatil.app.automessage.data.dao.LastSeenCallDao;
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
public final class AutoReplyRepositoryImpl_Factory implements Factory<AutoReplyRepositoryImpl> {
  private final Provider<AutoReplyLogDao> autoReplyLogDaoProvider;

  private final Provider<LastSeenCallDao> lastSeenCallDaoProvider;

  public AutoReplyRepositoryImpl_Factory(Provider<AutoReplyLogDao> autoReplyLogDaoProvider,
      Provider<LastSeenCallDao> lastSeenCallDaoProvider) {
    this.autoReplyLogDaoProvider = autoReplyLogDaoProvider;
    this.lastSeenCallDaoProvider = lastSeenCallDaoProvider;
  }

  @Override
  public AutoReplyRepositoryImpl get() {
    return newInstance(autoReplyLogDaoProvider.get(), lastSeenCallDaoProvider.get());
  }

  public static AutoReplyRepositoryImpl_Factory create(
      Provider<AutoReplyLogDao> autoReplyLogDaoProvider,
      Provider<LastSeenCallDao> lastSeenCallDaoProvider) {
    return new AutoReplyRepositoryImpl_Factory(autoReplyLogDaoProvider, lastSeenCallDaoProvider);
  }

  public static AutoReplyRepositoryImpl newInstance(AutoReplyLogDao autoReplyLogDao,
      LastSeenCallDao lastSeenCallDao) {
    return new AutoReplyRepositoryImpl(autoReplyLogDao, lastSeenCallDao);
  }
}
