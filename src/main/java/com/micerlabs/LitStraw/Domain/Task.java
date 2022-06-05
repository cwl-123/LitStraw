package com.micerlabs.LitStraw.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Task")
public class Task implements Serializable {
    // 唯一Id 用UUID UUID.randomUUID().toString()
    @Id
    private String taskId;
    // 文件数量
    private int fileNum = 1;
    // 任务创建时间 "yyyy-MM-dd :hh:mm:ss"
    private String startTime;
    // 任务结束时间 "yyyy-MM-dd :hh:mm:ss"
    private String endTime;
    // Task 总耗时
    private long costTime;
    // 每个文件平均耗时 ms
    private long avgTime;
    // 当前进展提示
    private String progress;
    // 当前进展百分比 0-100
    private int percentage;
    // 前置存储路径 "materialLib/taskId"
    private String storePath;
    // 报错信息
    private String errorMsg;

    public Task(String taskId) {
        this.taskId = taskId;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        this.startTime = dateFormat.format(date);
    }

    public void updateProgress(int percentage, String progress) {
        this.percentage = percentage;
        this.progress = progress;
    }
}
