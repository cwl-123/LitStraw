package com.micerlabs.LitStraw.Dao;

import com.micerlabs.LitStraw.Domain.Literature;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MongoDao extends MongoRepository<Literature, String> {
    public Literature findByTitle(String title);
}
