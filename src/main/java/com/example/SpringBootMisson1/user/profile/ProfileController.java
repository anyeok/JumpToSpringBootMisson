package com.example.SpringBootMisson1.user.profile;

import com.example.SpringBootMisson1.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/user")
@Controller
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;

    @GetMapping("/profile")
    public String userProfile(Model model, @RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        List<Profile> profileList = this.profileService.getList(userId, keyword);
        model.addAttribute("profileList", profileList);
        return "profile_list";
    }
}