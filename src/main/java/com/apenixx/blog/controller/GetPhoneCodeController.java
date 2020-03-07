package com.apenixx.blog.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.apenixx.blog.component.PhoneRandomBuilder;
import com.apenixx.blog.constant.AliyunOssConfigConstant;
import com.apenixx.blog.redis.StringRedisServiceImpl;
import com.apenixx.blog.utils.BlogJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ApeNixX
 * @Date 2020/2/2 23:52
 * @Version 1.0
 * @Describe 注册获得手机验证码
 */
@RestController
@Slf4j
public class GetPhoneCodeController {
    @Autowired
    StringRedisServiceImpl stringRedisService;

    private static final String REGISTER = "register";



    /**
     * 阿里云短信发送模板
     */
    private static final String SIGN_NAME = "浩先森的Blog";


    @PostMapping(value = "/getCode")
    public BlogJSONResult getAuthCode(HttpServletRequest request) {
        String phone = request.getParameter("phone");
        String sign = request.getParameter("sign");
        String trueMsgCode = PhoneRandomBuilder.randomBuilder();
//        在redis中保存手机号验证码并设置过期时间
        stringRedisService.set(phone, trueMsgCode);
        stringRedisService.expire(phone, 300);

        String msgCode;
        //注册的短信模板
        if(REGISTER.equals(sign)){
            msgCode = "SMS_182671127";
        }
        //改密码的短信模板
        else {
            msgCode = "SMS_182676093";
        }

        try {
            sendSmsResponse(phone, trueMsgCode, msgCode);
        } catch (ClientException e) {
            log.error("[{}] send phone message exception", phone, e);
            return BlogJSONResult.errorMsg("验证码获取失败");
        }

        return BlogJSONResult.ok();
    }

    public void sendSmsResponse(String phoneNumber, String code, String msgCode) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //"***"分别填写自己的AccessKey ID和Secret
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", AliyunOssConfigConstant.ACCESS_KEY_ID, AliyunOssConfigConstant.ACCESS_KEY_SECRET);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        //填写接收方的手机号码
        request.setPhoneNumbers(phoneNumber);
        //此处填写已申请的短信签名
        request.setSignName(SIGN_NAME);
        //此处填写获得的短信模版CODE
        request.setTemplateCode(msgCode);
        //笔者的短信模版中有${code}, 因此此处对应填写验证码
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        acsClient.getAcsResponse(request);

    }


}
