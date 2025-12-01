<template>
  <div class="bans-view">
    <h1>{{ $t("bans.page") }}</h1>

    <table class="bans-table">
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
          <td>{{ formatDate(ban.bannedAt) }}</td>
          <td>{{ ban.username || ban.userId }}</td>
          <td>{{ ban.moderatorName || ban.moderatorId }}</td>
          <td>{{ ban.banReason }}</td>
          <td>{{ ban.chatName || "-" }}</td>
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

.bans-table {
  width: 100%;
  border-collapse: collapse;
}

.bans-table th,
.bans-table td {
  border: 1px solid #ccc;
  padding: 0.5rem 0.75rem;
  text-align: left;
}

.bans-table th {
  background-color: #f4f4f4;
  font-weight: 600;
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
</style>
