package com.vizako.anonphobease.helper;


import com.vizako.anonphobease.model.RelatedEntityInfo;
import com.vizako.anonphobease.model.UserLog;
import com.vizako.anonphobease.service.*;
import com.vizako.anonphobease.v1.dto.ChatDTO;
import com.vizako.anonphobease.v1.dto.LanguageDTO;
import com.vizako.anonphobease.v1.dto.PhobiaDTO;
import com.vizako.anonphobease.v1.dto.ReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelatedEntityHelperService {
    private final UserService userService;
    private final ChatService chatService;
    private final PhobiaService phobiaService;
    private final LanguageService languageService;
    private final ReportService reportService;

    public RelatedEntityInfo getRelatedEntityInfo(UserLog userLog) {
        String relatedEntityName = null;
        String relatedEntityExtra = null;
        switch (userLog.getRelatedEntityType()) {
            case USER:
                relatedEntityName = userService.getUserNameById(userLog.getUserId().toString());
                break;
            case CHAT:
                ChatDTO chat = chatService.findById(userLog.getRelatedEntityId()).orElse(null);
                if (chat != null) {
                    relatedEntityName = chat.getChatName();
                    relatedEntityExtra = chat.getLanguageId() != null ? chat.getLanguageId() : null;
                }
                break;
            case MESSAGE:
                break;
            case REPORT:
                ReportDTO report = reportService.findById(userLog.getRelatedEntityId().toString()).orElse(null);
                if (report != null) {
                    relatedEntityName = report.getReason();
                }
                break;
            case PHOBIA:
                PhobiaDTO phobia = phobiaService.findById(userLog.getRelatedEntityId().toString()).orElse(null);
                if (phobia != null) {
                    relatedEntityName = phobia.getName();
                }
                break;
            case LANGUAGE:
                LanguageDTO language = languageService.findById(userLog.getRelatedEntityId().toString()).orElse(null);
                if (language != null) {
                    relatedEntityName = language.getName();
                }
                break;
            default:
                break;
        }
        return new RelatedEntityInfo(relatedEntityName, relatedEntityExtra);
    }
}
