package com.micerlabs.LitStraw.Dao;

import com.micerlabs.LitStraw.Domain.Task;
import org.springframework.data.repository.CrudRepository;

public interface RedisDao extends CrudRepository<Task, String> { }
