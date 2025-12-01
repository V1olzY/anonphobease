package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.Language;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.*;

@Repository
public interface LanguageRepository extends MongoRepository<Language, ObjectId> {
    Optional<Language> findByCode(String code);
}
