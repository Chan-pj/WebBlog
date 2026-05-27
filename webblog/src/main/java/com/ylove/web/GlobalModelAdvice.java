package com.ylove.web;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ylove.service.UserService;

@ControllerAdvice(basePackages = "com.ylove.web")
public class GlobalModelAdvice {

    @Resource
    private UserService userService;

    @ModelAttribute("memberCount")
    public long memberCount() {
        return userService.countAllUsers();
    }
}