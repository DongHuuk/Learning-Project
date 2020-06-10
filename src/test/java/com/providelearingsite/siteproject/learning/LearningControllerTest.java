package com.providelearingsite.siteproject.learning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.providelearingsite.siteproject.account.*;
import com.providelearingsite.siteproject.learning.form.LearningForm;
import com.providelearingsite.siteproject.tag.Tag;
import com.providelearingsite.siteproject.tag.TagForm;
import com.providelearingsite.siteproject.tag.TagRepository;
import com.providelearingsite.siteproject.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class LearningControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LearningRepository learningRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LearningService learningService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    @DisplayName("강의 만들기 뷰")
    @WithAccount("test@naver.com")
    public void viewUpload() throws Exception {
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
    public void createLearning() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String title = "테스트_1";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "흑우냥이";
        String lectureSubscription = "게시자에 대한 설명입니다.";

        mockMvc.perform(post("/profile/learning/create")
                .param("title", title)
                .param("subscription", subscription)
                .param("lecturerName", lecturerName)
                .param("lecturerDescription", lectureSubscription)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("account"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/profile/learning/create"))
                .andExpect(status().is3xxRedirection());

        Learning learning = learningRepository.findByTitle(title);

        assertNotNull(learning);
        assertEquals(learning.getTitle(), title);
        assertEquals(learning.getSubscription(), subscription);
        assertEquals(learning.getLecturerName(), lecturerName);
        assertEquals(learning.getLecturerDescription(), lectureSubscription);

        assertTrue(account.getLearnings().contains(learning));
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 만들기 - 실패_lecturerDescription null")
    public void createLearning_fail_lecturerDescription() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String title = "";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "test";

        mockMvc.perform(post("/profile/learning/create")
                .param("title", "Test_Title")
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
    @DisplayName("강의 만들기 - 실패_title null")
    public void createLearning_fail_title() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String title = "";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "test";
        String lectureSubscription = "게시자에 대한 설명입니다.";

        mockMvc.perform(post("/profile/learning/create")
                .param("subscription", subscription)
                .param("lecturerName", lecturerName)
                .param("lecturerDescription", lectureSubscription)
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
    @DisplayName("강의 만들기 - 실패_subscription null")
    public void createLearning_fail_subscription() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String title = "테스트_1";
        String subscription = "";
        String lecturerName = "흑우냥이_1";
        String lectureSubscription = "게시자에 대한 설명입니다.";

        mockMvc.perform(post("/profile/learning/create")
                .param("title", title)
                .param("lecturerName", lecturerName)
                .param("lecturerDescription", lectureSubscription)
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
    @DisplayName("강의 만들기 - 실패_lecturerName null")
    public void createLearning_fail_lecturerName() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        String title = "테스트_1";
        String subscription = "테스트 설명입니다.";
        String lecturerName = "";
        String lectureSubscription = "게시자에 대한 설명입니다.";

        mockMvc.perform(post("/profile/learning/create")
                .param("title", title)
                .param("subscription", subscription)
                .param("lecturerDescription", lectureSubscription)
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

    private Learning createLearning(Account account) {
        LearningForm learningForm = new LearningForm();
        learningForm.setTitle("테스트_1");
        learningForm.setSubscription("테스트_1 설명입니다.");
        learningForm.setLecturerName("흑우냥이");
        learningForm.setLecturerDescription("테스트 게시자 설명입니다.");
        return learningService.saveLearning(learningForm, account);
    }

    private void createTags(Account account) {
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
        for (Tag tag : all) {
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


    @Test
    @WithAccount("test@naver.com")
    @DisplayName("동영상 업로드 - 성공")
    public void videoUpdate_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);

        MockMultipartFile file_1 = new MockMultipartFile("videofile", "test_movie_1.mkv", "text/plain", "movie1 data".getBytes());
        MockMultipartFile file_2 = new MockMultipartFile("videofile", "test_movie_2.mp3", "text/plain", "movie2 data".getBytes());
        MockMultipartFile file_3 = new MockMultipartFile("videofile", "test_movie_3.mp4", "text/plain", "movie3 data".getBytes());


        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/video")
                .file(file_1)
                .file(file_2)
                .file(file_3)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        List<String> videos = learning.getVideos().stream().map(Video::getVideoTitle).collect(Collectors.toList());

        assertTrue(learning.getVideoCount() > 0);
        assertEquals(videos.size(), 3);
        assertNotNull(learning.getUploadVideo());

        log.info("===========info");
        for (int i = 0; i < videos.size(); i++) {
            log.info(videos.get(i));
            log.info(i + "");
        }
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("동영상 업로드 - 실패_values null")
    public void videoUpdate_fail() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/video")
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        List<String> videos = learning.getVideos().stream().map(Video::getVideoTitle).collect(Collectors.toList());

        assertEquals(learning.getVideoCount(), 0);
        assertTrue(videos.isEmpty());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("배너 이미지 업로드 - 성공")
    public void videoBanner_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);

        MockMultipartFile file_png = new MockMultipartFile("banner", "test_movie_1.png", "text/plain", "movie1 data".getBytes());
        MockMultipartFile file_jpg = new MockMultipartFile("banner", "test_movie_1.jpg", "text/plain", "movie1 data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/banner")
                .file(file_png)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        assertNotNull(learning.getBannerServerPath());
        assertNotNull(learning.getBannerBytes());

        log.info("===============info");
        log.info(learning.getBannerServerPath());
        log.info(learning.getBannerBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/banner")
                .file(file_jpg)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        assertNotNull(learning.getBannerServerPath());
        assertNotNull(learning.getBannerBytes());

        log.info("===============info");
        log.info(learning.getBannerServerPath());
        log.info(learning.getBannerBytes());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("배너 이미지 업로드 - 실패_values null")
    public void videoBanner_fail() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);

        MockMultipartFile file_png = new MockMultipartFile("banner", "test_movie_1.mp3", "text/plain", "movie1 data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/learning/upload/" + learning.getId() + "/banner")
                .file(file_png)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        assertNull(learning.getBannerServerPath());
        assertNull(learning.getBannerBytes());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 상세 페이지 뷰")
    public void viewMainLearning() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/learning/" + learning.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("listenLearning"))
                .andExpect(model().attributeExists("learningSet"))
                .andExpect(model().attributeExists("countVideo"))
                .andExpect(model().attributeExists("learning"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("ratings"))
                .andExpect(model().attributeExists("halfrating"))
                .andExpect(model().attributeExists("rating"))
                .andExpect(model().attributeExists("learningRating"))
                .andExpect(view().name("learning/main_learning"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 페이지에서 편집 화면으로 이동")
    public void updateMainLearning() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/profile/learning/update/" + learning.getId()))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("learning"))
                .andExpect(model().attributeExists("learningForm"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/update_learning"));

    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 성공")
    public void updateLearningScript_success() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("title", learning.getTitle())
                .param("subscription", "테스트_1 수정 설명입니다.")
                .param("lecturerName", "mark.2_흑우냥이")
                .param("lecturerDescription", "테스트_게시자_수정_입니다.")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        assertEquals(learning.getSubscription(), "테스트_1 수정 설명입니다.");
        assertEquals(learning.getLecturerName(), "mark.2_흑우냥이");
        assertEquals(learning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 실패 title null")
    public void updateLearningScript_success_title_null() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("subscription", "테스트_1 수정 설명입니다.")
                .param("lecturerName", "mark.2_흑우냥이")
                .param("lecturerDescription", "테스트_게시자_수정_입니다.")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        assertNotEquals(learning.getSubscription(), "테스트_1 수정 설명입니다.");
        assertNotEquals(learning.getLecturerName(), "mark.2_흑우냥이");
        assertNotEquals(learning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 실패 subscription null")
    public void updateLearningScript_success_subscription_null() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("title", "테스트_2")
                .param("lecturerName", "mark.2_흑우냥이")
                .param("lecturerDescription", "테스트_게시자_수정_입니다.")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        assertNotEquals(learning.getTitle(), "테스트_2");
        assertNotEquals(learning.getLecturerName(), "mark.2_흑우냥이");
        assertNotEquals(learning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 실패 lecturerDescription null")
    public void updateLearningScript_success_lecturerDescription_null() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("title", "테스트_2")
                .param("lecturerName", "mark.2_흑우냥이")
                .param("subscription", "테스트_1 수정 설명입니다.")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        assertNotEquals(learning.getTitle(), "테스트_2");
        assertNotEquals(learning.getLecturerName(), "mark.2_흑우냥이");
        assertNotEquals(learning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 편집 페이지에서 편집 - 실패 lecturerName null")
    public void updateLearningScript_success_lecturerName_null() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);
        learning.setBannerServerPath("C:/project/테스트_코드_1/"+ account.getId() +"/fpewjpoeq.jpg");

        mockMvc.perform(post("/profile/learning/update/" + learning.getId() + "/script")
                .param("title", "테스트_2")
                .param("subscription", "테스트_2 수정 설명코드입니다.")
                .param("lecturerDescription", "테스트_게시자_수정_입니다.")
                .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/learning/update/" + learning.getId()));

        assertNotEquals(learning.getTitle(), "테스트_2");
        assertNotEquals(learning.getSubscription(), "테스트_2 수정 설명코드입니다.");
        assertNotEquals(learning.getLecturerDescription(), "테스트_게시자_수정_입니다.");
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 상세 페이지 공개 전환")
    public void startLearning() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/profile/learning/start/" + learning.getId()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/learning/" + learning.getId()));

        assertTrue(learning.isStartingLearning());
        assertFalse(learning.isClosedLearning());
    }

    @Test
    @WithAccount("test@naver.com")
    @DisplayName("강의 상세 페이지 공개 취소(닫기)")
    public void closedLearning() throws Exception {
        Account account = accountRepository.findByEmailAndTokenChecked("test@naver.com", true);
        Learning learning = createLearning(account);

        mockMvc.perform(get("/profile/learning/close/" + learning.getId()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/learning/" + learning.getId()));

        assertFalse(learning.isStartingLearning());
        assertTrue(learning.isClosedLearning());
    }
    /*
    TODO File Mokito 파일이 생성됬는지 확인하는 방법?
      있으면? 확인해봣었는데 없었음..
      그냥 then이나 return값 받아오는건 있고 값이 있는지 확인하는건 없었음
    */
}