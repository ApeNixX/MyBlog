package com.apenixx.blog.service.impl;

import com.apenixx.blog.mapper.ArchiveMapper;
import com.apenixx.blog.service.ArchiveService;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/30 16:51
 * @Version 1.0
 * @Describe
 */
@Service
public class ArchiveServiceImpl implements ArchiveService {

    @Autowired
    ArchiveMapper archiveMapper;
    @Autowired
    ArticleService articleService;

    @Override
    public BlogJSONResult findArchiveNameAndArticleNum() {
        List<String> archives = archiveMapper.findArchives();
        JSONArray archivesJsonArray = new JSONArray();
        JSONObject archiveJson;
        TimeUtil timeUtil = new TimeUtil();
        for(String archive:archives){
            archiveJson = new JSONObject();
            archiveJson.put("archiveName",archive);
            archive = timeUtil.timeYearToWhippletree(archive);
            archiveJson.put("archiveArticleNum",articleService.countArticleArchiveByArchive(archive));
            archivesJsonArray.add(archiveJson);
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("result",archivesJsonArray);
        return BlogJSONResult.ok(returnJson);
    }

    @Override
    public void addArchiveName(String archiveName) {
        int archiveNameIsExist = archiveMapper.findArchiveNameByArchiveName(archiveName);
        if(archiveNameIsExist == 0){
            archiveMapper.save(archiveName);
        }
    }
}
