package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.*;

@Repository
public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    List<Message> findByChatId(ObjectId chatId);
    List<Message> findByUserId(ObjectId userId);
}
