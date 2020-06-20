package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.learning.event.LearningClosedEvent;
import com.providelearingsite.siteproject.learning.event.LearningUpdateEvent;
import com.providelearingsite.siteproject.learning.form.LearningForm;
import com.providelearingsite.siteproject.learning.event.LearningCreateEvent;
import com.providelearingsite.siteproject.review.Review;
import com.providelearingsite.siteproject.tag.Tag;
import com.providelearingsite.siteproject.tag.TagForm;
import com.providelearingsite.siteproject.tag.TagRepository;
import com.providelearingsite.siteproject.video.Video;
import com.providelearingsite.siteproject.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class LearningService {

    @Autowired private LearningRepository learningRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private VideoRepository videoRepository;
    @Autowired private ModelMapper modelMapper;
    @Autowired private ApplicationEventPublisher applicationEventPublisher;

    final File absoluteFile = new File("");
    final String rootPath = absoluteFile.getAbsolutePath();

    public Learning saveLearning(LearningForm learningForm, Account account){
        Learning learning = new Learning();

        learning.setTitle(learningForm.getTitle());
        learning.setSubscription(learningForm.getSubscription());
        learning.setLecturerName(learningForm.getLecturerName());
        learning.setLecturerDescription(learningForm.getLecturerDescription());
        learning.setPrice(learningForm.getPrice());
        learning.setKategorie(learningForm.getKategorie());
        learning.setSimplesubscription(learningForm.getSimplesubscription());
        learning.setCreateLearning(LocalDateTime.now());
        Learning newLearning = learningRepository.save(learning);
        newLearning.setAccount(account);

        return newLearning;
    }

    public void saveLearningTags(Learning learning, Tag tag){
        Tag newTag = tagRepository.save(tag);
        learning.getTags().add(newTag);
    }

    public void removeLearningTags(Learning learning, TagForm tagForm) {
        Tag tag = tagRepository.findByTitle(tagForm.getTitle());
        learning.getTags().remove(tag);
    }

    public void saveVideo(List<MultipartFile> videoFileList, Account account, Learning learning) throws IOException{
        final String accountPath = rootPath + "/src/main/resources/static/video/" + account.getId();
        final String accountLearningPath = rootPath + "/src/main/resources/static/video/" + account.getId() + "/" + learning.getTitle().trim();
        learning.setVideoCount(learning.getVideoCount() + videoFileList.size());

        //directory checking
        File accountFolder = new File(accountPath);
        if(!accountFolder.isDirectory()){
            accountFolder.mkdir();
        }
        File accountLearningfolder = new File(accountLearningPath);
        if(!accountLearningfolder.isDirectory()){
            accountLearningfolder.mkdir();
        }

        for (MultipartFile file : videoFileList){ //save the vide files in server folder
            Resource resource = file.getResource();
            try(
                    BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                            accountLearningPath + "/" + resource.getFilename()), 1024 * 500)
            ){
                Video video = new Video();
                video.setVideoServerPath(accountLearningPath +"/" + resource.getFilename());
                video.setVideoSize(file.getSize() > 0 ? file.getSize() : 0);
                video.setVideoTitle(updateVideoTitle(Objects.requireNonNull(file.getOriginalFilename())));
                video.setSaveTime(LocalDateTime.now());

                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();

                Video newVideo = videoRepository.save(video);
                learning.setVideos(newVideo);
            } catch (IOException e) {
                throw new IOException(e);
            }
        } //save the vide files in server folder

        learning.setUploadVideo(LocalDateTime.now());
        learningRepository.save(learning);
    }

    private String updateVideoTitle(String title){
        String regExp = "[a-zA-Zㄱ-ㅎ가-힣ㅏ-ㅣ\\s_](.mp3|.mp4|mkv)";
        String regExpNot = "[0-9]+(.)[0-9]+";
        String number = title.replaceAll(regExp, "").trim();
        String notNumber = title.replaceAll(regExpNot, "").trim();
        int i = number.indexOf("-");
        int strIndex = number.indexOf(notNumber.charAt(0));

        String f = number.substring(0, i); //앞
        String e = number.substring(i+1, strIndex); //뒤
        String newf = "";
        String newe = "";

        if(f.length() <= 1){
            newf = 0 + f;
        }else {
            newf = f;
        }

        if(e.length() <= 1){
            newe = 0 + e;
        }else {
            newe = e;
        }

        return newf + "-" + newe + notNumber;
    }

    public void saveBanner(MultipartFile banner, Account account, Learning learning) throws IOException{
        final String accountPath = rootPath + "/src/main/resources/static/video/" + account.getId();
        final String accountLearningPath = rootPath +  "/src/main/resources/static/video/" + account.getId() + "/" + learning.getTitle();

        learning.setBannerServerPath(accountLearningPath + "/" + banner.getResource().getFilename());

        try { //banner byte encoding
            byte[] bytes = Base64.encodeBase64(banner.getBytes());
            String bannerStr = new String(bytes, StandardCharsets.UTF_8);
            learning.setBannerBytes(bannerStr);
        }catch (IOException e){
            throw new IOException(e);
            //TODO 에러종류에 따라 코드 재작성
        } //banner byte encoding

        //directory checking
        File accountFolder = new File(accountPath);
        if(!accountFolder.isDirectory()){
            accountFolder.mkdir();
        }
        File accountLearningfolder = new File(accountLearningPath);
        if(!accountLearningfolder.isDirectory()){
            accountLearningfolder.mkdir();
        }

        Resource resource = banner.getResource();
        try(
                BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                        accountLearningPath + "/" + resource.getFilename()), 1024 * 5)
        ){ //save the banner img in server folder
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new IOException(e);
        } //save the banner img in server folder

        learningRepository.save(learning);
    }

    public boolean canListenLearning(Account account, Learning learning) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getListenLearning().contains(learning);
    }

    public void updateLearningScript(LearningForm learningForm, Account account, Long id) {
        Optional<Learning> learningById = learningRepository.findById(id);
        Learning oldLearning = learningById.orElseThrow();
        Optional<Account> byId = accountRepository.findById(account.getId());
        Account newAccount = byId.orElseThrow();

        final String learningPathBefore = rootPath + "/src/main/resources/static/video/" + account.getId() + "/" + oldLearning.getTitle().trim();
        final String learningPathAfter = rootPath +  "/src/main/resources/static/video/" + account.getId() + "/" + learningForm.getTitle().trim();

        File removeDir = new File(learningPathBefore);
        String[] removeList = removeDir.list();
        File newDir = new File(learningPathAfter);


        if(!newDir.isDirectory()){
            newDir.mkdir();
        }

        try {
            if (removeList != null) {
                for (String s : removeList) {
                    File file = new File(learningPathBefore + "/" + s);
                    inoutStream(file, learningPathAfter, s);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        if(newAccount.getLearnings().contains(oldLearning)){
            newAccount.getLearnings().remove(oldLearning);
        }

        oldLearning.setTitle(learningForm.getTitle());
        oldLearning.setSubscription(learningForm.getSubscription());
        oldLearning.setLecturerName(learningForm.getLecturerName());
        oldLearning.setLecturerDescription(learningForm.getLecturerDescription());
        oldLearning.setPrice(learningForm.getPrice());
        oldLearning.setKategorie(learningForm.getKategorie());
        oldLearning.setSimplesubscription(learningForm.getSimplesubscription());
        oldLearning.setUpdateLearning(LocalDateTime.now());
        oldLearning.setAccount(newAccount);

        try {
            String accountIdStr = newAccount.getId() + "";
            int i = oldLearning.getBannerServerPath().indexOf(accountIdStr);
            String firstStr = oldLearning.getBannerServerPath().substring(0, i);
            String secondStr = oldLearning.getBannerServerPath().substring(i + accountIdStr.length());
            oldLearning.setBannerServerPath(firstStr + accountIdStr + secondStr);
        }catch (NullPointerException e){
            log.info("banner image serverPath 미지정 = 기본 이미지 값 사용중");
        }

        if(oldLearning.isStartingLearning()){
            applicationEventPublisher.publishEvent(new LearningUpdateEvent(oldLearning));
        }
    }

    @Async
    public void inoutStream(File file, String learningPathAfter, String s) throws IOException {
        try (
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(learningPathAfter + "/" + s), 1024 * 500)
        ){
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (!file.isDirectory()) {
                file.delete();
            }else{
                FileUtils.deleteDirectory(file);
            }
        }
    }

    public void startLearning(Long id) {
        Optional<Learning> byId = learningRepository.findById(id);
        Learning learning = byId.orElseThrow();

        learning.setStartingLearning(true);
        learning.setClosedLearning(false);
        learning.setOpenLearning(LocalDateTime.now());

        applicationEventPublisher.publishEvent(new LearningCreateEvent(learning));
    }

    public void closeLearning(Long id) {
        Optional<Learning> byId = learningRepository.findById(id);
        Learning learning = byId.orElseThrow();

        learning.setStartingLearning(false);
        learning.setClosedLearning(true);
        learning.setCloseLearning(LocalDateTime.now());

        applicationEventPublisher.publishEvent(new LearningClosedEvent(learning));
    }

    public boolean checkOpenTimer(boolean startingLearning, boolean closedLearning, boolean contains) {
        return !startingLearning && closedLearning && contains;
    }

    public Object checkCloseTimer(boolean startingLearning, boolean closedLearning, boolean contains) {
        return startingLearning && !closedLearning && contains;
    }

    //TODO TestCode 지우기
    public void testLearning(Account account) {
        float z = 0;
        for(int i=0; i < 16; i++){
            LearningForm learningForm = new LearningForm();
            learningForm.setSimplesubscription("자바 테스트" + RandomString.make());
            learningForm.setKategorie("1");
            learningForm.setSubscription("자바 테스트 중 " + RandomString.make());
            learningForm.setLecturerName(RandomString.make());
            learningForm.setLecturerDescription("12년차 " + RandomString.make());
            learningForm.setPrice((int) Math.floor(Math.random() * 10000000));
            learningForm.setTitle("자바의 고급 " + RandomString.make());

            Learning learning = saveLearning(learningForm, account);
            learning.setStartingLearning(true);
            learning.setClosedLearning(false);
            learning.setOpenLearning(LocalDateTime.now().minusHours(i));

            if(z > 50){
                z = 0;
            }
            learning.setRating((float) (z * 0.1));
            learning.getTags().add(tagRepository.findByTitle("java"));

            if(i > 10){
                learning.getTags().add(tagRepository.findByTitle("spring"));
            }

            z += 1;
        }

        for(int i=0; i < 16; i++){
            LearningForm learningForm = new LearningForm();
            learningForm.setSimplesubscription("HTML 테스트" + RandomString.make());
            learningForm.setKategorie("2");
            learningForm.setSubscription("HTML 테스트 중 " + RandomString.make());
            learningForm.setLecturerName(RandomString.make());
            learningForm.setLecturerDescription("1년차 신입" + RandomString.make());
            learningForm.setPrice((int) Math.floor(Math.random() * 10000000));
            learningForm.setTitle("HTML의 초급과 고급 " + RandomString.make());
            saveLearning(learningForm, account);

            Learning learning = saveLearning(learningForm, account);
            learning.setStartingLearning(true);
            learning.setClosedLearning(false);
            learning.setOpenLearning(LocalDateTime.now().minusHours(i));

            if(z > 50){
                z = 0;
            }
            learning.setRating((float) (z * 0.1));
            learning.getTags().add(tagRepository.findByTitle("java"));

            if(i > 10){
                learning.getTags().add(tagRepository.findByTitle("spring"));
            }

            z += 1;
        }

    }

    public List<String> getContentsTitle(Learning learning) {
        List<String> contentTitle = new ArrayList<>();
        String regExp = "[a-zA-Zㄱ-ㅎ가-힣ㅏ-ㅣ\\s_](.mp3|.mp4|mkv)";
        String regExpNot = "[0-9]+(.)[0-9]+";
        learning.getVideos().stream().map(Video::getVideoTitle)
                .forEach(s -> {
                    String number = s.replaceAll(regExp, "").trim();
                    String notNumber = s.replaceAll(regExpNot, "").trim();
                    int i = number.indexOf("-");
                    int strIndex = number.indexOf(notNumber.charAt(0));

                    String f = number.substring(0, i); //앞
                    String e = number.substring(i+1, strIndex); //뒤
                    String newf = "";
                    String newe = "";

                    if(f.length() <= 1){
                        newf = 0 + f;
                    }else {
                        newf = f;
                    }

                    if(e.length() <= 1){
                        newe = 0 + e;
                    }else {
                        newe = e;
                    }
                    contentTitle.add(newf + "-" + newe + notNumber);
                });

        return contentTitle;
    }

    public Account listenLearning(Account account, Learning learning) {
        Account newAccount = accountRepository.findById(account.getId()).orElseThrow();

        newAccount.getListenLearning().add(learning);

        return newAccount;
    }

    public void setReview(Review review, Learning learning) {
        learning.setReviews(review);
        double sum = learning.getReviews().stream().mapToDouble(Review::getRating).sum();
        int ratingLength = learning.getReviews().size();
        learning.setRating((float) (sum / ratingLength));
    }
}
