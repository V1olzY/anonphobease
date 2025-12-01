<template>
  <div class="admin-page">
    <div class="header">
      <h2>{{ $t("phobias.page") }}</h2>
      <button @click="openModal()">{{ $t("common.add_new") }}</button>
    </div>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>{{ $t("phobias.name") }}</th>
          <th>{{ $t("phobias.description") }}</th>
          <th>{{ $t("common.actions") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="phobia in phobias"
          :key="phobia.id"
          @click="openModal(phobia)"
          class="clickable-row"
        >
          <td>{{ phobia.id }}</td>
          <td>{{ phobia.name }}</td>
          <td>{{ phobia.description }}</td>
          <td>
            <button @click.stop="deletePhobia(phobia.id)">❌</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <h3>{{ editingPhobia ? $t("common.edit") : $t("common.add") }}</h3>

        <label>{{ $t("phobias.name") }}</label>
        <input v-model="phobiaName" />

        <label>{{ $t("phobias.description") }}</label>
        <textarea v-model="phobiaDescription" rows="4" />

        <div class="modal-actions">
          <button @click="savePhobia">{{ $t("common.save") }}</button>
          <button @click="closeModal">{{ $t("common.cancel") }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "@/api/axiosInstance";
import type { Phobia } from "@/types/Phobia";

const phobias = ref<Phobia[]>([]);
const showModal = ref(false);
const editingPhobia = ref<Phobia | null>(null);
const phobiaName = ref<string>("");
const phobiaDescription = ref<string>("");

async function fetchPhobias() {
  const response = await axios.get<Phobia[]>("/v1/phobias");
  phobias.value = response.data;
}

function openModal(phobia: Phobia | null = null) {
  editingPhobia.value = phobia;
  phobiaName.value = phobia?.name ?? "";
  phobiaDescription.value = phobia?.description ?? "";
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  editingPhobia.value = null;
  phobiaName.value = "";
  phobiaDescription.value = "";
}

async function savePhobia() {
  if (editingPhobia.value) {
    await axios.put(`/v1/phobias/${editingPhobia.value.id}`, {
      name: phobiaName.value,
      description: phobiaDescription.value,
    });
  } else {
    await axios.post("/v1/phobias", {
      name: phobiaName.value,
      description: phobiaDescription.value,
    });
  }
  closeModal();
  await fetchPhobias();
}

async function deletePhobia(id: string) {
  await axios.delete(`/v1/phobias/${id}`);
  await fetchPhobias();
}

onMounted(fetchPhobias);
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
table {
  width: 100%;
  border-collapse: collapse;
}
th,
td {
  border: 1px solid #ccc;
  padding: 0.5rem;
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
