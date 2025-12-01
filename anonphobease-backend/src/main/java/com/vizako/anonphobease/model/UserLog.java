package com.vizako.anonphobease.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("user_logs")
public class UserLog {
    @Id
    private ObjectId id;

    @NotNull
    private ObjectId userId;


    private LogType logType;

    @CreatedDate
    private Date createdAt;

    @Size(max = 2000)
    private String details;

    private ObjectId relatedEntityId;

    private RelatedEntityType relatedEntityType;

}
