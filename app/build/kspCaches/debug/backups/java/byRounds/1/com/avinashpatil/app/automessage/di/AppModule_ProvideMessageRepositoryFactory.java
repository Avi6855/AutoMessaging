package com.avinashpatil.app.automessage.di;

import com.avinashpatil.app.automessage.data.database.AutoMessageDatabase;
import com.avinashpatil.app.automessage.data.repository.MessageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideMessageRepositoryFactory implements Factory<MessageRepository> {
  private final Provider<AutoMessageDatabase> databaseProvider;

  public AppModule_ProvideMessageRepositoryFactory(Provider<AutoMessageDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public MessageRepository get() {
    return provideMessageRepository(databaseProvider.get());
  }

  public static AppModule_ProvideMessageRepositoryFactory create(
      Provider<AutoMessageDatabase> databaseProvider) {
    return new AppModule_ProvideMessageRepositoryFactory(databaseProvider);
  }

  public static MessageRepository provideMessageRepository(AutoMessageDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideMessageRepository(database));
  }
}
