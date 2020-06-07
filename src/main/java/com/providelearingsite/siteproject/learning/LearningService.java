package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.learning.form.LearningForm;
import com.providelearingsite.siteproject.tag.Tag;
import com.providelearingsite.siteproject.tag.TagForm;
import com.providelearingsite.siteproject.tag.TagRepository;
import com.providelearingsite.siteproject.video.Video;
import com.providelearingsite.siteproject.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class LearningService {

    @Autowired private LearningRepository learningRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private VideoRepository videoRepository;
    @Autowired private ModelMapper modelMapper;

    public Learning saveLearning(LearningForm learningForm, Account account){
        Learning learning = new Learning();

        learning.setTitle(learningForm.getTitle());
        learning.setSubscription(learningForm.getSubscription());
        learning.setLecturerName(learningForm.getLecturerName());
        learning.setCreateLearning(LocalDateTime.now());
        account.getLearningSet().add(learning);
        learning.setAccount(account);

        return learningRepository.save(learning);
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
        final String accountPath = "C:/project/" + account.getId();
        final String accountLearningPath = "C:/project/" + account.getId() + "/" + learning.getTitle().trim();
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
                video.setVideoTitle(file.getOriginalFilename());
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

    public void saveBanner(MultipartFile banner, Account account, Learning learning) throws IOException{
        final String accountPath = "C:/project/" + account.getId();
        final String accountLearningPath = "C:/project/" + account.getId() + "/" + learning.getTitle();

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

        final String learningPathBefore = "C:/project/" + account.getId() + "/" + oldLearning.getTitle().trim();
        final String learningPathAfter = "C:/project/" + account.getId() + "/" + learningForm.getTitle().trim();

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

        oldLearning.setTitle(learningForm.getTitle());
        oldLearning.setSubscription(learningForm.getSubscription());
        oldLearning.setLecturerName(learningForm.getLecturerName());

        try {
            String accountIdStr = account.getId() + "";
            int i = oldLearning.getBannerServerPath().indexOf(accountIdStr);
            String firstStr = oldLearning.getBannerServerPath().substring(0, i);
            String secondStr = oldLearning.getBannerServerPath().substring(i + accountIdStr.length());
            oldLearning.setBannerServerPath(firstStr + accountIdStr + secondStr);
        }catch (NullPointerException e){
            log.info("banner image serverPath 미지정 = 기본 이미지 값 사용중");
        }

        if(account.getLearningSet().contains(oldLearning)){
            account.removeLearningSet(oldLearning);
            account.setLearningSet(oldLearning);
        }

        accountRepository.save(account);
        learningRepository.save(oldLearning);
    }

    @Async
    private void inoutStream(File file, String learningPathAfter, String s) throws IOException {
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
        learning.setOpenLearning(LocalDateTime.now());
    }

    public void closeLearning(Long id) {
        Optional<Learning> byId = learningRepository.findById(id);
        Learning learning = byId.orElseThrow();
        learning.setStartingLearning(false);
        learning.setClosedLearning(true);
        learning.setCloseLearning(LocalDateTime.now());
    }
}
