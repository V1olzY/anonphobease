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
@Document("languages")
public class Language {
    @Id
    private ObjectId id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 5)
    @Indexed(unique = true)
    private String code;
}
