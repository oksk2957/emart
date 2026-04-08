package com.ex.emartUser.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmartUserDTO {

    private String name;
    private String userId;
    private String userPassword;
    private String email;
    private String gender;
    private LocalDate birth;
    private String phone;
    private String useTerms;
    private LocalDateTime createdAt;
    private int roleId;

}
