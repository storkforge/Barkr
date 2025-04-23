package org.storkforge.barkr.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.exceptions.AccountNotFound;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ApiAccountController.class)
class ApiAccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AccountService service;

    @Test
    @DisplayName("Returns all accounts from service")
    @WithMockUser
    void accountsReturnsAllAccounts() throws Exception {
        List<ResponseAccount> mockAccounts = List.of(
                new ResponseAccount(1L, "testaccount", LocalDateTime.now(), "beagle", new byte[0]),
                new ResponseAccount(2L, "anotheraccount", LocalDateTime.now(), "beagle", new byte[0])
        );
        Page<ResponseAccount> accountPage = new PageImpl<>(mockAccounts);

        Pageable pageable = PageRequest.of(0, 10);
        when(service.findAll(pageable)).thenReturn(accountPage);

        mvc.perform(get("/api/accounts")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].username").value("testaccount"))
                .andExpect(jsonPath("$.content[1].username").value("anotheraccount"));
    }

    @Test
    @WithMockUser
    void findAccount() throws Exception {
        ResponseAccount mockAccount = new ResponseAccount(1L, "testAccount", LocalDateTime.now(), "beagle", new byte[0]);
        when(service.findById(1L)).thenReturn(mockAccount);

        mvc.perform(get("/api/accounts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testAccount"))
                .andExpect(jsonPath("$.breed").value("beagle"));
    }

    @Test
    @WithMockUser
    @DisplayName("Handles error for nonexistent id")
    void handlesErrorForNonexistentId() throws Exception {
        when(service.findById(1L)).thenThrow(new AccountNotFound("Account with id: 1 was not found"));

        mvc.perform(get("/api/accounts/1"))
                .andDo(print())
                .andExpect((status().isNotFound()));
    }
}
