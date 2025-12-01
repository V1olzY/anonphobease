package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.*;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByUsername(String username);
    List<User> findByIsActive(Boolean isActive);
    Optional<User> findByUsernameAndIsActiveTrue(String username);

}
