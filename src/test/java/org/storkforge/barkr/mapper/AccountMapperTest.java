package org.storkforge.barkr.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {
  @Test
  @DisplayName("Can convert entity to response object")
  void canConvertEntityToResponse() {
    Account account = new Account();
    account.setId(1L);
    account.setUsername("username");
    account.setBreed("beagle");

    assertThat(AccountMapper.mapToResponse(account))
            .isInstanceOf(ResponseAccount.class)
            .hasFieldOrPropertyWithValue("id", 1L)
            .hasFieldOrPropertyWithValue("username", "username")
            .hasFieldOrPropertyWithValue("breed", "beagle");
  }

  @Test
  @DisplayName("Returns null for null account")
  void returnsNullForNullAccount() {
    assertThat(AccountMapper.mapToResponse(null)).isNull();
  }
}
