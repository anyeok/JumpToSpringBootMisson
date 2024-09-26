package com.example.SpringBootMisson1.user.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public List<Profile> getList(Long userId, String keyword) {
        return profileRepository.findAllByUserIdAndKeyword(userId, keyword);
    }

//    public Profile getProfile(Integer id) {
//        Optional<Profile> profile = this.profileRepository.findById(id);
//        if (profile.isPresent()) {
//            return profile.get();
//        } else {
//            throw new DataNotFoundException("profile not found");
//        }
//    }
}
