package com.apenixx.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author ApeNixX
 * @Date 2020/2/9 16:15
 * @Version 1.0
 * @Describe 错误页面跳转
 */
@Controller
public class ErrorController {
    @GetMapping("/404")
    public String error404(){
        return "error/404";
    }

    @GetMapping("/403")
    public String error403(){
        return "error/403";
    }
}
