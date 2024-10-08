package com.example.SpringBootMisson1.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getNickname(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/profile/list")
    public String profileList(Model model, Principal principal) {
        SiteUser user = userService.getUserByPrincipal(principal);
        model.addAttribute("user", user);
        return "profile_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/modifynickname/{id}")
    public String profileModifyNickname(Model model, @PathVariable("id") Long id, Principal principal) {
        SiteUser siteUser = this.userService.getUser(id);
        model.addAttribute("siteUser", siteUser);
        return "profile_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/modifynickname/{id}")
    public String modifyNickname(@ModelAttribute("siteUser") @Valid SiteUser updatedUser, BindingResult bindingResult, Principal principal, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "profile_form";
        }
        SiteUser siteUser = this.userService.getUser(id);
        this.userService.modifyNicname(siteUser, updatedUser.getNickname());
        return String.format("redirect:/user/profile/list");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/modifypassword/{id}")
    public String profileModifyPassword(Model model, @PathVariable("id") Long id) {
        // 사용자 객체 생성 및 password 필드 추가
        UserCreateForm userCreateForm = new UserCreateForm();
        model.addAttribute("passwordForm", userCreateForm);
        return "profile_form"; // 템플릿 이름
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/modifypassword/{id}")
    public String modify(@ModelAttribute("siteUser") @Valid SiteUser siteUser, BindingResult bindingResult, Principal principal, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "profile_form";
        }

        // 비밀번호 확인 로직
        if (!siteUser.getPassword1().equals(siteUser.getPassword2())) {
            bindingResult.rejectValue("password2", "error.siteUser", "비밀번호가 일치하지 않습니다.");
            return "profile_form"; // 에러가 있을 경우 다시 폼으로 돌아감
        }

        SiteUser siteUSer = this.userService.getUser(id);
        this.userService.modifypassword(siteUser, siteUser.getPassword1());
        return String.format("redirect:/user/profile/list");
    }

}