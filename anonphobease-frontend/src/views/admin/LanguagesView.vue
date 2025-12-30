<template>
  <div class="admin-page">
    <div class="header">
      <h2>{{ $t("languages.page") }}</h2>
      <button @click="openModal()">{{ $t("common.add_new") }}</button>
    </div>

    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("languages.page") }}</th>
          <th>{{ $t("languages.code") }}</th>
          <th>{{ $t("common.actions") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="language in languages"
          :key="language.id"
          @click="openModal(language)"
          class="clickable-row"
        >
          <td>{{ language.name }}</td>
          <td>{{ language.code }}</td>
          <td>
            <button
              class="icon-button"
              @click.stop="deleteLanguage(language.id)"
            >
              ❌
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <h3>
          {{ editingLanguage ? $t("common.edit") : $t("common.add_new") }}
        </h3>

        <label>{{ $t("languages.name") }}</label>
        <input v-model="languageName" />
        <label>{{ $t("languages.code") }}</label>
        <input v-model="languageCode" />

        <div class="modal-actions">
          <button @click="saveLanguage">{{ $t("common.save") }}</button>
          <button @click="closeModal">{{ $t("common.cancel") }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "@/api/axiosInstance";
import type { Language } from "@/types/Language";

const languages = ref<Language[]>([]);
const showModal = ref(false);
const editingLanguage = ref<Language | null>(null);
const languageName = ref<string>("");
const languageCode = ref<string>("");

async function fetchLanguages() {
  const response = await axios.get<Language[]>("/v1/languages");
  languages.value = response.data;
}

function openModal(language: Language | null = null) {
  editingLanguage.value = language;
  languageName.value = language?.name ?? "";
  languageCode.value = language?.code ?? "";
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  editingLanguage.value = null;
  languageName.value = "";
  languageCode.value = "";
}

async function saveLanguage() {
  const payload = {
    name: languageName.value,
    code: languageCode.value,
  };

  if (editingLanguage.value) {
    await axios.put(`/v1/languages/${editingLanguage.value.id}`, payload);
  } else {
    await axios.post("/v1/languages", payload);
  }

  closeModal();
  await fetchLanguages();
}

async function deleteLanguage(id: string) {
  await axios.delete(`/v1/languages/${id}`);
  await fetchLanguages();
}

onMounted(fetchLanguages);
</script>

<style scoped>
.admin-page {
  padding: 2rem;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.clickable-row {
  cursor: pointer;
  transition: background 0.2s;
}
.clickable-row:hover {
  background-color: #eef;
}
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  min-width: 300px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: stretch;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}
</style>
