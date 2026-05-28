package com.chan.web;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.chan.service.UserService;

@ControllerAdvice(basePackages = "com.chan.web")
public class GlobalModelAdvice {

    @Resource
    private UserService userService;

    @ModelAttribute("memberCount")
    public long memberCount() {
        return userService.countAllUsers();
    }
}