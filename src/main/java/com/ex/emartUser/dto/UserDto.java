package com.ex.emartUser.dto;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class UserDto {

    private Integer id;
    private String accountName;
   private String userPassword;         // DB: user_password
    private String userPasswordConfirm;  // 입력폼에서만 사용, DB에 저장 안 함
    private String nickname;
    private String email;
    private String gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth; 

    private String phone;
    private String useTerms;
    private LocalDateTime createdAt;
}
