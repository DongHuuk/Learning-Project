package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.learning.form.VideoForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VideoService {

    @Autowired private VideoRepository videoRepository;
    @Autowired private ModelMapper modelMapper;

    public Video saveVideo(Video video, List<MultipartFile> multipartFileList, String  nickname){
        String folderPath = "C:/project/" + nickname;
        for (MultipartFile file : multipartFileList){
            try{
                File folder = new File(folderPath);
                if(!folder.isDirectory()){
                    folder.mkdir();
                }

                Resource resource = file.getResource();
                video.setVideoPath(folderPath +"/" + resource.getFilename());
                video.setVideoSize(resource.getFile().length());
                video.setVideoTitle(resource.getFilename());

                BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(folderPath + "/" + resource.getFilename()), 1024 * 500);
                IOUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                log.info(e.getMessage() + " is error code");
            }
        }//save the vide files in server folder

        video.setSaveTime(LocalDateTime.now());
        return videoRepository.save(video);
    }
}