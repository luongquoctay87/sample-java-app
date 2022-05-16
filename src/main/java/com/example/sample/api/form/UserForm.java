package com.example.sample.api.form;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserForm implements Serializable {
    private String username;
    private String password;
    private String rePassword;
}
