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
    console.log("✅ WebSocket connected");
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
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: var(--color-surface);
  padding: 20px;
  border-radius: 12px;
  max-width: 420px;
  width: 92%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.18);
  box-sizing: border-box;
}

.modal-content h3 {
  margin: 0 0 10px 0;
  font-size: 1.2rem;
}

.modal-content textarea {
  width: 100%;
  padding: 10px;
  font-size: 0.95rem;
  border-radius: 8px;
  border: 1px solid var(--color-border);
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
  padding: 8px 14px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

.cancel-button {
  background: var(--btn-neutral-bg);
  color: var(--btn-neutral-text);
}

.cancel-button:hover {
  background: var(--btn-neutral-bg-hover);
}

.submit-button {
  background: var(--btn-primary-bg);
  color: var(--btn-primary-text);
}

.submit-button:hover {
  background: var(--btn-primary-bg-hover);
}

.chat-view {
  width: 100%;
}

.chat-box {
  border: 1px solid var(--color-border);
  border-radius: 12px;
  background: var(--color-surface);

  width: min(95%, 900px);
  margin: 16px auto;
  padding: 10px;

  display: flex;
  flex-direction: column;

  height: calc(100dvh - 110px);
  box-sizing: border-box;
}

.chat-messages {
  flex: 1 1 auto;
  min-height: 0;
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
  flex: 0 0 auto;

  position: sticky;
  bottom: 0;

  background: var(--color-surface);
  border-top: 1px solid var(--color-border);

  display: flex;
  gap: 10px;
  padding-top: 10px;
  margin-top: 10px;
  box-sizing: border-box;
}

.chat-input-field {
  flex: 1;

  padding: 10px;
  border: 1px solid var(--color-border);
  border-radius: 10px;
  font-size: 1rem;
  line-height: 1.4;

  resize: none;

  min-height: 48px;
  max-height: 140px;
  overflow-y: auto;

  box-sizing: border-box;
}

.send-button {
  flex: 0 0 auto;
  padding: 10px 18px;

  font-weight: 700;
  background: var(--color-primary);
  color: #fff;

  border: none;
  border-radius: 10px;
  cursor: pointer;
}

.send-button:hover {
  background: var(--color-primary-hover);
}

.send-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.chat-banned-notice {
  width: 100%;
  border: 1px solid var(--status-danger-text);
  background: var(--status-danger-bg);
  color: var(--status-danger-text);
  padding: 8px 12px;
  border-radius: 10px;
  font-size: 0.9rem;
  margin-bottom: 6px;
  box-sizing: border-box;
}

@media (max-width: 768px) {
  .chat-box {
    width: 100%;
    margin: 0;
    border-radius: 0;
    height: calc(100dvh - 80px);
  }
}

@media (max-width: 480px) {
  .chat-input {
    flex-direction: column;
    align-items: stretch;
    padding-bottom: calc(10px + env(safe-area-inset-bottom));
  }

  .send-button {
    width: 100%;
  }
}
</style>
