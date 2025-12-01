package com.vizako.anonphobease.v1.dto;

import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLogDTO {
    private String id;
    private String userId;
    private String userName;
    private LogType logType;
    private Date createdAt;
    private String details;
    private String relatedEntityId;
    private RelatedEntityType relatedEntityType;
    private String relatedEntityName;
    private String relatedEntityExtra;
}
