<template>
  <div class="roles-page">
    <div class="header">
      <h2>{{ $t("roles.page") }}</h2>
      <button @click="openModal(null)">{{ $t("common.add_new") }}</button>
    </div>

    <table class="roles-table">
      <thead>
        <tr>
          <th>{{ $t("roles.name") }}</th>
          <th>{{ $t("common.actions") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="role in roles" :key="role.id" @click="openModal(role)">
          <td>{{ role.name }}</td>
          <td>
            <button @click.stop="deleteRole(role.id)">✖</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Модальное окно -->
    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <h3>{{ editingRole ? $t("common.edit") : $t("common.add_new") }}</h3>

        <label>{{ $t("roles.name") }}</label>
        <input v-model="name" />

        <div class="modal-actions">
          <button @click="saveRole">{{ $t("common.save") }}</button>
          <button @click="closeModal">{{ $t("common.cancel") }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "@/api/axiosInstance";
import type { Role } from "@/types/Role"; // поправь путь, если другой

const roles = ref<Role[]>([]);
const showModal = ref(false);
const editingRole = ref<Role | null>(null);
const name = ref<string>("");

async function fetchRoles() {
  const response = await axios.get<Role[]>("/v1/roles");
  roles.value = response.data;
}

function openModal(role: Role | null) {
  editingRole.value = role;
  name.value = role?.name ?? "";
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  editingRole.value = null;
  name.value = "";
}

async function saveRole() {
  const payload = { name: name.value };

  if (editingRole.value) {
    await axios.put(`/v1/roles/${editingRole.value.id}`, payload);
  } else {
    await axios.post("/v1/roles", payload);
  }

  await fetchRoles();
  closeModal();
}

async function deleteRole(id: string) {
  await axios.delete(`/v1/roles/${id}`);
  await fetchRoles();
}

onMounted(fetchRoles);
</script>

<style scoped>
.roles-page {
  padding: 2rem;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
.roles-table {
  width: 100%;
  border-collapse: collapse;
}
.roles-table th,
.roles-table td {
  border: 1px solid #ccc;
  padding: 0.5rem;
}
.roles-table tr:hover {
  background: #f0f0f0;
  cursor: pointer;
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
