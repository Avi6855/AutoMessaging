package com.avinashpatil.app.automessage.ui.screens.groups;

import android.app.Application;
import com.avinashpatil.app.automessage.data.repository.ContactRepository;
import com.avinashpatil.app.automessage.data.repository.GroupRepository;
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
public final class GroupDetailsViewModel_Factory implements Factory<GroupDetailsViewModel> {
  private final Provider<Application> applicationProvider;

  private final Provider<GroupRepository> groupRepositoryProvider;

  private final Provider<ContactRepository> contactRepositoryProvider;

  private final Provider<MessageRepository> messageRepositoryProvider;

  public GroupDetailsViewModel_Factory(Provider<Application> applicationProvider,
      Provider<GroupRepository> groupRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider) {
    this.applicationProvider = applicationProvider;
    this.groupRepositoryProvider = groupRepositoryProvider;
    this.contactRepositoryProvider = contactRepositoryProvider;
    this.messageRepositoryProvider = messageRepositoryProvider;
  }

  @Override
  public GroupDetailsViewModel get() {
    return newInstance(applicationProvider.get(), groupRepositoryProvider.get(), contactRepositoryProvider.get(), messageRepositoryProvider.get());
  }

  public static GroupDetailsViewModel_Factory create(Provider<Application> applicationProvider,
      Provider<GroupRepository> groupRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider) {
    return new GroupDetailsViewModel_Factory(applicationProvider, groupRepositoryProvider, contactRepositoryProvider, messageRepositoryProvider);
  }

  public static GroupDetailsViewModel newInstance(Application application,
      GroupRepository groupRepository, ContactRepository contactRepository,
      MessageRepository messageRepository) {
    return new GroupDetailsViewModel(application, groupRepository, contactRepository, messageRepository);
  }
}
