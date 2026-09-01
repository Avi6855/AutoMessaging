package com.avinashpatil.app.automessage.ui.screens.recent;

import androidx.lifecycle.SavedStateHandle;
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
public final class MessageDetailViewModel_Factory implements Factory<MessageDetailViewModel> {
  private final Provider<AutoReplyRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public MessageDetailViewModel_Factory(Provider<AutoReplyRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public MessageDetailViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static MessageDetailViewModel_Factory create(
      Provider<AutoReplyRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new MessageDetailViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static MessageDetailViewModel newInstance(AutoReplyRepository repository,
      SavedStateHandle savedStateHandle) {
    return new MessageDetailViewModel(repository, savedStateHandle);
  }
}
