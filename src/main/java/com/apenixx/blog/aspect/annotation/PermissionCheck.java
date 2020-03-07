package com.apenixx.blog.aspect.annotation;

import java.lang.annotation.*;

/**
 * @Author ApeNixX
 * @Date 2020/2/9 17:40
 * @Version 1.0
 * @Describe
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionCheck {
    String value();

}
