<template>
  <div class="reports-view">
    <h1>{{ $t("reports.page") }}</h1>
    <div class="filter-container">
      <label for="report-filter" class="filter-label">Filter:</label>
      <select id="report-filter" v-model="filter" class="filter-select">
        <option value="unresolved">
          {{ $t("reports.filter_unresolved") }}
        </option>
        <option value="resolved">
          {{ $t("reports.filter_resolved") }}
        </option>
        <option value="all">
          {{ $t("reports.filter_all") }}
        </option>
      </select>
    </div>
    <table class="reports-table">
      <thead>
        <tr>
          <th>{{ $t("reports.created_at") }}</th>
          <th>{{ $t("reports.chat_name") }}</th>
          <th>{{ $t("reports.reported_user") }}</th>
          <th>{{ $t("reports.reporter") }}</th>
          <th>{{ $t("reports.reason") }}</th>
          <th>{{ $t("reports.is_resolved") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="report in filteredReports"
          :key="report.id"
          @click="openModal(report)"
          :class="{ clickable: !report.isResolved }"
        >
          <td>{{ formatDate(report.createdAt) }}</td>
          <td>{{ report.chatName }}</td>
          <td>{{ report.reportedUsername }}</td>
          <td>{{ report.reporterUsername }}</td>
          <td>{{ report.reason }}</td>
          <td>
            <span v-if="report.isResolved" class="resolved-icon">✔</span>
            <span v-else class="unresolved-icon">✘</span>
          </td>
        </tr>
      </tbody>
    </table>
    <ReportModal
      v-if="showModal"
      :report="selectedReport"
      :initialActionReason="selectedReport?.actionReason || ''"
      @ban="handleBan"
      @close="closeModal"
      @noViolation="noViolation"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, Ref, computed } from "vue";
import axios from "@/api/axiosInstance";
import { formatDate } from "@/utils/date";
import ReportModal from "@/components/report/ReportModal.vue";
import type { Report } from "@/types/Report";
import { ReportActionRequest } from "@/types/ReportActionRequest";
import { useAuthStore } from "@/stores/authStore";

const reports: Ref<Report[]> = ref([]);
const showModal = ref(false);
const selectedReport = ref<Report | null>(null);
const authStore = useAuthStore();
const filter = ref<"all" | "resolved" | "unresolved">("unresolved");
const filteredReports = computed(() => {
  if (filter.value === "resolved") {
    return reports.value.filter((r) => r.isResolved);
  }
  if (filter.value === "unresolved") {
    return reports.value.filter((r) => !r.isResolved);
  }
  return reports.value;
});
async function fetchReports() {
  try {
    const response = await axios.get("/v1/reports");
    reports.value = response.data;
  } catch (error) {
    console.error("Failed to fetch reports:", error);
  }
}

function openModal(report: Report) {
  selectedReport.value = report;
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  selectedReport.value = null;
}

function updateReportAfterAction(responseData: any) {
  if (!selectedReport.value) return;
  selectedReport.value.isResolved = true;
  selectedReport.value.actionReason = responseData.actionReason;
  selectedReport.value.moderatorId = responseData.moderatorId;
  selectedReport.value.actionTaken = responseData.actionTaken;
  selectedReport.value.resolvedAt = responseData.resolvedAt;
  const idx = reports.value.findIndex((r) => r.id === selectedReport.value?.id);
  if (idx !== -1 && selectedReport.value) {
    reports.value[idx] = { ...selectedReport.value };
  }
}

async function handleBan(payload: { actionReason: string }) {
  if (!selectedReport.value) return;
  try {
    const reportActionRequest: ReportActionRequest = {
      actionReason: payload.actionReason,
      moderatorId: authStore.user?.id || "",
    };

    const response = await axios.post(
      `/v1/reports/${selectedReport.value.id}/ban`,
      reportActionRequest
    );
    updateReportAfterAction(response.data);
    closeModal();
  } catch (error) {
    console.error("Failed to ban user:", error);
  }
}
async function noViolation(payload: { actionReason: string }) {
  if (!selectedReport.value) return;
  try {
    const reportActionRequest: ReportActionRequest = {
      actionReason: payload.actionReason,
      moderatorId: authStore.user?.id || "",
    };
    const response = await axios.post(
      `/v1/reports/${selectedReport.value.id}/no-violation`,
      reportActionRequest
    );
    updateReportAfterAction(response.data);
    closeModal();
  } catch (error) {
    console.error("Failed to mark no violation:", error);
  }
}

onMounted(fetchReports);
</script>

<style scoped>
.reports-table {
  width: 100%;
  border-collapse: collapse;
}

.reports-table th,
.reports-table td {
  border: 1px solid #ccc;
  padding: 0.5rem;
  text-align: left;
}

.reports-table th {
  background-color: #f4f4f4;
}

button {
  padding: 0.3rem 0.6rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.resolved-icon {
  color: green;
  font-size: 1.2rem;
}

.unresolved-icon {
  color: red;
  font-size: 1.2rem;
}
.clickable {
  cursor: pointer;
  background-color: #f9f9f9;
}
.clickable:hover {
  background-color: #e6f0ff;
}
.filter-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
  margin-right: 1rem;
  justify-content: flex-end;
}

.filter-label {
  font-weight: 500;
  color: #333;
}

.filter-select {
  padding: 0.4rem 1.2rem 0.4rem 0.6rem;
  border: 1px solid #b0b0b0;
  border-radius: 6px;
  background: #f8fafd;
  color: #222;
  font-size: 1rem;
  transition: border 0.2s;
}

.filter-select:focus {
  border-color: #007bff;
  outline: none;
  background: #fff;
}
</style>
