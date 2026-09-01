package com.avinashpatil.app.automessage.service;

import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository;
import com.avinashpatil.app.automessage.data.repository.ContactRepository;
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository;
import com.avinashpatil.app.automessage.data.repository.GroupRepository;
import com.avinashpatil.app.automessage.data.repository.MessageRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class CallDetectionService_MembersInjector implements MembersInjector<CallDetectionService> {
  private final Provider<DataStoreRepository> dataStoreRepositoryProvider;

  private final Provider<ContactRepository> contactRepositoryProvider;

  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<AutoReplyRepository> autoReplyRepositoryProvider;

  private final Provider<GroupRepository> groupRepositoryProvider;

  private final Provider<AutoMessagingManager> autoMessagingManagerProvider;

  public CallDetectionService_MembersInjector(
      Provider<DataStoreRepository> dataStoreRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<GroupRepository> groupRepositoryProvider,
      Provider<AutoMessagingManager> autoMessagingManagerProvider) {
    this.dataStoreRepositoryProvider = dataStoreRepositoryProvider;
    this.contactRepositoryProvider = contactRepositoryProvider;
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.autoReplyRepositoryProvider = autoReplyRepositoryProvider;
    this.groupRepositoryProvider = groupRepositoryProvider;
    this.autoMessagingManagerProvider = autoMessagingManagerProvider;
  }

  public static MembersInjector<CallDetectionService> create(
      Provider<DataStoreRepository> dataStoreRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<GroupRepository> groupRepositoryProvider,
      Provider<AutoMessagingManager> autoMessagingManagerProvider) {
    return new CallDetectionService_MembersInjector(dataStoreRepositoryProvider, contactRepositoryProvider, messageRepositoryProvider, autoReplyRepositoryProvider, groupRepositoryProvider, autoMessagingManagerProvider);
  }

  @Override
  public void injectMembers(CallDetectionService instance) {
    injectDataStoreRepository(instance, dataStoreRepositoryProvider.get());
    injectContactRepository(instance, contactRepositoryProvider.get());
    injectMessageRepository(instance, messageRepositoryProvider.get());
    injectAutoReplyRepository(instance, autoReplyRepositoryProvider.get());
    injectGroupRepository(instance, groupRepositoryProvider.get());
    injectAutoMessagingManager(instance, autoMessagingManagerProvider.get());
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.service.CallDetectionService.dataStoreRepository")
  public static void injectDataStoreRepository(CallDetectionService instance,
      DataStoreRepository dataStoreRepository) {
    instance.dataStoreRepository = dataStoreRepository;
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.service.CallDetectionService.contactRepository")
  public static void injectContactRepository(CallDetectionService instance,
      ContactRepository contactRepository) {
    instance.contactRepository = contactRepository;
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.service.CallDetectionService.messageRepository")
  public static void injectMessageRepository(CallDetectionService instance,
      MessageRepository messageRepository) {
    instance.messageRepository = messageRepository;
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.service.CallDetectionService.autoReplyRepository")
  public static void injectAutoReplyRepository(CallDetectionService instance,
      AutoReplyRepository autoReplyRepository) {
    instance.autoReplyRepository = autoReplyRepository;
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.service.CallDetectionService.groupRepository")
  public static void injectGroupRepository(CallDetectionService instance,
      GroupRepository groupRepository) {
    instance.groupRepository = groupRepository;
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.service.CallDetectionService.autoMessagingManager")
  public static void injectAutoMessagingManager(CallDetectionService instance,
      AutoMessagingManager autoMessagingManager) {
    instance.autoMessagingManager = autoMessagingManager;
  }
}
