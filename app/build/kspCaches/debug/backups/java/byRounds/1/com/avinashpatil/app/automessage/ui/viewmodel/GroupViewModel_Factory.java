package com.avinashpatil.app.automessage.ui.viewmodel;

import com.avinashpatil.app.automessage.data.repository.GroupRepository;
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
public final class GroupViewModel_Factory implements Factory<GroupViewModel> {
  private final Provider<GroupRepository> groupRepositoryProvider;

  public GroupViewModel_Factory(Provider<GroupRepository> groupRepositoryProvider) {
    this.groupRepositoryProvider = groupRepositoryProvider;
  }

  @Override
  public GroupViewModel get() {
    return newInstance(groupRepositoryProvider.get());
  }

  public static GroupViewModel_Factory create(Provider<GroupRepository> groupRepositoryProvider) {
    return new GroupViewModel_Factory(groupRepositoryProvider);
  }

  public static GroupViewModel newInstance(GroupRepository groupRepository) {
    return new GroupViewModel(groupRepository);
  }
}
