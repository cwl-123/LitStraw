package com.micerlabs.LitStraw.Service;

import com.micerlabs.LitStraw.Dao.RedisDao;
import com.micerlabs.LitStraw.Domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RedisService {
    @Autowired
    private RedisDao redisDao;

    // Add
    public void saveTask(Task task) {
        redisDao.save(task);
    }

    // delete
    public void deleteTask(Task task) {
        redisDao.delete(task);
    }

    // Find
    public Task findTask(String taskId) {
        return redisDao.findById(taskId).get();
    }
}
