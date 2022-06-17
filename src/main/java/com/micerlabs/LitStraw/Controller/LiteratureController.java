package com.micerlabs.LitStraw.Controller;

import com.micerlabs.LitStraw.Domain.*;

import com.micerlabs.LitStraw.Service.LiteratureService;
import com.micerlabs.LitStraw.Service.MongoService;
import com.micerlabs.LitStraw.Service.RedisService;
import com.micerlabs.LitStraw.Utils.FileUtil;
import com.micerlabs.LitStraw.Utils.SimplifyUtils;
import com.micerlabs.LitStraw.VO.Result;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@CrossOrigin
@RestController
@RequestMapping("/LitStraw")
@Validated
public class LiteratureController {

    @Resource
    private LiteratureService literatureService;

    @Resource
    private RedisService redisService;

    @Resource
    private MongoService mongoService;

    @Value("${config.storePath}")
    private String storePath;

    /**
     * 一次处理一篇论文
     *
     * @return
     */
    @PostMapping("/singlePaper")
    @ResponseBody
    public Result singlePaper(FileReq fileReq) {
        String taskId = UUID.randomUUID().toString();

        // new Task()
        Task task = new Task(taskId);
        String taskPath = storePath + "/" + taskId;
        task.setStorePath(taskPath);
        new File(taskPath + "/pdf").mkdirs();

        // 固定文件位置
        String pdfFileName = fileReq.getFile().getOriginalFilename();
        try {
            InputStream inputStream = fileReq.getFile().getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, new File(new File(taskPath).getAbsolutePath() + "/pdf/" + pdfFileName));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.BAD().data("文件存储失败").build();
        }

        // 调起新线程执行singlePaper()
        literatureService.singlePaper(fileReq, task);
        return Result.OK().data(taskId).build();
    }

    /**
     * 返回Task当前状态
     *
     * @param taskId
     * @return
     */
    @GetMapping("/taskMsg")
    @ResponseBody
    public Result getTaskMsg(@RequestParam(value = "taskId") String taskId) {
        try {
            Task task = redisService.findTask(taskId);
            return Result.OK().data(task).build();
        } catch (NoSuchElementException e) {
            return Result.BAD().data("TaskId is not present").build();
        }
    }

    /**
     * 获取Task的Txt形式结果
     *
     * @param taskId
     * @return
     */
    @GetMapping("/txtResult")
    @ResponseBody
    public Result getTxtResult(@RequestParam(value = "taskId") String taskId) {
        String txtLibPath = storePath + "/" + taskId + "/txt";
        List<String> fileNames = FileUtil.getFile(txtLibPath);
        Collections.sort(fileNames);
        List<String> txtList = new ArrayList<>();
        for (String file : fileNames) {
            txtList.add(FileUtil.readFileToString(txtLibPath + "/" + file));
        }
        return Result.OK().data(txtList).build();
    }

    /**
     * 获取Task的Json形式结果
     *
     * @param taskId
     * @return
     */
    @GetMapping("/jsonResult")
    @ResponseBody
    public Result getJsonResult(@RequestParam(value = "taskId") String taskId) {
        List<EasyLiterature> easyLiteratures = new ArrayList<>();
        easyLiteratures.add(SimplifyUtils.simplifyLiterature(mongoService.findLiteratureById(taskId)));
        return Result.OK().data(easyLiteratures).build();
    }

}
