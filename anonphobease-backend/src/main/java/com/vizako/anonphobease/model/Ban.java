package com.vizako.anonphobease.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("bans")
public class Ban {
    @Id
    private ObjectId id;

    @NotNull
    private ObjectId userId;

    @NotNull
    private ObjectId chatId;

    @NotNull
    private ObjectId messageId;

    @NotNull
    private ObjectId moderatorId;

    @NotBlank
    private String banReason;

    private Date bannedAt;

}
