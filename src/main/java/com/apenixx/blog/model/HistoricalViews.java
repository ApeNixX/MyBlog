package com.apenixx.blog.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author ApeNixX
 * @Date 2020/2/12 18:52
 * @Version 1.0
 * @Describe
 */
@Data
public class HistoricalViews  implements Serializable {
    private long id;
    private Integer views;
    private Date createBy;
}
