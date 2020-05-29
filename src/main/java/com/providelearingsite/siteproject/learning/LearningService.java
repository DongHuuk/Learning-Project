package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.AccountRepository;
import com.providelearingsite.siteproject.learning.form.LearningForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class LearningService {

    @Autowired private LearningRepository learningRepository;
    @Autowired private AccountRepository accountRepository;

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

//    public void saveVideoAndBanner(List<MultipartFile> videoFileList, MultipartFile banner, Account account){
//        final String folderPath = "C:/project/" + account.getId();
//        BufferedInputStream inputStream = null;
//        BufferedOutputStream outputStream = null;
//
//        for(Learning learning : account.getLearningSet()){
//            Optional<Learning> byId = learningRepository.findById(learning.getId());
//            Learning newLearning = byId.orElseGet(Learning::new);
//        }
//
//        Learning learning = new Learning();
//        learning.setBannerPath(folderPath + "/" + banner.getResource().getFilename());
//
//        try { //banner byte encoding
//            byte[] bytes = Base64.encodeBase64(banner.getBytes());
//            String bannerStr = new String(bytes, StandardCharsets.UTF_8);
//            learning.setBanner_bytes(bannerStr);
//        }catch (IOException e){
//            log.info("banner bytes error");
//        } //banner byte encoding
//
//        //directory checking
//        File folder = new File(folderPath);
//        if(!folder.isDirectory()){
//            folder.mkdir();
//        }
//
//        for (MultipartFile file : videoFileList){ //save the vide files in server folder
//            Video video = new Video();
//            try{
//                Resource resource = file.getResource();
//                video.setVideoServerPath(folderPath +"/" + resource.getFilename());
//                video.setVideoSize(file.getSize() > 0 ? file.getSize() : 0);
//                video.setVideoTitle(file.getOriginalFilename());
//                video.setSaveTime(LocalDateTime.now());
//
//                inputStream = new BufferedInputStream(resource.getInputStream());
//                outputStream = new BufferedOutputStream(new FileOutputStream(folderPath + "/" + resource.getFilename()), 1024 * 500);
//                IOUtils.copy(inputStream, outputStream);
//                outputStream.flush();
//            } catch (IOException e) {
//                log.info(e.getMessage() + " is error code");
//            }
//            videoRepository.save(video);
//            learning.getVideos().add(video);
//        } //save the vide files in server folder
//
//        try{ //save the banner img in server folder
//            Resource resource = banner.getResource();
//            inputStream = new BufferedInputStream(resource.getInputStream());
//            outputStream = new BufferedOutputStream(new FileOutputStream(folderPath + "/" + resource.getFilename()), 1024 * 5);
//            IOUtils.copy(inputStream, outputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } //save the banner img in server folder
//
//
//        learningRepository.save(learning);
//    }



}
