package com.avinashpatil.app.automessage.service;

import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository;
import com.avinashpatil.app.automessage.data.repository.DiscrepancyRepository;
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
public final class CallVerificationService_MembersInjector implements MembersInjector<CallVerificationService> {
  private final Provider<AutoReplyRepository> autoReplyRepositoryProvider;

  private final Provider<DiscrepancyRepository> discrepancyRepositoryProvider;

  private final Provider<AutoMessagingManager> autoMessagingManagerProvider;

  public CallVerificationService_MembersInjector(
      Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<DiscrepancyRepository> discrepancyRepositoryProvider,
      Provider<AutoMessagingManager> autoMessagingManagerProvider) {
    this.autoReplyRepositoryProvider = autoReplyRepositoryProvider;
    this.discrepancyRepositoryProvider = discrepancyRepositoryProvider;
    this.autoMessagingManagerProvider = autoMessagingManagerProvider;
  }

  public static MembersInjector<CallVerificationService> create(
      Provider<AutoReplyRepository> autoReplyRepositoryProvider,
      Provider<DiscrepancyRepository> discrepancyRepositoryProvider,
      Provider<AutoMessagingManager> autoMessagingManagerProvider) {
    return new CallVerificationService_MembersInjector(autoReplyRepositoryProvider, discrepancyRepositoryProvider, autoMessagingManagerProvider);
  }

  @Override
  public void injectMembers(CallVerificationService instance) {
    injectAutoReplyRepository(instance, autoReplyRepositoryProvider.get());
    injectDiscrepancyRepository(instance, discrepancyRepositoryProvider.get());
    injectAutoMessagingManager(instance, autoMessagingManagerProvider.get());
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.service.CallVerificationService.autoReplyRepository")
  public static void injectAutoReplyRepository(CallVerificationService instance,
      AutoReplyRepository autoReplyRepository) {
    instance.autoReplyRepository = autoReplyRepository;
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.service.CallVerificationService.discrepancyRepository")
  public static void injectDiscrepancyRepository(CallVerificationService instance,
      DiscrepancyRepository discrepancyRepository) {
    instance.discrepancyRepository = discrepancyRepository;
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.service.CallVerificationService.autoMessagingManager")
  public static void injectAutoMessagingManager(CallVerificationService instance,
      AutoMessagingManager autoMessagingManager) {
    instance.autoMessagingManager = autoMessagingManager;
  }
}
