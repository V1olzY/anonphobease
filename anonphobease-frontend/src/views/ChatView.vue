<template>
  <div class="chat-view">
    <div class="chat-box">
      <div class="chat-messages" ref="chatContainer">
        <div
          v-for="message in messages"
          :key="message.id"
          :class="[
            'chat-message',
            message.username === authStore.user?.username ? 'self' : 'other',
          ]"
        >
          <MessageCard
            :key="message.messageId"
            :username="message.username ?? 'Unknown'"
            :message="message.content ?? ''"
            :isSelf="message.username === authStore.user?.username"
            @report="() => handleReport(message)"
            :role="message.role"
          />
        </div>
      </div>
      <teleport to="body">
        <div v-if="showReportModal" class="modal-overlay">
          <div class="modal-content">
            <h3>📢 {{ $t("chat.report_message") }}</h3>
            <textarea
              v-model="reportReason"
              :placeholder="$t('chat.report_placeholder')"
              rows="4"
            ></textarea>
            <div class="modal-actions">
              <button class="cancel-button" @click="closeReportModal">
                {{ $t("common.cancel") }}
              </button>
              <button class="submit-button" @click="submitReport">
                {{ $t("chat.report") }}
              </button>
            </div>
          </div>
        </div>
      </teleport>
      <div class="chat-input">
        <div v-if="isBanned" class="chat-banned-notice">
          {{ $t("chat.bannedMessage") }}
        </div>

        <textarea
          v-model="newMessage"
          class="chat-input-field"
          :placeholder="isBanned ? '' : $t('chat.message_placeholder')"
          @input="autoResize"
          @keydown.ctrl.enter="sendMessage"
          :disabled="isBanned"
        />

        <button @click="sendMessage" class="send-button" :disabled="isBanned">
          {{ $t("chat.send") }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import { useRoute } from "vue-router";
import MessageCard from "@/components/chat/MessageCard.vue";
import { Message } from "@/types/Message";
import { useAuthStore } from "@/stores/authStore";
import axios from "@/api/axiosInstance";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const route = useRoute();
const chatId = route.params.id as string;
const messages = ref<Message[]>([]);
const newMessage = ref("");
const authStore = useAuthStore();
const chatContainer = ref<HTMLElement | null>(null);
const token = localStorage.getItem("token") || "";
let socket: WebSocket | null = null;
const showReportModal = ref(false);
const reportReason = ref("");
const reportMessageId = ref<string | null>(null);
const selectedMessage = ref<Message | null>(null);
const isBanned = ref(false);

function handleReport(message: Message) {
  selectedMessage.value = message;
  reportMessageId.value = message.messageId;
  showReportModal.value = true;
}

function closeReportModal() {
  showReportModal.value = false;
  reportMessageId.value = null;
}

async function submitReport() {
  if (!reportReason.value.trim()) {
    alert(t("chat.report_reason_required"));
    return;
  }

  try {
    await axios.post("/v1/reports", {
      messageId: reportMessageId.value,
      messageContent: selectedMessage.value?.content,
      reportedUserId: selectedMessage.value?.userId,
      chatId: chatId,
      reporterUserId: authStore.user?.id,
      reason: reportReason.value.trim(),
    });

    alert(t("chat.send_success"));
    closeReportModal();
  } catch (error) {
    alert(t("chat.send_error"));
  }
}

function sendMessage() {
  if (isBanned.value) {
    console.warn("❌ User is banned, cannot send message.");
    return;
  }

  const trimmed = newMessage.value.trim();
  if (trimmed && socket?.readyState === WebSocket.OPEN) {
    const msg = {
      username: authStore.user?.username ?? "Guest",
      content: trimmed,
      chatId,
      createdAt: new Date().toISOString(),
    };
    socket.send(JSON.stringify(msg));
    newMessage.value = "";
  } else {
    console.warn("❌ Message empty or WebSocket not open.");
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
    }
  });
}

function autoResize(event: Event) {
  const target = event.target as HTMLTextAreaElement;
  target.style.height = "auto";
  target.style.height = `${target.scrollHeight}px`;
  nextTick(() => {
    chatContainer.value?.scrollTo({ top: chatContainer.value.scrollHeight });
  });
}

onMounted(() => {
  const socketUrl = `${process.env.VUE_APP_SOCKET_URL}?chatId=${chatId}&token=${token}`;
  socket = new WebSocket(socketUrl);

  socket.onopen = () => {
    console.log("✅ WebSocket connected to", socketUrl);
  };

  socket.onmessage = (event) => {
    try {
      const incoming = JSON.parse(event.data);

      if (incoming.type === "error" && incoming.code === "USER_BANNED") {
        isBanned.value = true;
        console.warn("User is banned from sending messages.");
        return;
      }

      messages.value.push({
        messageId: incoming.messageId,
        userId: incoming.userId,
        username: incoming.username ?? "Unknown",
        chatId: incoming.chatId,
        content: incoming.content,
        createdAt: incoming.createdAt,
        role: incoming.role,
      });

      scrollToBottom();
    } catch (err) {
      console.error("❌ Error parsing message:", event.data);
    }
  };

  socket.onerror = (event) => {
    console.error("❌ WebSocket error:", event);
  };

  socket.onclose = (event) => {
    console.log("🔌 WebSocket closed:", event.reason);
  };
});

onUnmounted(() => {
  if (socket?.readyState === WebSocket.OPEN) {
    socket.close();
  }
  socket = null;
});
</script>
<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 12px;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  box-sizing: border-box;
}

.modal-content h3 {
  margin-bottom: 10px;
  font-size: 1.2rem;
}

.modal-content textarea {
  width: 100%;
  padding: 10px;
  font-size: 0.95rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  resize: none;
  box-sizing: border-box;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

.cancel-button,
.submit-button {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.cancel-button {
  background-color: #eee;
}

.submit-button {
  background-color: #4caf50;
  color: white;
}

.submit-button:hover {
  background-color: #45a049;
}

.chat-box {
  border: 1px solid #ccc;
  border-radius: 10px;
  width: 95%;
  margin: 25px auto;
  padding: 10px;
  display: flex;
  flex-direction: column;
  height: 80vh;
  box-sizing: border-box;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  scroll-behavior: smooth;
  box-sizing: border-box;
}

.chat-message {
  display: flex;
}

.chat-message.self {
  justify-content: flex-end;
}

.chat-message.other {
  justify-content: flex-start;
}

.chat-input {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.chat-input-field {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  resize: none;
  min-height: 2.5rem;
  max-height: 8rem;
  overflow-y: auto;
  line-height: 1.4;
}

.send-button {
  padding: 10px 20px;
  font-weight: bold;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.send-button:hover {
  background-color: #45a049;
}

.chat-banned-notice {
  width: 100%;
  border: 1px solid #e53935;
  background: rgba(229, 57, 53, 0.06);
  color: #b71c1c;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  margin-bottom: 6px;
}

@media (max-width: 480px) {
  .chat-box {
    padding: 6px;
    height: 90vh;
  }

  .chat-input {
    flex-direction: column;
  }

  .chat-input-field {
    width: 100%;
  }

  .send-button {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .chat-box {
    height: 90vh;
    padding: 6px;
    border-radius: 6px;
  }

  .chat-input {
    flex-direction: column;
    align-items: stretch;
  }

  .send-button {
    width: 100%;
    font-size: 1rem;
  }

  .chat-input-field {
    font-size: 1rem;
  }

  .message-card {
    max-width: 90%;
  }
}
</style>
