package com.apenixx.blog.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author ApeNixX
 * @Date 2020/1/27 14:00
 * @Version 1.0
 * @Describe 阿里云连接密钥
 */
@Component
public class AliyunOssConfigConstant {
    /**
     * 阿里云API的外网域名
     */
    public static final String ENDPOINT = "oss-cn-shenzhen.aliyuncs.com";

    /**
     * 阿里云API的密钥Access Key ID
     */

    public static String ACCESS_KEY_ID = "LTAI4FwSMnvVzejVWYgaP2CB";
    /**
     *阿里云API的密钥Access Key Secret
     */
    public static String ACCESS_KEY_SECRET = "UKN96nKdjbGFONo7iElMcUCMrFE9v2";

    /**
     * 阿里云API的bucket名称
     * 在阿里云上自己创建一个bucket
     */
    public static final String BACKET_NAME = "apenixx-oss";

    /**
     * 阿里云API的文件夹名称
     * 在阿里云上自己创建一个文件夹，方便分类管理图片
     */
    public static final String FOLDER="public/";

//
//    @Value("${aliyun.accessKeyId}")
//    public static void setAccessKeyId(String accessKeyId) {
//        ACCESS_KEY_ID = accessKeyId;
//    }
//
//
//    @Value("${aliyun.secret}")
//    public static void setAccessKeySecret(String accessKeySecret) {
//        ACCESS_KEY_SECRET = accessKeySecret;
//    }
}
