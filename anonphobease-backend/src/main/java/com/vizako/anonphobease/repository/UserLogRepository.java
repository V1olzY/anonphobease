package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.UserLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.*;

@Repository
public interface UserLogRepository extends MongoRepository<UserLog, ObjectId> {
    List<UserLog> findByUserId(ObjectId userId);
}
