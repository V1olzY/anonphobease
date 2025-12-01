package com.vizako.anonphobease.repository;

import com.vizako.anonphobease.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.*;

@Repository
public interface ReportRepository extends MongoRepository<Report, ObjectId> {
    List<Report> findByChatId(ObjectId chatId);
    List<Report> findByReportedUserId(ObjectId reportedUserId);
    List<Report> findByIsResolved(Boolean isResolved);
}
