package com.providelearingsite.siteproject.learning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.account.AccountService;
import com.providelearingsite.siteproject.account.WithAccount;
import com.providelearingsite.siteproject.learning.form.LearningForm;
import com.providelearingsite.siteproject.tag.Tag;
import com.providelearingsite.siteproject.tag.TagForm;
import com.providelearingsite.siteproject.tag.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class LearningControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private LearningRepository learningRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private LearningService learningService;
    @Autowired private TagRepository tagRepository;
    @Autowired private AccountService accountService;
    @Autowired private ObjectMapper objectMapper;


    @Test
    @DisplayName("강의 만들기 뷰")
    @WithAccount("test@naver.com")
    public void viewUpload() throws Exception{
        mockMvc.perform(get("/profile/learning/create"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learningForm"))
                .andExpect(view().name("profile/create_learning"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("강의 만들기 - 성공")
    @WithAccount("test@naver.com")
    public void createLearning() throws Exception{
        String title = "테스트_1";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "흑우냥이";

        mockMvc.perform(post("/profile/learning/create")
                .param("title", title)
                .param("subscription", subscription)
                .param("lecturerName", lecturerName)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name(LearningController.CREATE_LEARNING))
                .andExpect(status().isOk());

        Learning learning = learningRepository.findByTitle(title);

        assertNotNull(learning);
        assertEquals(learning.getTitle(), title);
        assertEquals(learning.getSubscription(), subscription);
        assertEquals(learning.getLecturerName(), lecturerName);
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 만들기 - 실패_duplication title")
    public void createLearning_fail_title() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String title = "테스트_1";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "흑우냥이";

        LearningForm learningForm = new LearningForm();
        learningForm.setTitle(title);
        learningForm.setSubscription("테스트_2 설명입니다.");
        learningForm.setLecturerName("흑우냥이_2");
        learningService.saveLearning(learningForm, account);

        mockMvc.perform(post("/profile/learning/create")
                .param("title", title)
                .param("subscription", subscription)
                .param("lecturerName", lecturerName)
                .with(csrf()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("learningForm"))
                .andExpect(model().hasErrors())
                .andExpect(view().name(LearningController.CREATE_LEARNING))
                .andExpect(status().isOk());

        Learning learning = learningRepository.findByTitle(title);

        assertNotNull(learning);
        assertEquals(learning.getTitle(), title);
        assertNotEquals(learning.getSubscription(), "테스트 설명입니다.");
        assertNotEquals(learning.getLecturerName(), "흑우냥이");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 만들기 - 실패_lecturerName null")
    public void createLearning_fail_lecturerName() throws Exception{
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String title = "테스트_1";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "";

        mockMvc.perform(post("/profile/learning/create")
                .param("title", title)
                .param("subscription", subscription)
                .param("lecturerName", lecturerName)
                .with(csrf()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attributeExists("learningForm"))
                .andExpect(model().hasErrors())
                .andExpect(view().name(LearningController.CREATE_LEARNING))
                .andExpect(status().isOk());

        Learning learning = learningRepository.findByTitle(title);

        assertNull(learning);
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 리스트 뷰")
    public void updateLearning() throws Exception {
        mockMvc.perform(get("/profile/learning/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("profile/learning_list"));
    }

    private Learning createLearning(Account account){
        LearningForm learningForm = new LearningForm();
        learningForm.setTitle("테스트_1");
        learningForm.setSubscription("테스트_1 설명입니다.");
        learningForm.setLecturerName("흑우냥이");
        return learningService.saveLearning(learningForm, account);
    }

    private void createTags(Account account){
        Tag tag_1 = tagRepository.save(Tag.builder().title("test_Tag_1").build());
        Tag tag_2 = tagRepository.save(Tag.builder().title("test_Tag_2").build());
        accountService.addTag(account, tag_1);
        accountService.addTag(account, tag_2);
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 영상 업로드 뷰 - 성공")
    public void viewVideosUpdate() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);
        createTags(account);
        List<Tag> all = tagRepository.findAll();
        for (Tag tag : all){
            learning.getTags().add(tag);
        }

        mockMvc.perform(get("/profile/learning/upload/" + learning.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learning"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("profile/learning"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 태그 추가 - 성공")
    public void learningAddTags_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);
        TagForm tagForm = new TagForm();
        tagForm.setTitle("addTags_test_1");

        mockMvc.perform(post("/profile/learning/upload/" + learning.getId() + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle(tagForm.getTitle());

        assertTrue(learning.getTags().contains(newTag));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 태그 삭제 - 성공")
    public void learningRemoveTags_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);
        createTags(account);
        Tag learningTag = tagRepository.findByTitle("test_Tag_1");
        learning.getTags().add(learningTag);

        TagForm tagForm = new TagForm();
        tagForm.setTitle("test_Tag_1");

        mockMvc.perform(post("/profile/learning/upload/" + learning.getId() + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle(tagForm.getTitle());

        assertNotNull(newTag);
        assertFalse(learning.getTags().contains(newTag));
        assertFalse(learning.getTags().contains(learningTag));
        assertEquals(learningTag, newTag);
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 태그 삭제 - 실패_not found tag")
    public void learningRemoveTags_fail() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);
        createTags(account);
        Tag learningTag = tagRepository.findByTitle("test_Tag_1");
        learning.getTags().add(learningTag);

        TagForm tagForm = new TagForm();
        tagForm.setTitle("test_Tag_3");

        mockMvc.perform(post("/profile/learning/upload/" + learning.getId() + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        Tag newTag = tagRepository.findByTitle(tagForm.getTitle());

        assertNull(newTag);
        assertTrue(learning.getTags().contains(learningTag));
    }



}