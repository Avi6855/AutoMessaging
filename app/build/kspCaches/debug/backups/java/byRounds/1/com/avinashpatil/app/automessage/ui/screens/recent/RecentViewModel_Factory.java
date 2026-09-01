package com.avinashpatil.app.automessage.ui.screens.recent;

import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository;
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
public final class RecentViewModel_Factory implements Factory<RecentViewModel> {
  private final Provider<AutoReplyRepository> autoReplyRepositoryProvider;

  public RecentViewModel_Factory(Provider<AutoReplyRepository> autoReplyRepositoryProvider) {
    this.autoReplyRepositoryProvider = autoReplyRepositoryProvider;
  }

  @Override
  public RecentViewModel get() {
    return newInstance(autoReplyRepositoryProvider.get());
  }

  public static RecentViewModel_Factory create(
      Provider<AutoReplyRepository> autoReplyRepositoryProvider) {
    return new RecentViewModel_Factory(autoReplyRepositoryProvider);
  }

  public static RecentViewModel newInstance(AutoReplyRepository autoReplyRepository) {
    return new RecentViewModel(autoReplyRepository);
  }
}
