<template>
  <div class="bans-view">
    <h1>{{ $t("bans.page") }}</h1>

    <table class="table bans-table">
      <thead>
        <tr>
          <th>{{ $t("bans.banned_at") }}</th>
          <th>{{ $t("bans.banned_user") }}</th>
          <th>{{ $t("bans.moderator") }}</th>
          <th>{{ $t("bans.reason") }}</th>
          <th>{{ $t("bans.chat_name") }}</th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="ban in bans"
          :key="ban.id"
          class="clickable-row"
          @click="openModal(ban)"
        >
          <td :data-label="$t('bans.banned_at')">
            <span class="cell-value">{{ formatDate(ban.bannedAt) }}</span>
          </td>

          <td :data-label="$t('bans.banned_user')">
            <span class="cell-value">{{ ban.username || ban.userId }}</span>
          </td>

          <td :data-label="$t('bans.moderator')">
            <span class="cell-value">{{
              ban.moderatorName || ban.moderatorId
            }}</span>
          </td>

          <td :data-label="$t('bans.reason')">
            <span class="cell-value">{{ ban.banReason }}</span>
          </td>

          <td :data-label="$t('bans.chat_name')">
            <span class="cell-value">{{ ban.chatName || "-" }}</span>
          </td>
        </tr>

        <tr v-if="!bans.length">
          <td colspan="5" class="empty-cell">
            {{ $t("bans.empty") }}
          </td>
        </tr>
      </tbody>
    </table>
    <BanModal
      v-if="showModal && selectedBan"
      :ban="selectedBan"
      @close="closeModal"
      @unban="unbanSelected"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, type Ref } from "vue";
import axios from "@/api/axiosInstance";
import { formatDate } from "@/utils/date";
import type { Ban } from "@/types/Ban";
import BanModal from "@/components/ban/BanModal.vue";

const bans: Ref<Ban[]> = ref([]);
const showModal = ref(false);
const selectedBan = ref<Ban | null>(null);

async function fetchBans() {
  try {
    const response = await axios.get<Ban[]>("/v1/bans");
    bans.value = response.data;
  } catch (error) {
    console.error("Failed to fetch bans:", error);
  }
}

function openModal(ban: Ban) {
  selectedBan.value = ban;
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
}

async function unbanSelected() {
  if (!selectedBan.value) return;

  try {
    await axios.delete(`/v1/bans/${selectedBan.value.id}`);
    bans.value = bans.value.filter((b) => b.id !== selectedBan.value?.id);
    closeModal();
  } catch (error) {
    console.error("Failed to unban user:", error);
  }
}

onMounted(fetchBans);
</script>

<style scoped lang="scss">
.bans-view {
  padding: 1.5rem;
}

.clickable-row {
  cursor: pointer;
}

.clickable-row:hover {
  background-color: #f5f5f5;
}

.empty-cell {
  text-align: center;
  padding: 1rem;
  font-style: italic;
}

@media (max-width: 768px) {
  .bans-table thead {
    display: none;
  }

  .bans-table tr {
    display: block;
    margin-bottom: 12px;
    border: 1px solid var(--color-border);
    border-radius: 12px;
    background: var(--color-surface);
    padding: 8px 10px;
  }

  .bans-table td {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    border: 0;
    padding: 8px 0;
    align-items: flex-start;
    word-break: break-word;
  }

  .bans-table td::before {
    content: attr(data-label);
    font-weight: 700;
    color: var(--color-muted);
    min-width: 42%;
  }
}
</style>
