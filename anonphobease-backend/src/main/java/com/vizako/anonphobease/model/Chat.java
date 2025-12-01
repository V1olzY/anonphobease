package com.vizako.anonphobease.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("chats")
public class Chat {
    @Id
    private ObjectId id;

    @NotNull
    @Indexed
    private ObjectId languageId;

    @NotNull
    @Indexed
    private ObjectId phobiaId;

    private String name;

    @CreatedDate
    private Date createdAt;
}
