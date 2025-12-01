package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.*;

@Repository
public interface ChatRepository extends MongoRepository<Chat, ObjectId> {
    List<Chat> findByLanguageId(ObjectId languageId);
    List<Chat> findByPhobiaId(ObjectId phobiaId);
}
