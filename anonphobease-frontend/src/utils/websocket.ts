import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";

let stompClient: Client | null = null;

export function connectWebSocket(userId: string, onConnected?: () => void) {
  if (stompClient && stompClient.active) return;

  const socket = new SockJS(`${process.env.VUE_APP_API_URL}/api/ws`);

  stompClient = new Client({
    webSocketFactory: () => socket,
    connectHeaders: { userId },
    debug: (str) => console.log(str),
    onConnect: () => {
      console.log("✅ WebSocket connected");
      if (onConnected) onConnected();
    },
    onStompError: (frame) => {
      console.error("❌ STOMP error", frame);
    },
  });

  stompClient.activate();
}

export function subscribeToChat(
  chatId: string,
  callback: (message: IMessage) => void
) {
  if (!stompClient || !stompClient.connected) return;

  stompClient.subscribe(`/topic/chat/${chatId}`, callback);
}

export function sendMessage(destination: string, body: any) {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination,
      body: JSON.stringify(body),
    });
  }
}

export function disconnectWebSocket() {
  if (stompClient && stompClient.active) {
    stompClient.deactivate();
    console.log("🔌 WebSocket disconnected");
  }
}
