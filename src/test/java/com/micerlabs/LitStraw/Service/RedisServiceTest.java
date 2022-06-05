package com.micerlabs.LitStraw.Service;

import com.micerlabs.LitStraw.Domain.*;
import com.micerlabs.LitStraw.ExtractApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExtractApplication.class)
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    public void testSaveTask() {
        Task task = new Task("86377b24-9c95-4f52-a9c3-e7253091bfa2");
        task.setCostTime(1111111);
        redisService.saveTask(task);
    }

    @Test
    public void testFindTask(){
        System.out.println(redisService.findTask("86377b24-9c95-4f52-a9c3-e7253091bfa2"));
    }

}
