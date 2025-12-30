<template>
  <div class="reports-view">
    <div class="header-row">
      <h1>{{ $t("reports.page") }}</h1>

      <div class="filter-container">
        <label for="report-filter" class="filter-label">Filter:</label>
        <select id="report-filter" v-model="filter">
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
    </div>

    <div class="table-responsive">
      <table class="table reports-table">
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
            <td :data-label="$t('reports.created_at')">
              {{ formatDate(report.createdAt) }}
            </td>

            <td :data-label="$t('reports.chat_name')">
              {{ report.chatName }}
            </td>

            <td :data-label="$t('reports.reported_user')">
              {{ report.reportedUsername }}
            </td>

            <td :data-label="$t('reports.reporter')">
              {{ report.reporterUsername }}
            </td>

            <td :data-label="$t('reports.reason')">
              {{ report.reason }}
            </td>

            <td :data-label="$t('reports.is_resolved')">
              <span v-if="report.isResolved" class="resolved-icon">✔</span>
              <span v-else class="unresolved-icon">✘</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

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
  if (filter.value === "resolved")
    return reports.value.filter((r) => r.isResolved);
  if (filter.value === "unresolved")
    return reports.value.filter((r) => !r.isResolved);
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
  if (idx !== -1) {
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
.reports-view {
  padding: 2rem;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.filter-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-label {
  font-weight: 600;
  color: var(--color-text);
}

.table-responsive {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.resolved-icon {
  color: var(--status-success-text);
  font-size: 1.2rem;
  font-weight: 700;
}

.unresolved-icon {
  color: var(--status-danger-text);
  font-size: 1.2rem;
  font-weight: 700;
}

.clickable {
  cursor: pointer;
}
.clickable:hover {
  background: var(--color-muted-bg);
}

@media (max-width: 768px) {
  .header-row {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-container {
    justify-content: flex-end;
  }

  .reports-table thead {
    display: none;
  }

  .reports-table tr {
    display: block;
    margin-bottom: 12px;
    border: 1px solid var(--color-border);
    border-radius: 12px;
    background: var(--color-surface);
    padding: 8px 10px;
  }

  .reports-table td {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    border: 0;
    padding: 8px 0;
    align-items: flex-start;
    word-break: break-word;
  }

  .reports-table td::before {
    content: attr(data-label);
    font-weight: 700;
    color: var(--color-muted);
    min-width: 42%;
  }
}
</style>
