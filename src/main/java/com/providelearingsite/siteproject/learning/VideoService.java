package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.learning.form.VideoForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class VideoService {

    final String FILE_PATH = "C:/project";

    public void saveVideo(VideoForm videoForm, MultipartFile file) throws IOException {

        Resource resource = file.getResource();

        BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream());
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(FILE_PATH + "/" + resource.getFilename()), 1024 * 10);
        IOUtils.copy(inputStream, outputStream);

    }
}
