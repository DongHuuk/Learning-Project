package com.providelearingsite.siteproject.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.account.AccountService;
import com.providelearingsite.siteproject.account.WithAccount;
import com.providelearingsite.siteproject.account.form.AccountForm;
import com.providelearingsite.siteproject.tag.Tag;
import com.providelearingsite.siteproject.tag.TagForm;
import com.providelearingsite.siteproject.tag.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountService accountService;
    @Autowired private TagRepository tagRepository;
    @Autowired private ModelMapper modelMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("프로필 대시보드 보여주기")
    @WithAccount("test@naver.com")
    public void viewProfileDashBoard() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        mockMvc.perform(get("/profile/" + account.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learningTitle"))
                .andExpect(model().attributeExists("accountQuestion"))
                .andExpect(model().attributeExists("tagList"))
                .andExpect(model().attributeExists("learnings"))
                .andExpect(view().name("profile/profile"))
                .andExpect(status().isOk());

    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정 화면 보여주기 - 성공")
    public void viewProfile_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Tag tag_1 = Tag.builder()
                .title("java")
                .build();
        Tag tag_2 = Tag.builder()
                .title("spring")
                .build();
        accountService.addTag(account, tag_1);
        accountService.addTag(account, tag_2);

        mockMvc.perform(get("/profile/" + account.getId() + "/custom"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정 화면 보여주기 - 성공_빈값")
    public void viewProfile_success_isEmpty() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        mockMvc.perform(get("/profile/" + account.getId() + "/custom"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정하기 - 성공")
    public void updateProfile_success() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        mockMvc.perform(post("/update/nickname/" + account.getId())
                .param("nickname", "테스트냥이_2")
                .param("description", "테스트 코드에용")
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeExists("account"))
                .andExpect(redirectedUrl("/profile/" + account.getId() + "/custom"))
                .andExpect(status().is3xxRedirection());

        Account newAccount = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        assertEquals(newAccount.getNickname(), "테스트냥이_2");
        assertEquals(newAccount.getDescription(), "테스트 코드에용");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정하기 - 실패(nickname pattern error)")
    public void updateProfile_fail_pattern() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        mockMvc.perform(post("/update/nickname/" + account.getId())
                .param("nickname", "테스트냥이_2( )")
                .param("description", "테스트 코드에용")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        assertNotEquals(newAccount.getNickname(), "테스트냥이_2( )");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정하기 - 실패(nickname duplication)")
    public void updateProfile_fail_duplication() throws Exception{
        Account account_1 = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        AccountForm accountForm = new AccountForm();
        accountForm.setNickname("테스트냥이2");
        accountForm.setEmail("test2@naver.com");
        accountForm.setPassword("1234567890");
        accountForm.setPasswordcheck("1234567890");
        Account account_2 = accountService.createAccount(modelMapper.map(accountForm, Account.class));
        account_2.setTokenChecked(true);

        mockMvc.perform(post("/update/nickname/" + account_1.getId())
                .param("nickname", "테스트냥이")
                .param("description", "테스트 코드에용")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        assertNotEquals(newAccount.getNickname(), "테스트냥이2");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 수정하기 - 실패(description empty and null)")
    public void updateProfile_fail_description() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        account.setDescription("테스트용 문구");

        mockMvc.perform(post("/update/nickname/" + account.getId())
                .param("nickname", "테스트냥이_3")
                .param("description", "")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE))
                .andExpect(status().isOk());

        Account newAccount = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        assertNotEquals(newAccount.getNickname(), "테스트냥이_3");
        log.info("=====================info=====================");
        log.info(newAccount.getDescription());
        log.info(newAccount.getNickname());
        log.info("=====================info=====================");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 성공")
    public void updateProfilePassword_success() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String newPassword = "0987654321";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", "1234567890")
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPassword)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("account"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/" + account.getId() + "/custom"));

        Account newAccount = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        assertTrue(passwordEncoder.matches(newPassword, newAccount.getPassword()));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 실패_passwordForm unmatch Pattern_min")
    public void updateProfilePassword_fail_pattern_min() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String newPassword = "0987654";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", "1234567890")
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPassword)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE));

        Account newAccount = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        assertFalse(passwordEncoder.matches(newPassword, newAccount.getPassword()));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 실패_passwordForm unmatch Pattern_max")
    public void updateProfilePassword_fail_pattern_max() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String newPassword = "0987654wequiosaklnlvzroppqnfslb!@$padj!212@mdpowqn!!@";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", "1234567890")
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPassword)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE));

        Account newAccount = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        assertFalse(passwordEncoder.matches(newPassword, newAccount.getPassword()));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 실패_passwordForm unmatch validator nowPassword")
    public void updateProfilePassword_fail_validator_unmatch_now() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String nowPassword = "12345678qrwq#@!";
        String newPassword = "0987654wequinlvzroppqnfslb!@$padj!212@mdpow";
        String newPasswordCheck = "0987654wequinlvzroppqnfslb!@$padj!212@mdpow";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", nowPassword)
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPasswordCheck)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE));

        Account newAccount = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        assertFalse(passwordEncoder.matches(nowPassword, account.getPassword()));
        assertFalse(passwordEncoder.matches(newPasswordCheck, newAccount.getPassword()));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 비밀번호 수정하기 - 실패_passwordForm unmatch validator newPassword")
    public void updateProfilePassword_fail_validator_unmatch_new() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String newPassword = "0987654wequinlvzroppqnfslb!@$padj!212@mdpow";
        String newPasswordCheck = "0987654wequinlvzroppqnfslb!@$padj!212@mdpowqn@";
        String nowPassword = "1234567890";

        mockMvc.perform(post("/update/password/" + account.getId())
                .param("nowPassword", nowPassword)
                .param("newPassword", newPassword)
                .param("newPasswordCheck", newPasswordCheck)
                .param("accountNickname", account.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("profileUpdateForm"))
                .andExpect(model().attributeExists("notificationUpdateForm"))
                .andExpect(status().isOk())
                .andExpect(view().name(ProfileController.CUSTOM_PROFILE));

        assertTrue(passwordEncoder.matches(nowPassword, account.getPassword()));
        assertNotEquals(newPassword, newPasswordCheck);
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 알림 설정 - 성공")
    public void updateProfileNotifications_success() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        mockMvc.perform(post("/update/noti/" + account.getId())
                .param("siteMailNotification", "true")
                .param("siteWebNotification", "true")
                .param("learningMailNotification", "true")
                .param("learningWebNotification", "true")
                .with(csrf()))
                .andExpect(flash().attributeExists("account"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/profile/" + account.getId() + "/custom"))
                .andExpect(status().is3xxRedirection());

        assertTrue(account.isSiteMailNotification());
        assertTrue(account.isSiteWebNotification());
        assertTrue(account.isLearningMailNotification());
        assertTrue(account.isLearningWebNotification());
    } //실패 가능성 0

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 태그 추가 - 성공")
    public void updateProfileTags_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        TagForm tagForm = new TagForm();
        tagForm.setTitle("test_Tag");

        mockMvc.perform(post("/update/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle(tagForm.getTitle());

        assertTrue( account.getTags().contains(newTag));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 태그 추가 중복값 - 성공")
    public void updateProfileTagsDuplication_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Tag tag = tagRepository.save(Tag.builder().title("test_Tag").build());
        accountService.addTag(account, tag);

        TagForm tagForm = new TagForm();
        tagForm.setTitle("test_Tag");

        mockMvc.perform(post("/update/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle(tagForm.getTitle());

        assertTrue(account.getTags().contains(newTag));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 태그 삭제 - 성공")
    public void removeProfileTags_success() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Tag tag = tagRepository.save(Tag.builder().title("test_Tag").build());
        accountService.addTag(account, tag);

        TagForm tagForm = new TagForm();
        tagForm.setTitle("test_Tag");

        mockMvc.perform(post("/update/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle(tagForm.getTitle());

        assertNotNull(newTag);
        assertFalse(account.getTags().contains(newTag));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("프로필 태그 삭제 - 실패")
    public void removeProfileTags_fail() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);

        TagForm tagForm = new TagForm();
        tagForm.setTitle("test_Tag");

        mockMvc.perform(post("/update/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        Tag newTag = tagRepository.findByTitle(tagForm.getTitle());

        assertNull(newTag);
    }

}