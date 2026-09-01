package com.avinashpatil.app.automessage.ui.screens.messages;

import com.avinashpatil.app.automessage.data.repository.MessageRepository;
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
public final class MessagesViewModel_Factory implements Factory<MessagesViewModel> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  public MessagesViewModel_Factory(Provider<MessageRepository> messageRepositoryProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
  }

  @Override
  public MessagesViewModel get() {
    return newInstance(messageRepositoryProvider.get());
  }

  public static MessagesViewModel_Factory create(
      Provider<MessageRepository> messageRepositoryProvider) {
    return new MessagesViewModel_Factory(messageRepositoryProvider);
  }

  public static MessagesViewModel newInstance(MessageRepository messageRepository) {
    return new MessagesViewModel(messageRepository);
  }
}
