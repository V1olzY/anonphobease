<template>
  <div class="admin-chats">
    <div class="header">
      <h2>{{ $t("chats.page") }}</h2>
      <button @click="openModal(null)" class="add-btn">
        {{ $t("common.add_new") }}
      </button>
    </div>

    <table class="table">
      <thead>
        <tr>
          <th>{{ $t("languages.page") }}</th>
          <th>{{ $t("phobias.page") }}</th>
          <th>{{ $t("common.actions") }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="chat in chats" :key="chat.id" @click="openModal(chat)">
          <td>{{ chat.language.name || "-" }}</td>
          <td>{{ chat.phobia.name || "-" }}</td>
          <td class="actions-cell" @click.stop>
            <button class="icon-button" @click="goToChat(chat.id)">🔗</button>
            <button class="icon-button" @click="deleteChat(chat.id)">❌</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <h3>{{ editingChat ? $t("common.edit") : $t("common.add_new") }}</h3>

        <label>{{ $t("languages.name") }}</label>
        <select v-model="selectedLanguage">
          <option v-for="lang in languages" :key="lang.id" :value="lang.id">
            {{ lang.name }}
          </option>
        </select>

        <label>{{ $t("phobias.name") }}</label>
        <select v-model="selectedPhobia">
          <option v-for="phobia in phobias" :key="phobia.id" :value="phobia.id">
            {{ phobia.name }}
          </option>
        </select>

        <div class="modal-actions">
          <button @click="saveChat">
            {{ $t("common.save") }}
          </button>
          <button @click="closeModal">
            {{ $t("common.cancel") }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "@/api/axiosInstance";

import type { Chat } from "@/types/Chat";
import type { Language } from "@/types/Language";
import type { Phobia } from "@/types/Phobia";
import { ChatsResponse } from "@/types/ChatRsponse";

const router = useRouter();

const chats = ref<Chat[]>([]);
const languages = ref<Language[]>([]);
const phobias = ref<Phobia[]>([]);

const showModal = ref(false);
const editingChat = ref<Chat | null>(null);
const selectedLanguage = ref<string>("");
const selectedPhobia = ref<string>("");

async function fetchData() {
  const response = await axios.get<ChatsResponse>("/v1/chats");

  chats.value = response.data.chats.map((dto) => ({
    id: dto.id,
    name: dto.chatName,
    phobia: {
      id: dto.phobiaId,
      name: dto.phobiaName,
      description: "",
    },
    language: {
      id: dto.languageId,
      name: dto.languageName,
      code: dto.languageCode,
    },
  }));
  languages.value = response.data.languages;
  phobias.value = response.data.phobias;
}

function openModal(chat: Chat | null) {
  editingChat.value = chat;
  selectedLanguage.value = chat?.language.id ?? "";
  selectedPhobia.value = chat?.phobia.id ?? "";
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  editingChat.value = null;
  selectedLanguage.value = "";
  selectedPhobia.value = "";
}

async function saveChat() {
  const payload = {
    languageId: selectedLanguage.value,
    phobiaId: selectedPhobia.value,
  };

  if (editingChat.value) {
    await axios.put(`/v1/chats/${editingChat.value.id}`, payload);
  } else {
    await axios.post("/v1/chats", payload);
  }

  await fetchData();
  closeModal();
}

function goToChat(id: string) {
  router.push(`/chats/${id}`);
}

async function deleteChat(id: string) {
  if (!confirm("Kas sa oled kindel?")) {
    return;
  }

  await axios.delete(`/v1/chats/${id}`);
  chats.value = chats.value.filter((c) => c.id !== id);
}
onMounted(fetchData);
</script>

<style scoped>
.admin-chats {
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.add-btn {
  padding: 0.5rem 1rem;
}

.actions-cell {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
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
