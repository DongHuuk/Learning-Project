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
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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

    public Learning saveLearning(LearningForm learningForm, Account account){
        Optional<Account> byId = accountRepository.findById(account.getId());
        Learning learning = new Learning();

        learning.setTitle(learningForm.getTitle());
        learning.setSubscription(learningForm.getSubscription());
        learning.setCreateLearning(LocalDateTime.now());
        Account newAccount = byId.orElseThrow();
        newAccount.getLearningSet().add(learning);
        learning.setAccount(newAccount);

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

    public void saveVideoAndBanner(List<MultipartFile> videoFileList, MultipartFile banner, Account account, Learning learning){
        final String accountPath = "C:/project/" + account.getId();
        final String accountLearningPath = "C:/project/" + account.getId() + "/" + learning.getId();
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;

        Video video = new Video();
        video.setBannerServerPath(accountLearningPath + "/" + banner.getResource().getFilename());

        try { //banner byte encoding
            byte[] bytes = Base64.encodeBase64(banner.getBytes());
            String bannerStr = new String(bytes, StandardCharsets.UTF_8);
            video.setBannerBytes(bannerStr);
        }catch (IOException e){
            log.info("banner bytes error");
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

        for (MultipartFile file : videoFileList){ //save the vide files in server folder
            try{
                Resource resource = file.getResource();
                video.setVideoServerPath(accountLearningPath +"/" + resource.getFilename());
                video.setVideoSize(file.getSize() > 0 ? file.getSize() : 0);
                video.setVideoTitle(file.getOriginalFilename());
                video.setSaveTime(LocalDateTime.now());

                inputStream = new BufferedInputStream(resource.getInputStream());
                outputStream = new BufferedOutputStream(new FileOutputStream(accountLearningPath + "/" + resource.getFilename()), 1024 * 500);
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
            } catch (IOException e) {
                log.info(e.getMessage() + " is error code");
            }
            videoRepository.save(video);
            learning.setVideos(video);
        } //save the vide files in server folder

        try{ //save the banner img in server folder
            Resource resource = banner.getResource();
            inputStream = new BufferedInputStream(resource.getInputStream());
            outputStream = new BufferedOutputStream(new FileOutputStream(accountLearningPath + "/" + resource.getFilename()), 1024 * 5);
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } //save the banner img in server folder

        learning.setUploadVideo(LocalDateTime.now());
        learningRepository.save(learning);
    }



}
