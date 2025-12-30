<template>
  <div class="logs-view">
    <h1>{{ $t("logs.page") }}</h1>

    <table class="table logs-table">
      <thead>
        <tr>
          <th>{{ $t("logs.time") }}</th>
          <th>{{ $t("logs.user_id") }}</th>
          <th>{{ $t("logs.details") }}</th>
          <th>{{ $t("logs.related_entity_name") }}</th>
          <th>{{ $t("logs.related_entity_extra") }}</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="log in logs" :key="log.id">
          <td :data-label="$t('logs.time')">
            <span class="cell-value">{{ formatDate(log.createdAt) }}</span>
          </td>

          <td :data-label="$t('logs.user_id')">
            <span class="cell-value">{{ log.userName }}</span>
          </td>

          <td :data-label="$t('logs.details')">
            <span class="cell-value">{{ log.details }}</span>
          </td>

          <td :data-label="$t('logs.related_entity_name')">
            <span class="cell-value">{{ log.relatedEntityName }}</span>
          </td>

          <td :data-label="$t('logs.related_entity_extra')">
            <span class="cell-value">{{ log.relatedEntityExtra }}</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "@/api/axiosInstance";
import { useRoute } from "vue-router";
import { UserLog } from "@/types/UserLog";
import { formatDate } from "@/utils/date";

const route = useRoute();

const logs = ref<UserLog[]>([]);

async function fetchReports() {
  const userId = route.params.id as string;
  try {
    const response = await axios.get(`/v1/logs/${userId}`);
    logs.value = response.data;
  } catch (error) {
    console.error("Failed to fetch reports:", error);
  }
}

onMounted(() => {
  fetchReports();
});
</script>
<style scoped lang="scss">
.clickable {
  color: #1976d2;
  cursor: pointer;
  text-decoration: #0056b3;
}

@media (max-width: 768px) {
  .logs-table thead {
    display: none;
  }

  .logs-table tr {
    display: block;
    margin-bottom: 12px;
    border: 1px solid var(--color-border);
    border-radius: 12px;
    background: var(--color-surface);
    padding: 8px 10px;
  }

  .logs-table td {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    border: 0;
    padding: 8px 0;
    align-items: flex-start;
    word-break: break-word;
  }

  .logs-table td::before {
    content: attr(data-label);
    font-weight: 700;
    color: var(--color-muted);
    min-width: 42%;
  }
}
</style>
