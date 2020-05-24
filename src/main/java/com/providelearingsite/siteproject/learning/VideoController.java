package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.form.VideoForm;
import com.providelearingsite.siteproject.learning.validator.VideoValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
public class VideoController {

    @Autowired private VideoService videoService;
    @Autowired private ModelMapper modelMapper;
    @Autowired private VideoValidator videoValidator;

    @InitBinder("videoForm")
    private void initVideoForm(WebDataBinder webDataBinder){
        webDataBinder.addValidators(videoValidator);
    }

    @GetMapping("/profile/{id}/upload")
    public String viewUpload(@CurrentAccount Account account, @PathVariable Long id, Model model) throws IOException {
        model.addAttribute(account);
        model.addAttribute(new VideoForm());
        return "profile/upload";
    }

    @PostMapping("/profile/{id}/upload")
    public String updateVideo(@CurrentAccount Account account, @PathVariable Long id, Model model
            ,MultipartHttpServletRequest multipartHttpServletRequest, @Valid VideoForm videoForm, Errors errors) {
        List<MultipartFile> videoFileList = multipartHttpServletRequest.getFiles("file");
        MultipartFile banner = multipartHttpServletRequest.getFile("banner");

        if(videoFileList.isEmpty() || banner == null || errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute("message", "다시 입력해주세요.");
            return "profile/upload";
        }

        Video video = videoService.saveVideo(modelMapper.map(videoForm, Video.class), videoFileList, banner, account.getId());

        model.addAttribute(account);
        model.addAttribute("imgePath", video.getBanner());
        model.addAttribute("message", "추가되었습니다");
        return "profile/upload";
    }
}
