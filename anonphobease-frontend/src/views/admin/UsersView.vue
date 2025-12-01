<template>
  <div class="admin-page">
    <div class="header">
      <h2>{{ $t("users.page") }}</h2>
      <button @click="openModal(null)">{{ $t("common.add_new") }}</button>
    </div>

    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("users.username") }}</th>
          <th>{{ $t("users.role") }}</th>
          <th>{{ $t("users.is_active") }}</th>
          <th>{{ $t("users.created_at") }}</th>
          <th>{{ $t("common.actions") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id" @click="openModal(user)">
          <td>{{ user.username }}</td>
          <td>{{ user.role.name }}</td>
          <td>{{ user.isActive ? "✔" : "✘" }}</td>
          <td>{{ new Date(user.createdAt).toLocaleDateString() }}</td>
          <td @click.stop>
            <button @click="deleteUser(user.id)">❌</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <h3>{{ editingUser ? $t("common.edit") : $t("common.add_new") }}</h3>

        <label>{{ $t("users.username") }}</label>
        <input v-model="username" />

        <label>{{ $t("users.role") }}</label>
        <select v-model="selectedRole">
          <option v-for="role in roles" :key="role.id" :value="role.id">
            {{ role.name }}
          </option>
        </select>
        <label v-if="isPrivilegedRole">{{ $t("users.password") }}</label>
        <input v-if="isPrivilegedRole" v-model="password" type="password" />

        <label>{{ $t("users.is_active") }}</label>
        <input type="checkbox" v-model="isActive" />

        <div class="modal-actions">
          <button @click="saveUser">{{ $t("common.save") }}</button>
          <button @click="closeModal">{{ $t("common.cancel") }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import axios from "@/api/axiosInstance";

import type { User } from "@/types/User";
import type { Role } from "@/types/Role";

const users = ref<User[]>([]);
const roles = ref<Role[]>([]);
const showModal = ref(false);
const editingUser = ref<User | null>(null);

const username = ref("");
const selectedRole = ref<string>("");
const isActive = ref<boolean>(false);
const password = ref<string>("");

async function fetchUsers() {
  const res = await axios.get("/v1/users");
  users.value = res.data as User[];
}

async function fetchRoles() {
  const res = await axios.get("/v1/roles");
  roles.value = res.data as Role[];
}

onMounted(async () => {
  await fetchUsers();
  await fetchRoles();
});

const isPrivilegedRole = computed(() => {
  const role = roles.value.find((r) => r.id === selectedRole.value);
  const name = role?.name;
  return name === "ADMIN" || name === "MODERATOR";
});

function openModal(user: User | null) {
  showModal.value = true;
  editingUser.value = user;

  if (user) {
    username.value = user.username;
    selectedRole.value = user.role.id;
    isActive.value = user.isActive;
    password.value = "";
  } else {
    username.value = "";
    selectedRole.value = roles.value[0]?.id || "";
    isActive.value = true;
    password.value = "";
  }
}

function closeModal() {
  showModal.value = false;
}

async function saveUser() {
  const userPayload: any = {
    username: username.value,
    role: { id: selectedRole.value },
    isActive: isActive.value,
  };

  if (isPrivilegedRole.value && password.value.trim()) {
    userPayload.password = password.value.trim();
  }

  if (editingUser.value) {
    await axios.put(`/v1/users/${editingUser.value.id}`, userPayload);
  } else {
    await axios.post("/v1/users", userPayload);
  }

  closeModal();
  await fetchUsers();
}

async function deleteUser(id: string) {
  await axios.delete(`/v1/users/${id}`);
  await fetchUsers();
}
</script>

<style scoped>
.admin-page {
  padding: 2rem;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.table {
  width: 100%;
  border-collapse: collapse;
}
.table th,
.table td {
  padding: 0.5rem;
  border: 1px solid #ccc;
  text-align: left;
}
.table tr:hover {
  background-color: #f2f2f2;
  cursor: pointer;
}
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.modal-actions {
  display: flex;
  justify-content: space-between;
}
</style>
