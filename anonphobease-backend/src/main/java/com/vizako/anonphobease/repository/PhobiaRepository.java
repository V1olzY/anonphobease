package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.Phobia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.*;

@Repository
public interface PhobiaRepository extends MongoRepository<Phobia, ObjectId> {
    Optional<Phobia> findByName(String name);
}
