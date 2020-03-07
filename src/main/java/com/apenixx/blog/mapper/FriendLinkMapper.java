package com.apenixx.blog.mapper;

import com.apenixx.blog.model.FriendLink;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/7 21:49
 * @Version 1.0
 * @Describe
 */
@Repository
@Mapper
public interface FriendLinkMapper {
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, statementType = StatementType.STATEMENT,resultType=int.class)
    @Insert("insert into friendlink(blogger,url,img) values(#{blogger},#{url},#{img})")
    int save(FriendLink friendLink);

    @Update("update friendlink set blogger=#{friendLink.blogger},url=#{friendLink.url},img=#{friendLink.img} where id=#{id}")
    void updateFriendLink(@Param("friendLink") FriendLink friendLink, @Param("id") int id);

    @Select("select * from friendlink")
    List<FriendLink> getAllFriendLink();

    @Select("select IFNULL((select id from friendlink where blogger=#{blogger}), 0)")
    int findIsExistByBlogger(String blogger);

    @Delete("delete from friendlink where id=#{id}")
    void deleteFriendLinkById(int id);
}
