package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.account.CurrentAccount;
import com.providelearingsite.siteproject.learning.form.VideoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.util.List;

@Controller
public class VideoController {

    @Autowired private VideoService videoService;

    @GetMapping("/profile/{id}/upload")
    public String viewUpload(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        model.addAttribute(account);
        model.addAttribute(new VideoForm());
        return "profile/upload";
    }

    @PostMapping("/profile/{id}/upload")
    public String updateVideo(@CurrentAccount Account account, @PathVariable Long id, Model model
            ,MultipartHttpServletRequest multipartHttpServletRequest, @Valid VideoForm videoForm, Errors errors) {
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute("message", "다시 입력해주세요.");
            return "profile/upload";
        }
        List<MultipartFile> multipartFileList = multipartHttpServletRequest.getFiles("file");
        videoService.saveVideo(videoForm, multipartFileList);

        model.addAttribute(account);
        model.addAttribute("message", "추가되었습니다");
        return "profile/upload";
    }
}
