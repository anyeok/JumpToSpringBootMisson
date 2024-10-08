package com.example.SpringBootMisson1.user;

import com.example.SpringBootMisson1.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String nickname, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser getUserId(Long id) {
        Optional<SiteUser> siteUser = this.userRepository.findById(id);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public List<SiteUser> getList() {
        return this.userRepository.findAll();
    }

    public SiteUser getUser(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("profile not found"));
    }


    public void modifyNicname(SiteUser siteUser, String nickname) {
        siteUser.setNickname(nickname);
        siteUser.setModifyDate(LocalDateTime.now());
        this.userRepository.save(siteUser);
    }

    public void modifypassword(SiteUser siteUser, String password) {
        siteUser.setPassword(password); // password1에서 가져온 비밀번호로 설정
        siteUser.setModifyDate(LocalDateTime.now());
        this.userRepository.save(siteUser);
    }

    public SiteUser getUserByPrincipal(Principal principal) {
        if (principal == null) {
            throw new UsernameNotFoundException("사용자가 인증되지 않았습니다.");
        }

        String username = principal.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }
}