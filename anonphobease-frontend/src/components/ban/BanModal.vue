<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <button class="close-btn" @click="$emit('close')">×</button>

      <h2 class="modal-title">{{ $t("bans.details") }}</h2>

      <div class="details-grid">
        <div>
          <strong>{{ $t("bans.banned_user") }}:</strong>
          <span>{{ ban.username || ban.userId }}</span>
        </div>

        <div>
          <strong>{{ $t("bans.moderator") }}:</strong>
          <span>{{ ban.moderatorName || ban.moderatorId }}</span>
        </div>

        <div>
          <strong>{{ $t("bans.chat_id") }}:</strong>
          <span>{{ ban.chatId || "-" }}</span>
        </div>

        <div>
          <strong>{{ $t("bans.banned_at") }}:</strong>
          <span>{{ formatDate(ban.bannedAt) }}</span>
        </div>

        <div class="full-row">
          <strong>{{ $t("bans.message") }}:</strong>
          <span>{{ ban.messageContent || "-" }}</span>
        </div>

        <div class="full-row">
          <strong>{{ $t("bans.reason") }}:</strong>
          <span>{{ ban.banReason }}</span>
        </div>
      </div>

      <div class="actions">
        <button class="btn-secondary" @click="$emit('close')">
          {{ $t("common.cancel") }}
        </button>
        <button class="btn-danger" @click="$emit('unban')">
          {{ $t("bans.unban_button") }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatDate } from "@/utils/date";
import type { Ban } from "@/types/Ban";

defineProps<{
  ban: Ban;
}>();

defineEmits<{
  (e: "close"): void;
  (e: "unban"): void;
}>();
</script>

<style scoped lang="scss">
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  position: relative;
  background: #fff;
  padding: 1.5rem 1.75rem 1.25rem;
  border-radius: 10px;
  max-width: 650px;
  width: 100%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
}

.modal-title {
  margin: 0 0 1rem;
}

.close-btn {
  position: absolute;
  top: 0.5rem;
  right: 0.75rem;
  border: none;
  background: transparent;
  font-size: 1.4rem;
  cursor: pointer;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.6rem 1.2rem;
  margin-bottom: 1.25rem;
}

.details-grid div {
  display: flex;
  flex-direction: column;
  font-size: 0.95rem;
}

.full-row {
  grid-column: 1 / -1;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn-secondary,
.btn-danger {
  padding: 0.4rem 0.9rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
}

.btn-secondary {
  background: #e0e0e0;
}

.btn-danger {
  background: var(--color-danger);
  color: #fff;
}
</style>
