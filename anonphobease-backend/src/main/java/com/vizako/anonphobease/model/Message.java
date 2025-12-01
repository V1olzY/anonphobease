package com.vizako.anonphobease.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("messages")
public class Message {
    @Id
    private ObjectId id;

    @NotNull
    private ObjectId userId;

    @NotNull
    private ObjectId chatId;

    @NotNull
    @Size(max = 2000)
    private String content;

    @CreatedDate
    private Date createdAt;
}
