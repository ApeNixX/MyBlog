package com.apenixx.blog.model.VO;

import lombok.Data;

/**
 * @Author ApeNixX
 * @Date 2020/2/9 22:22
 * @Version 1.0
 * @Describe
 */
@Data
public class CommentVO {
    private long pId;
    private long articleId;
    private int answererId;
    private String img;
    private String content;
    private String name;
    private String time;
    private String articleName;
}
