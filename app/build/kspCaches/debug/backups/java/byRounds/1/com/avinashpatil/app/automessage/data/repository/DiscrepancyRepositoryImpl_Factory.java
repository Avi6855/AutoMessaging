package com.avinashpatil.app.automessage.data.repository;

import com.avinashpatil.app.automessage.data.dao.DiscrepancyLogDao;
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
public final class DiscrepancyRepositoryImpl_Factory implements Factory<DiscrepancyRepositoryImpl> {
  private final Provider<DiscrepancyLogDao> daoProvider;

  public DiscrepancyRepositoryImpl_Factory(Provider<DiscrepancyLogDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public DiscrepancyRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static DiscrepancyRepositoryImpl_Factory create(Provider<DiscrepancyLogDao> daoProvider) {
    return new DiscrepancyRepositoryImpl_Factory(daoProvider);
  }

  public static DiscrepancyRepositoryImpl newInstance(DiscrepancyLogDao dao) {
    return new DiscrepancyRepositoryImpl(dao);
  }
}
