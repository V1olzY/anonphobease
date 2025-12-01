package com.vizako.anonphobease.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("phobias")
public class Phobia {
    @Id
    private ObjectId id;

    @NotBlank
    @Size(max = 255)
    @Indexed(unique = true)
    private String name;

    @Size(max = 1000)
    private String description;
}
