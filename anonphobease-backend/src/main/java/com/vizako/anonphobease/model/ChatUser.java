package com.vizako.anonphobease.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("chat_users")
public class ChatUser {
    @Id
    private ObjectId id;

    @NotNull
    @Indexed
    private ObjectId chatId;

    @NotNull
    @Indexed
    private ObjectId userId;

    @NotNull
    private Date joinedAt;

    private Date leftAt;
}
