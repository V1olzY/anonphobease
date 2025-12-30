<template>
  <div :class="['message-card', isSelf ? 'self' : 'other']">
    <div class="message-header">
      <span :class="['username', getRoleClass(role)]">{{ username }}</span>
      <button
        v-if="!isSelf && role === 'USER'"
        class="report-button btn--icon"
        @click="onReport"
      >
        🚩
      </button>
    </div>
    <div class="message-content">
      {{ message }}
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  username: string;
  message: string;
  messageId?: string;
  isSelf?: boolean;
  onReport?: () => void;
  role?: "USER" | "MODERATOR" | "ADMIN";
}>();

const getRoleClass = (role?: string) => {
  console.log(role);
  if (role === "ADMIN") return "admin";
  if (role === "MODERATOR") return "moderator";
  return "user";
};
</script>

<style scoped>
.message-card {
  background-color: var(--bg-color);
  border-radius: 10px;
  padding: 10px;
  max-width: 70%;
  display: inline-block;
  word-break: break-word;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  gap: 10px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.username.admin {
  color: var(--color-danger);
}
.username.moderator {
  color: var(--color-primary);
}
.username.user {
  color: var(--color-text);
}

.username {
  font-weight: bold;
  flex-grow: 1;
}

.report-button {
  white-space: nowrap;
  padding: 2px 8px;
  font-size: 0.8rem;
  border: none;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  flex-shrink: 0;
}

.report-button:hover {
  background-color: var(--color-muted-bg);
}

.message-content {
  font-size: 0.95rem;
}

.self {
  --bg-color: #e3f2fd;
  border-color: #9ddfaa;
}

.other {
  --bg-color: #f0f0f0;
  border-color: #d3d3d3;
}
</style>
