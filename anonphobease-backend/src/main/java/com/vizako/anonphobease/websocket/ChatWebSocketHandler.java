package com.vizako.anonphobease.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.Message;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.service.*;
import com.vizako.anonphobease.v1.dto.MessageDTO;
import com.vizako.anonphobease.v1.dto.UserLogDTO;
import com.vizako.anonphobease.v1.mapper.MessageMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.vizako.anonphobease.util.JwtUtil;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@Data
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, Set<WebSocketSession>> chatSessions = new ConcurrentHashMap<>();

    private final MessageService messageService;
    private final JwtUtil jwtUtil;
    private final UserLogService userLogService;
    private final ChatService chatService;
    private final MessageFilterService messageFilterService;
    private final BanService banService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("🔧 New connection: " + session.getUri());

        String chatId = getChatId(session);
        if (chatId == null) {
            System.out.println("❌ No chatId found. Closing.");
            closeSession(session, CloseStatus.BAD_DATA);
            return;
        }
        String token = getTokenFromQuery(session.getUri().getQuery());
        String userId = jwtUtil.extractUserId(token);
        String userName = jwtUtil.extractUserName(token);
        userLogService.save( userId, LogType.CONNECTION_ESTABLISHED, chatId, RelatedEntityType.CHAT);


        chatSessions
                .computeIfAbsent(chatId, key -> ConcurrentHashMap.newKeySet())
                .add(session);

        System.out.println("🔌 Connected: " + session.getId() + " to chat " + chatId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String chatId = getChatId(session);
        if (chatId == null) return;

        String token = getTokenFromQuery(session.getUri().getQuery());
        String userId = jwtUtil.extractUserId(token);
        String username = jwtUtil.extractUsername(token);

        if (banService != null && banService.isUserGloballyBanned(new ObjectId(userId))) {
            ObjectMapper om = new ObjectMapper();
            ObjectNode error = om.createObjectNode();
            error.put("type", "error");
            error.put("code", "USER_BANNED");
            error.put("message", "You are banned from the platform."); // можно оставить как debug

            session.sendMessage(new TextMessage(error.toString()));
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = (ObjectNode) objectMapper.readTree(message.getPayload());

        json.put("userId", userId);
        json.put("username", username);
        json.put("chatId", chatId);
        json.put("createdAt", new Date().getTime());
        json.put("messageId", new ObjectId().toString());


        MessageDTO dto = objectMapper.treeToValue(json, MessageDTO.class);

        String languageCode = chatService.getLanguageCodeByChatId(chatId);
        System.out.println("[WS] chatId=" + chatId + ", languageCode=" + languageCode);

        String originalText = dto.getContent();
        String filteredText = messageFilterService.filter(originalText, languageCode);

        MessageDTO saved = messageService.save(dto);
        saved.setRole(jwtUtil.extractUserRole(token));
        saved.setUsername(username);
        saved.setContent(filteredText);

        String response = objectMapper.writeValueAsString(saved);
        Set<WebSocketSession> sessions = chatSessions.get(chatId);
        if (sessions != null) {
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(response));
                }
            }
        }

        System.out.println("✅ Message saved and broadcasted: " + response);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String chatId = getChatId(session);
        if (chatId == null) return;

        String token = getTokenFromQuery(session.getUri().getQuery());
        String userId = jwtUtil.extractUserId(token);

        userLogService.save(userId, LogType.CONNECTION_CLOSED, chatId, RelatedEntityType.CHAT);

        Set<WebSocketSession> sessions = chatSessions.get(chatId);
        if (sessions != null) {
            sessions.remove(session);
            System.out.println("❌ Disconnected: " + session.getId() + " from chat " + chatId);
            if (sessions.isEmpty()) {
                chatSessions.remove(chatId);
            }
        }
    }

    private String getChatId(WebSocketSession session) {
        return Optional.ofNullable(session.getUri())
                .map(uri -> {
                    String[] params = uri.getQuery().split("&");
                    for (String param : params) {
                        if (param.startsWith("chatId=")) {
                            return param.substring("chatId=".length());
                        }
                    }
                    return null;
                })
                .orElse(null);
    }

    private void closeSession(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (IOException e) {
            System.err.println("Error closing session: " + e.getMessage());
        }
    }

    private String getTokenFromQuery(String query) {
        if (query == null) return null;
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring("token=".length());
            }
        }
        return null;
    }



}
