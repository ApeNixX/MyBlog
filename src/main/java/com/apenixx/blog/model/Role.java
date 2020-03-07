package com.apenixx.blog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ApeNixX
 * @Date 2020/1/19 15:33
 * @Version 1.0
 * @Describe 权限
 */
@Data
@NoArgsConstructor
public class Role {
    private int id;

    private String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
