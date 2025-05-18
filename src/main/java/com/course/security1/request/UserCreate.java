package com.course.security1.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreate {

    private String username;
    private String password;
    private String email;

}