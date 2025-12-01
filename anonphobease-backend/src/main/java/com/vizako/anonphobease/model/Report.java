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
@Document("reports")
public class Report {
    @Id
    private ObjectId id;

    @NotNull
    private ObjectId chatId;

    @NotNull
    private ObjectId reportedUserId;

    @NotNull
    private ObjectId reporterUserId;
    @NotNull
    private ObjectId messageId;

    @NotNull
    @Size(max = 2000)
    private String reason;

    private Boolean isResolved = false;

    @CreatedDate
    private Date createdAt;

    private Date resolvedAt;

    private ObjectId moderatorId;
    private ActionType actionTaken;

    @Size(max = 2000)
    private String actionReason;
}
