package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.learning.form.VideoForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.util.List;

@Slf4j
@Service
public class VideoService {

    final String FILE_PATH = "C:/project";

    public void saveVideo(VideoForm videoForm, List<MultipartFile> multipartFileList){

        for (MultipartFile file : multipartFileList){
            try{
                Resource resource = file.getResource();
                BufferedInputStream inputStream = new BufferedInputStream(resource.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(FILE_PATH + "/" + resource.getFilename()), 1024 * 500);
                IOUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
