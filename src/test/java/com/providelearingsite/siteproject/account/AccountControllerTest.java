package com.providelearingsite.siteproject.account;

import com.providelearingsite.siteproject.account.form.AccountForm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AccountService accountService;
    @Autowired private ModelMapper modelMapper;


    @Test
    @DisplayName("로그인 폼 구현 - 성공")
    public void createAccountForm() throws Exception{
        mockMvc.perform(get("/create"))
                .andExpect(model().attributeExists("accountForm"))
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 계정 생성 - 성공")
    public void createAccount_success() throws Exception{
        mockMvc.perform(post("/create")
                .param("email", "test@test.com")
                .param("password", "1234567890")
                .param("passwordcheck", "1234567890")
                .param("nickname", "Test")
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("reEmailToken"))
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("navbar/token_validation"))
                .andExpect(status().isOk());

        Account account = accountRepository.findByNicknameAndTokenChecked("Test", false);
        assertNotNull(account);
        assertTrue(passwordEncoder.matches("1234567890", account.getPassword()));
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Password Error)")
    public void createAccount_fail_password() throws Exception{
        mockMvc.perform(post("/create")
                .param("email", "test@test.com")
                .param("password", "1234567890")
                .param("passwordcheck", "12345678")
                .param("nickname", "Test")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());

        Account account = accountRepository.findByNicknameAndTokenChecked("Test", false);
        assertNull(account);
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Blank Value)")
    public void createAccount_fail_blank() throws Exception{
        mockMvc.perform(post("/create")
                .param("email", "test@test.com")
                .param("password", "1234567890")
                .param("passwordcheck", "12345678")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());

        Account account = accountRepository.findByNicknameAndTokenChecked("Test", false);
        assertNull(account);
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Email_Duplication Error)")
    public void createAccount_fail_duplication_email() throws Exception{
        AccountForm accountForm = new AccountForm();
        accountForm.setEmail("test@test.com");
        accountForm.setPassword("1234567890");
        accountForm.setPasswordcheck("1234567890");
        accountForm.setNickname("Test");
        accountService.createAccount(modelMapper.map(accountForm, Account.class));

        mockMvc.perform(post("/create")
                .param("email", "test@test.com")
                .param("password", "1234567890")
                .param("passwordcheck", "1234567890")
                .param("nickname", "test2")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByNicknameAndTokenChecked("test2", false);
        assertNull(newAccount);
    }

    @Test
    @DisplayName("로그인 계정 생성 - 실패 (Nickname_Duplication Error)")
    public void createAccount_fail_duplication_nickname() throws Exception{
        AccountForm accountForm = new AccountForm();
        accountForm.setEmail("test@test.com");
        accountForm.setPassword("1234567890");
        accountForm.setPasswordcheck("1234567890");
        accountForm.setNickname("Test");
        accountService.createAccount(modelMapper.map(accountForm, Account.class));

        mockMvc.perform(post("/create")
                .param("email", "test2@test.com")
                .param("password", "1234567890")
                .param("passwordcheck", "1234567890")
                .param("nickname", "Test")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(view().name("navbar/create_account"))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByEmailAndTokenChecked("test2@test.com", false);
        assertNull(newAccount);
    }

    @Test
    @DisplayName("로그인 메일 인증 - 성공")
    public void checkEmailToken_success() throws Exception {
        AccountForm accountForm = new AccountForm();
        accountForm.setEmail("test@test.com");
        accountForm.setPassword("1234567890");
        accountForm.setPasswordcheck("1234567890");
        accountForm.setNickname("Test");
        Account account = accountService.createAccount(modelMapper.map(accountForm, Account.class));

        mockMvc.perform(get("/check-token")
                .param("token", account.getEmailCheckToken())
                .param("email", account.getEmail()))
                .andExpect(flash().attributeExists("account"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(redirectedUrl("/"))
                .andExpect(status().is3xxRedirection());

        Account emailAccount = accountRepository.findByEmailAndTokenChecked("test@test.com", true);
        assertNotNull(emailAccount);
        Account nicknameAccount = accountRepository.findByNicknameAndTokenChecked("Test", true);
        assertNotNull(nicknameAccount);
        assertEquals(emailAccount, nicknameAccount);
    }

    @Test
    @DisplayName("로그인 메일 인증 - 실패(token Error")
    public void checkEmailToken_fail_token() throws Exception {
        AccountForm accountForm = new AccountForm();
        accountForm.setEmail("test@test.com");
        accountForm.setPassword("1234567890");
        accountForm.setPasswordcheck("1234567890");
        accountForm.setNickname("Test");
        Account account = accountService.createAccount(modelMapper.map(accountForm, Account.class));

        mockMvc.perform(get("/check-token")
                .param("token", account.getEmailCheckToken() + "dp2")
                .param("email", account.getEmail()))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("reEmailToken"))
                .andExpect(view().name("navbar/token_validation"))
                .andExpect(status().isOk());

        Account emailAccount = accountRepository.findByEmailAndTokenChecked("test@test.com", true);
        assertNull(emailAccount);
        Account nicknameAccount = accountRepository.findByNicknameAndTokenChecked("Test", true);
        assertNull(nicknameAccount);
    }

    @Test
    @DisplayName("로그인 메일 인증 - 실패(email Error")
    public void checkEmailToken_fail_email() throws Exception {
        AccountForm accountForm = new AccountForm();
        accountForm.setEmail("test@test.com");
        accountForm.setPassword("1234567890");
        accountForm.setPasswordcheck("1234567890");
        accountForm.setNickname("Test");
        Account account = accountService.createAccount(modelMapper.map(accountForm, Account.class));

        mockMvc.perform(get("/check-token")
                .param("token", account.getEmailCheckToken())
                .param("email", "kuroneko2@naver.com"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("reEmailToken"))
                .andExpect(view().name("navbar/token_validation"))
                .andExpect(status().isOk());

        Account emailAccount = accountRepository.findByEmailAndTokenChecked("test@test.com", true);
        assertNull(emailAccount);
        Account nicknameAccount = accountRepository.findByNicknameAndTokenChecked("Test", true);
        assertNull(nicknameAccount);
    }

    @Test
    @DisplayName("로그인 메일 재인증 - 성공")
    public void reCheckEmailToken_success() throws Exception {
        AccountForm accountForm = new AccountForm();
        accountForm.setEmail("test@test.com");
        accountForm.setPassword("1234567890");
        accountForm.setPasswordcheck("1234567890");
        accountForm.setNickname("Test");
        Account account = accountService.createAccount(modelMapper.map(accountForm, Account.class));
        account.setCreateEmailToken(LocalDateTime.now().minusHours(3));

        mockMvc.perform(post("/recheck-token")
                .param("email", accountForm.getEmail())
                .with(csrf()))
                .andExpect(flash().attributeExists())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Account newAccount = accountRepository.findByEmailAndTokenChecked(accountForm.getEmail(), false);
        assertNotNull(newAccount.getEmailCheckToken());
        //emailToken 서로 다른 값 log로 확인 완료
    }

    @Test
    @DisplayName("로그인 메일 재인증 - 실패 (Time Error)")
    public void reCheckEmailToken_fail() throws Exception {
        AccountForm accountForm = new AccountForm();
        accountForm.setEmail("test@test.com");
        accountForm.setPassword("1234567890");
        accountForm.setPasswordcheck("1234567890");
        accountForm.setNickname("Test");
        accountService.createAccount(modelMapper.map(accountForm, Account.class));

        mockMvc.perform(post("/recheck-token")
                .param("email", accountForm.getEmail())
                .with(csrf()))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("emailToken"))
                .andExpect(view().name("navbar/token_validation"))
                .andExpect(status().isOk());

        Account account = accountRepository.findByEmailAndTokenChecked(accountForm.getEmail(), false);
        assertNotNull(account);
    }
}