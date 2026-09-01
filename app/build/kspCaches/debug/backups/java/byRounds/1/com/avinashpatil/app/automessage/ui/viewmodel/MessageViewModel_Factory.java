package com.avinashpatil.app.automessage.ui.viewmodel;

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
public final class MessageViewModel_Factory implements Factory<MessageViewModel> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  public MessageViewModel_Factory(Provider<MessageRepository> messageRepositoryProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
  }

  @Override
  public MessageViewModel get() {
    return newInstance(messageRepositoryProvider.get());
  }

  public static MessageViewModel_Factory create(
      Provider<MessageRepository> messageRepositoryProvider) {
    return new MessageViewModel_Factory(messageRepositoryProvider);
  }

  public static MessageViewModel newInstance(MessageRepository messageRepository) {
    return new MessageViewModel(messageRepository);
  }
}
