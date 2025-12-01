package com.vizako.anonphobease.websocket;

import com.vizako.anonphobease.model.LogType;
import com.vizako.anonphobease.model.RelatedEntityType;
import com.vizako.anonphobease.service.*;
import com.vizako.anonphobease.util.JwtUtil;
import com.vizako.anonphobease.v1.dto.MessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ChatWebSocketHandlerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserLogService userLogService;

    @Mock
    private WebSocketSession session;
    @Mock
    private MessageFilterService messageFilterService;

    @Mock
    private ChatService chatService;

    @Mock
    private BanService banService;

    @Captor
    private ArgumentCaptor<TextMessage> textMessageCaptor;

    private ChatWebSocketHandler handler;


    @BeforeEach
    void setUp() {
        handler = new ChatWebSocketHandler(messageService, jwtUtil, userLogService, chatService, messageFilterService, banService);
    }


    @Test
    @DisplayName("afterConnectionEstablished closes session if chatId missing")
    void afterConnectionEstablished_NoChatId_ClosesSession() throws Exception {
        URI uri = new URI("ws://localhost/ws?token=abc");
        when(session.getUri()).thenReturn(uri);

        handler.afterConnectionEstablished(session);

        verify(session, times(1)).close(eq(CloseStatus.BAD_DATA));
        verifyNoInteractions(jwtUtil, userLogService, messageService);
        assertTrue(handler.getChatSessions().isEmpty());
    }

    @Test
    @DisplayName("afterConnectionEstablished adds session and logs when valid")
    void afterConnectionEstablished_Valid_AddsSessionAndLogs() throws Exception {
        String chatId = "chat123";
        String token = "jwt-token";
        String userId = "user-1";

        URI uri = new URI("ws://localhost/ws?chatId=" + chatId + "&token=" + token);
        when(session.getUri()).thenReturn(uri);
        when(session.getId()).thenReturn("session-1");

        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(jwtUtil.extractUserName(token)).thenReturn("Test User");

        handler.afterConnectionEstablished(session);

        verify(userLogService, times(1))
                .save(eq(userId), eq(LogType.CONNECTION_ESTABLISHED), eq(chatId), eq(RelatedEntityType.CHAT));

        assertTrue(handler.getChatSessions().containsKey(chatId));
        Set<WebSocketSession> sessions = handler.getChatSessions().get(chatId);
        assertEquals(1, sessions.size());
        assertTrue(sessions.contains(session));
    }

    @Test
    @DisplayName("handleTextMessage saves message and broadcasts to chat sessions")
    void handleTextMessage_SavesAndBroadcasts() throws Exception {
        String chatId = "chat123";
        String token = "jwt-token";
        String userId = "user-1";
        String username = "userName";
        String role = "USER";

        URI uri = new URI("ws://localhost/ws?chatId=" + chatId + "&token=" + token);
        when(session.getUri()).thenReturn(uri);
        when(session.isOpen()).thenReturn(true);

        handler.getChatSessions().put(chatId, Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>()));
        handler.getChatSessions().get(chatId).add(session);

        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.extractUserRole(token)).thenReturn(role);

        MessageDTO savedDto = new MessageDTO();
        savedDto.setMessageId("msg-1");
        savedDto.setChatId(chatId);
        savedDto.setUserId(userId);
        savedDto.setUsername("willOverwrite");
        when(messageService.save(any(MessageDTO.class))).thenReturn(savedDto);

        TextMessage incoming = new TextMessage("{\"content\":\"Hello\"}");

        handler.handleTextMessage(session, incoming);

        verify(messageService, times(1)).save(any(MessageDTO.class));

        verify(jwtUtil, times(1)).extractUserRole(token);

        verify(session, times(1)).sendMessage(textMessageCaptor.capture());
        String payload = textMessageCaptor.getValue().getPayload();

        assertTrue(payload.contains("\"chatId\":\"" + chatId + "\""));
        assertTrue(payload.contains("\"messageId\":\"msg-1\""));
        assertTrue(payload.contains("\"username\":\"" + username + "\""));
        assertTrue(payload.contains("\"role\":\"" + role + "\""));
    }



    @Test
    @DisplayName("afterConnectionClosed saves log and removes session")
    void afterConnectionClosed_RemovesSessionAndLogs() throws Exception {
        String chatId = "chat123";
        String token = "jwt-token";
        String userId = "user-1";

        URI uri = new URI("ws://localhost/ws?chatId=" + chatId + "&token=" + token);
        when(session.getUri()).thenReturn(uri);
        when(jwtUtil.extractUserId(token)).thenReturn(userId);

        handler.getChatSessions().put(chatId, Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>()));
        handler.getChatSessions().get(chatId).add(session);

        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(userLogService, times(1))
                .save(eq(userId), eq(LogType.CONNECTION_CLOSED), eq(chatId), eq(RelatedEntityType.CHAT));

        assertFalse(handler.getChatSessions().containsKey(chatId));
    }
}
