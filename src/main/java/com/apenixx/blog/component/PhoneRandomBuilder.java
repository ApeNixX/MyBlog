package com.apenixx.blog.component;

import org.springframework.stereotype.Component;

/**
 * @Author ApeNixX
 * @Date 2020/2/3 0:05
 * @Version 1.0
 * @Describe 手机验证码随机生成
 */
@Component
public class PhoneRandomBuilder {
    public static String randomBuilder(){

        String result = "";
        for(int i=0;i<4;i++){
            result += Math.round(Math.random() * 9);
        }

        return result;

    }
}
