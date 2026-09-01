package com.avinashpatil.app.automessage.data.repository;

import com.avinashpatil.app.automessage.data.dao.CustomMessageDao;
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
public final class MessageRepositoryImpl_Factory implements Factory<MessageRepositoryImpl> {
  private final Provider<CustomMessageDao> customMessageDaoProvider;

  public MessageRepositoryImpl_Factory(Provider<CustomMessageDao> customMessageDaoProvider) {
    this.customMessageDaoProvider = customMessageDaoProvider;
  }

  @Override
  public MessageRepositoryImpl get() {
    return newInstance(customMessageDaoProvider.get());
  }

  public static MessageRepositoryImpl_Factory create(
      Provider<CustomMessageDao> customMessageDaoProvider) {
    return new MessageRepositoryImpl_Factory(customMessageDaoProvider);
  }

  public static MessageRepositoryImpl newInstance(CustomMessageDao customMessageDao) {
    return new MessageRepositoryImpl(customMessageDao);
  }
}
