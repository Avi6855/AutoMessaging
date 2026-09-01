package com.avinashpatil.app.automessage.ui.screens.contacts;

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
public final class ContactsViewModel_Factory implements Factory<ContactsViewModel> {
  private final Provider<ContactRepository> contactRepositoryProvider;

  public ContactsViewModel_Factory(Provider<ContactRepository> contactRepositoryProvider) {
    this.contactRepositoryProvider = contactRepositoryProvider;
  }

  @Override
  public ContactsViewModel get() {
    return newInstance(contactRepositoryProvider.get());
  }

  public static ContactsViewModel_Factory create(
      Provider<ContactRepository> contactRepositoryProvider) {
    return new ContactsViewModel_Factory(contactRepositoryProvider);
  }

  public static ContactsViewModel newInstance(ContactRepository contactRepository) {
    return new ContactsViewModel(contactRepository);
  }
}
