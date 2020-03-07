package com.apenixx.blog.mapper;

import com.apenixx.blog.model.Role;
import com.apenixx.blog.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/19 15:35
 * @Version 1.0
 * @Describe user表SQL语句
 */
@Mapper
@Repository
public interface UserMapper {
    @Select("select * from user")
    List<User> getAllUser();
    @Select("select * from user where phone=#{phone}")
    @Results({
            @Result(column = "username", property = "username"),
            @Result(column = "avatarImgUrl", property = "avatarImgUrl"),
            @Result(column = "password", property = "password"),
            @Result(column = "phone", property = "roles", javaType = List.class, many = @Many(select = "com.apenixx.blog.mapper.UserMapper.getRoleNameByPhone"))
    })
    User getUsernameAndRolesByPhone(@Param("phone") String phone);

    @Select("select r.name from user u LEFT JOIN user_role sru on u.id= sru.User_id LEFT JOIN role r on sru.Role_id=r.id where phone=#{phone}")
    Role getRoleNameByPhone(String phone);

    @Select("select count(*) from user_role where User_id=#{userId}")
    int getRoleNumByUserId(@Param("userId")int userId);

    @Select("select * from user where username=#{username}")
    User findUsernameByUsername(@Param("username") String username);


    @Select("select * from user where phone=#{phone}")
    User findUserByPhone(@Param("phone") String phone);

    @Select("select id from user where phone=#{phone}")
    int findUserIdByPhone(@Param("phone") String phone);

    @Insert("insert into user(phone,username,password,gender,avatarImgUrl) values(#{phone},#{username},#{password},#{gender},#{avatarImgUrl})")
    void save(User user);

    @Insert("insert into user_role(User_id, Role_id) values (#{userId}, #{roleId})")
    void saveRole(@Param("userId") int userId, @Param("roleId") int roleId);

    @Delete("delete from user_role where User_id=#{userId} and Role_id=#{roleId}")
    int deleteRole(@Param("userId") int userId, @Param("roleId") int roleId);


    @Select("select * from user where username=#{username}")
    User getUserPersonalInfoByUsername(@Param("username") String username);

    @Update("update user set username=#{user.username},gender=#{user.gender},trueName=#{user.trueName},birthday=#{user.birthday},email=#{user.email},personalBrief=#{user.personalBrief} where username=#{username}")
    void savePersonalDate(@Param("user") User user, @Param("username") String username);

    @Update("update user set avatarImgUrl=#{avatarImgUrl} where id=#{id}")
    void updateAvatarImgUrlById(@Param("avatarImgUrl") String avatarImgUrl, @Param("id") int id);

    @Select("select id from user where username=#{username}")
    int findIdByUsername(String username);

    @Select("select avatarImgUrl from user where id=#{id}")
    String getHeadPortraitUrl(@Param("id") int id);

    @Select("select username from user where id=#{id}")
    String findUsernameById(int id);

    @Update("update user set password=#{password} where phone=#{phone}")
    void updatePassword(@Param("phone") String phone, @Param("password") String password);

    @Update("update user set recentlyLanded=#{recentlyLanded} where phone=#{phone}")
    void updateRecentlyLanded(@Param("phone") String phone, @Param("recentlyLanded") String recentlyLanded);

    @Select("select phone from user where username=#{username}")
    String findPhoneByUsername(@Param("username") String username);

    @Select("select count(*) from user")
    int countUserNum();

    @Update("update user set status=#{status} where id=#{id}")
    int updateUserStatus(@Param("status")int status,@Param("id")int id);


    @Update("update user set username=#{user.username},avatarImgUrl=#{user.avatarImgUrl} where id=#{id}")
    int updateUser(@Param("user")User user,@Param("id")int id);


    @Select("select * from user order by recentlyLanded desc limit 4")
    List<User> getUserByRecent();

    @Select("select * from user")
    @Results({
            @Result(column = "id",property = "id"),
            @Result(column = "phone", property = "phone"),
            @Result(column = "username", property = "username"),
            @Result(column = "avatarImgUrl", property = "avatarImgUrl"),
            @Result(column = "phone", property = "roles", javaType = List.class, many = @Many(select = "com.apenixx.blog.mapper.UserMapper.getRoleNameByPhone"))
    })
    List<User> getAllUserAndRoles();
}
