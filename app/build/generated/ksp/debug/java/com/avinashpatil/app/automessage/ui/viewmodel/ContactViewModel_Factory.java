package com.avinashpatil.app.automessage.ui.viewmodel;

import com.avinashpatil.app.automessage.data.repository.ContactRepository;
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
public final class ContactViewModel_Factory implements Factory<ContactViewModel> {
  private final Provider<ContactRepository> contactRepositoryProvider;

  public ContactViewModel_Factory(Provider<ContactRepository> contactRepositoryProvider) {
    this.contactRepositoryProvider = contactRepositoryProvider;
  }

  @Override
  public ContactViewModel get() {
    return newInstance(contactRepositoryProvider.get());
  }

  public static ContactViewModel_Factory create(
      Provider<ContactRepository> contactRepositoryProvider) {
    return new ContactViewModel_Factory(contactRepositoryProvider);
  }

  public static ContactViewModel newInstance(ContactRepository contactRepository) {
    return new ContactViewModel(contactRepository);
  }
}
