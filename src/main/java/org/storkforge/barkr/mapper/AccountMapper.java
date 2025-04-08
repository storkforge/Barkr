package org.storkforge.barkr.mapper;

import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.dto.accountDto.CreateAccount;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.accountDto.UpdateAccount;

public class AccountMapper {

    public static ResponseAccount mapToResponse(Account account) {
        if (account == null) {
            return null;
        }
        return new ResponseAccount(
                account.getId(),
                account.getUsername(),
                account.getCreatedAt(),
                account.getBreed(),
                account.getImage()
        );
    }

    public static Account mapToEntity(CreateAccount dto) {
        if (dto == null) {
            return null;
        }
        Account account = new Account();
        account.setUsername(dto.username());
        return account;
    }

    public static void mapToUpdateEntity(Account account, UpdateAccount dto) {
        if (account == null || dto == null) {
            return ;
        }
        account.setUsername(dto.username());
    }
}
