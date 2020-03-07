package com.apenixx.blog.mapper;

import com.apenixx.blog.model.Resource;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/8 15:32
 * @Version 1.0
 * @Describe
 */
@Mapper
@Repository
public interface ResourceMapper {
    @Select("select * from resource where status=0 and resourceTypeName=#{resourceTypeName}")
    List<Resource> getAllResourceByType(@Param("resourceTypeName")String resourceTypeName);
    @Select("select * from resource where status=0")
    List<Resource> getAllResource1();
    @Select("select * from resource")
    List<Resource> getAllResource();
    @Insert("insert into resource(resourceName,resourceUserName,resourcePath,createTime,imgSrc,resourceDescribe,resourceTypeName,status) values(#{resourceName},#{resourceUserName},#{resourcePath},#{createTime},#{imgSrc},#{resourceDescribe},#{resourceTypeName},#{status})")
    int save(Resource resource);

    @Update("update resource set resourceName=#{resource.resourceName},resourcePath= #{resource.resourcePath},imgSrc= #{resource.imgSrc},resourceDescribe= #{resource.resourceDescribe},resourceTypeName = #{resource.resourceTypeName} where id = #{id}")
    int updateResource(@Param("resource") Resource resource, @Param("id")int id);

    @Update("update resource set status=#{status} where id = #{id}")
    int updateResourceStatus(@Param("status") int status,@Param("id")int id);

}
