package com.avinashpatil.app.automessage.ui;

import com.avinashpatil.app.automessage.service.AutoMessagingManager;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<AutoMessagingManager> autoMessagingManagerProvider;

  public MainActivity_MembersInjector(Provider<AutoMessagingManager> autoMessagingManagerProvider) {
    this.autoMessagingManagerProvider = autoMessagingManagerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<AutoMessagingManager> autoMessagingManagerProvider) {
    return new MainActivity_MembersInjector(autoMessagingManagerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectAutoMessagingManager(instance, autoMessagingManagerProvider.get());
  }

  @InjectedFieldSignature("com.avinashpatil.app.automessage.ui.MainActivity.autoMessagingManager")
  public static void injectAutoMessagingManager(MainActivity instance,
      AutoMessagingManager autoMessagingManager) {
    instance.autoMessagingManager = autoMessagingManager;
  }
}
