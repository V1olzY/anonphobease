<template>
  <div class="chat-page">
    <div class="sidebar">
      <h2>{{ $t("chats.filter") }}</h2>

      <label>{{ $t("chats.choose_phobia") }}</label>
      <select v-model="selectedPhobiaId">
        <option value="">{{ $t("chats.all") }}</option>
        <option v-for="phobia in phobias" :key="phobia.id" :value="phobia.id">
          {{ $t("phobiaNames." + phobia.name.toUpperCase()) }}
        </option>
      </select>

      <label>{{ $t("chats.choose_language") }}</label>
      <select v-model="selectedLanguageId">
        <option value="">{{ $t("chats.all") }}</option>
        <option v-for="lang in languages" :key="lang.id" :value="lang.id">
          {{ lang.name }}
        </option>
      </select>
    </div>

    <div class="chat-list">
      <h2>{{ $t("chats.title") }}</h2>
      <router-link
        v-for="chat in filteredChats"
        :key="chat.id"
        :to="`/chats/${chat.id}`"
        class="chat-link"
      >
        <ChatCard :chat="chat" />
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import axios from "@/api/axiosInstance";
import ChatCard from "@/components/chat/ChatCard.vue";
import type { Chat } from "@/types/Chat";
import type { Phobia } from "@/types/Phobia";
import type { Language } from "@/types/Language";
import type { ChatDTO } from "@/types/ChatDTO";

interface ChatsResponse {
  chats: ChatDTO[];
  phobias: Phobia[];
  languages: Language[];
}

const chats = ref<Chat[]>([]);
const phobias = ref<Phobia[]>([]);
const languages = ref<Language[]>([]);

const selectedPhobiaId = ref<string>("");
const selectedLanguageId = ref<string>("");

onMounted(async () => {
  const response = await axios.get<ChatsResponse>("/v1/chats");

  chats.value = response.data.chats.map((dto) => ({
    id: dto.id,
    name: dto.chatName,
    phobia: {
      id: dto.phobiaId,
      name: dto.phobiaName,
    },
    language: {
      id: dto.languageId,
      name: dto.languageName,
      code: dto.languageCode,
    },
  }));

  phobias.value = response.data.phobias;
  languages.value = response.data.languages;
});

const filteredChats = computed<Chat[]>(() => {
  return chats.value.filter((chat) => {
    const matchesPhobia =
      !selectedPhobiaId.value || chat.phobia?.id === selectedPhobiaId.value;

    const matchesLanguage =
      !selectedLanguageId.value ||
      chat.language?.id === selectedLanguageId.value;

    return matchesPhobia && matchesLanguage;
  });
});
</script>

<style scoped>
.chat-page {
  display: flex;
  gap: 2rem;
  padding: 2rem;
}

.sidebar {
  width: 250px;
  background: #a3c8a3;
  padding: 1rem;
  border-radius: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.chat-list {
  flex: 1;
  background: #bccdfc;
  padding: 2rem;
  border-radius: 1rem;
}

.chat-card {
  background: white;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flag {
  width: 30px;
}
</style>
