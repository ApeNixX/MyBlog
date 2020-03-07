package com.apenixx.blog.controller;

import com.alibaba.fastjson.JSON;
import com.apenixx.blog.mapper.TimelineMapper;
import com.apenixx.blog.model.Timeline;
import com.apenixx.blog.service.TimeLineService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.FileUtil;
import com.apenixx.blog.utils.TimeUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/2/7 15:21
 * @Version 1.0
 * @Describe
 */
@RestController
@Slf4j
public class TimeLineController {
    @Autowired
    private TimeLineService timeLineService;
    @Autowired
    private TimelineMapper timelineMapper;

    @PostMapping("/getTimeLine")
    public BlogJSONResult getTimeLine(){
        return BlogJSONResult.ok(timeLineService.getAllTimeLine());
    }

    @RequestMapping("/getDiary")
    public Map<String,Object> getDiary(int page,int limit) {
        PageInfo<Timeline> timelinePageInfo = new PageInfo<>(timeLineService.getAllTimeLineByPage(page,limit));
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",timelinePageInfo.getList());
        map.put("count",timelinePageInfo.getTotal());
        return map;
    }

    @PostMapping("/publishDiary")
    public BlogJSONResult publishDiary(Timeline timeline){
        TimeUtil timeUtil = new TimeUtil();
        timeline.setPublishDate(timeUtil.getFormatDateForFive());
        return timeLineService.insertTimeLine(timeline);
    }

    @PostMapping("/updateDiary")
    public BlogJSONResult updateDiary(Timeline timeline){
        return BlogJSONResult.ok(timelineMapper.updateDiary(timeline));
    }

    @PostMapping("/deleteDiary")
    public BlogJSONResult deleteDiary(int id){
        timelineMapper.deleteCategory(id);
        return BlogJSONResult.ok();
    }

    /**
     * 上传日记图片
     */
    @RequestMapping("/uploadFile")
    @ResponseBody
    public Map<String,Object> image(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<>();

        try {
            request.setCharacterEncoding("utf-8");
            //设置返回头后页面才能获取返回url
            response.setHeader("X-Frame-Options", "SAMEORIGIN");

            FileUtil fileUtil = new FileUtil();
            //文件路径
            String filePath = this.getClass().getResource("/").getPath().substring(1) + "blogImg/";
            log.info(this.getClass().getResource("/").getPath());
            String fileContentType = file.getContentType();
            //文件后缀
            String fileExtension = fileContentType.substring(fileContentType.indexOf("/") + 1);

            TimeUtil timeUtil = new TimeUtil();
            //文件名
            String fileName = timeUtil.getLongTime() + "." + fileExtension;

            //文件目录
            String subCatalog = "DiaryImg/" + new TimeUtil().getFormatDateForThree();
            String fileUrl = fileUtil.uploadFile(fileUtil.multipartFileToFile(file, filePath, fileName), subCatalog);
            log.info("文件路径" + filePath + "文件后缀" + fileContentType + "文件后缀" + fileExtension + "文件名" + fileName + "文件目录" + subCatalog + "文件URL" + fileUrl);
            resultMap.put("code", 0);
            resultMap.put("msg", "上传成功");
            resultMap.put("data",map2);
            map2.put("src", fileUrl);//图片url
            map2.put("title", fileName);//图片名称，这个会显示在输入框里
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  resultMap;

    }
}
