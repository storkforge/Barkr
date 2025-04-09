package org.storkforge.barkr.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;

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
    void accountsReturnsAllAccounts() throws Exception {
        List<ResponseAccount> mockAccounts = List.of(
                new ResponseAccount(1L, "testaccount", LocalDateTime.now(), "beagle", new byte[0]),
                new ResponseAccount(2L, "anotheraccount", LocalDateTime.now(), "beagle", new byte[0])
        );
        when(service.findAll()).thenReturn(mockAccounts);

        mvc.perform(get("/api/accounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts.length()").value(2))
                .andExpect(jsonPath("$.accounts[0].username").value("testaccount"))
                .andExpect(jsonPath("$.accounts[1].username").value("anotheraccount"));
    }

    @Test
    void FindAccount() throws Exception {
        ResponseAccount mockAccount = new ResponseAccount(1L, "testAccount", LocalDateTime.now(), "beagle", new byte[0]);
        when(service.findById(1L)).thenReturn(mockAccount);

        mvc.perform(get("/api/accounts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testAccount"))
                .andExpect(jsonPath("$.breed").value("beagle"));
    }
}