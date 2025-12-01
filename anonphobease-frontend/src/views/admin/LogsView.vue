<template>
  <div class="logs-view">
    <h1>{{ $t("logs.page") }}</h1>
    <table>
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
          <td>{{ formatDate(log.createdAt) }}</td>
          <td class="clickable" @click="goToUserLogs(log.userId)">
            {{ log.userName }}
          </td>
          <td>{{ log.details }}</td>
          <td>{{ log.relatedEntityName }}</td>
          <td>{{ log.relatedEntityExtra }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "@/api/axiosInstance";
import { format } from "date-fns";
import router from "@/router";
import { UserLog } from "@/types/UserLog";
import { formatDate } from "@/utils/date";

const logs = ref<UserLog[]>([]);

async function fetchReports() {
  try {
    const response = await axios.get("/v1/logs");
    logs.value = response.data;
  } catch (error) {
    console.error("Failed to fetch reports:", error);
  }
}
function goToUserLogs(userId: string) {
  router.push(`/logs/${userId}`);
}

onMounted(() => {
  fetchReports();
});
</script>
<style scoped lang="scss">
table {
  width: 100%;
  border-collapse: collapse;
  th,
  td {
    border: 1px solid #ccc;
    padding: 8px;
  }
  th {
    background: #f5f5f5;
  }
}
.clickable {
  color: #1976d2;
  cursor: pointer;
  text-decoration: #0056b3;
}
</style>
