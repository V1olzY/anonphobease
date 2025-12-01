package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.ChatUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.*;

@Repository
public interface ChatUserRepository extends MongoRepository<ChatUser, ObjectId> {
    List<ChatUser> findByChatId(ObjectId chatId);
    List<ChatUser> findByUserId(ObjectId userId);
}
