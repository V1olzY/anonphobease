package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.Ban;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.*;

@Repository
public interface BanRepository extends MongoRepository<Ban, ObjectId> {
    List<Ban> findByUserId(ObjectId userId);
    List<Ban> findByChatId(ObjectId chatId);
}
