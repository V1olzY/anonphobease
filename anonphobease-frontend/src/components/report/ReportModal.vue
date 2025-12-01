<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <button class="close-btn" @click="$emit('close')">×</button>

      <div class="header-row">
        <h2>{{ $t("reports.details_title") }}</h2>
        <span :class="['badge', report.isResolved ? 'resolved' : 'unresolved']">
          {{
            report.isResolved
              ? $t("reports.status_resolved")
              : $t("reports.status_unresolved")
          }}
        </span>
      </div>

      <div class="details-grid">
        <div>
          <strong>{{ $t("reports.report_id") }}:</strong>
          <span>{{ report.id }}</span>
        </div>
        <div>
          <strong>{{ $t("reports.chat_name") }}:</strong>
          <span>{{ report.chatName }}</span>
        </div>
        <div>
          <strong>{{ $t("reports.reported_username") }}:</strong>
          <span>{{ report.reportedUsername }}</span>
        </div>
        <div>
          <strong>{{ $t("reports.reporter_username") }}:</strong>
          <span>{{ report.reporterUsername }}</span>
        </div>
        <div>
          <strong>{{ $t("reports.message_content") }}:</strong>
          <span>{{ report.messageContent }}</span>
        </div>
        <div>
          <strong>{{ $t("reports.reason") }}:</strong>
          <span>{{ report.reason }}</span>
        </div>
        <div>
          <strong>{{ $t("reports.created_at") }}:</strong>
          <span>{{ formatDate(report.createdAt) }}</span>
        </div>
        <div>
          <strong>{{ $t("reports.resolved_at") }}:</strong>
          <span>{{ formatDate(report.resolvedAt) }}</span>
        </div>
      </div>

      <div class="action-section">
        <label
          ><strong>{{ $t("reports.action_type") }}</strong></label
        >
        <span :class="actionClass">{{ actionLabel }}</span>

        <label for="actionReason" style="margin-top: 0.7rem">
          <strong>{{ $t("reports.decision_justification") }}</strong>
        </label>
        <textarea id="actionReason" v-model="actionReason" rows="3" />
      </div>

      <div class="actions">
        <button @click="banUser">{{ $t("reports.action_ban") }}</button>
        <button @click="noViolation">
          {{ $t("reports.action_no_violation") }}
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from "vue";
import type { Report } from "@/types/Report";
import { formatDate } from "@/utils/date";
import { ActionType } from "@/types/ActionType";

const props = defineProps<{ report: Report; initialActionReason: string }>();
const actionReason = ref(props.initialActionReason);
console.log(props.report);
const emit = defineEmits(["ban", "close", "noViolation"]);

const actionClass = computed(() => {
  switch (props.report.actionTaken) {
    case ActionType.PENDING:
      return "pending";
    case ActionType.BANNED:
      return "banned";
    case ActionType.NOT_BANNED:
      return "no-violation";
    default:
      return "";
  }
});

const actionLabel = computed(() => {
  switch (props.report.actionTaken) {
    case ActionType.PENDING:
      return "PENDING";
    case ActionType.BANNED:
      return "BANNED";
    case ActionType.NOT_BANNED:
      return "NO VIOLATION";
    default:
      return props.report.actionTaken;
  }
});
function banUser() {
  emit("ban", { actionReason: actionReason.value });
}
function noViolation() {
  emit("noViolation", { actionReason: actionReason.value });
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  position: relative;
  background: #fff;
  padding: 2rem 2.5rem 2rem 2.5rem;
  border-radius: 10px;
  min-width: 350px;
  max-width: 500px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}
.close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.7rem;
  cursor: pointer;
  color: #888;
}
.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.7rem 1.5rem;
  margin-bottom: 1.5rem;
}
.details-grid div {
  display: flex;
  flex-direction: column;
  font-size: 0.98rem;
}
.action-section {
  margin-bottom: 1.2rem;
  display: flex;
  flex-direction: column;
}
.action-section label {
  margin-bottom: 0.3rem;
}
.actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}
.actions button {
  padding: 0.5rem 1.2rem;
  border-radius: 5px;
  border: none;
  background: #1976d2;
  color: #fff;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.actions button:last-child {
  background: #bdbdbd;
  color: #333;
}
.actions button:hover {
  background: #1565c0;
}
.actions button:last-child:hover {
  background: #9e9e9e;
}
.badge {
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.resolved {
  background: #c8e6c9;
  color: #388e3c;
}
.unresolved {
  background: #ffcdd2;
  color: #d32f2f;
}

.pending {
  background: #fff3cd;
  color: #856404;
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-weight: 600;
}
.banned {
  background: #ffcdd2;
  color: #d32f2f;
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-weight: 600;
}
.no-violation {
  background: #bbdefb;
  color: #1976d2;
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-weight: 600;
}
</style>
