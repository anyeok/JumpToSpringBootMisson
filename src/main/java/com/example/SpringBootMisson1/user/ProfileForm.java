package com.example.SpringBootMisson1.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileForm {
    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    @Size(max = 20)
    private String password;

    @NotEmpty(message = "닉네임은 필수항목입니다.")
    @Size(max = 10)
    private String nickname;
}