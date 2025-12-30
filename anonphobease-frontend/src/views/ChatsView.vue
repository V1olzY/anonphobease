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
import { ChatsResponse } from "@/types/ChatRsponse";

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
      description: "",
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
  background: var(--panel-teal);
  padding: 1rem;
  border-radius: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.chat-list {
  flex: 1;
  background: var(--panel-blue);
  padding: 2rem;
  border-radius: 1rem;
}

.chat-link {
  display: block;
  text-decoration: none;
  color: inherit;
  margin-bottom: 1rem;
  border-radius: 12px;
  transition: transform 0.12s, box-shadow 0.12s;
}

.chat-link:visited {
  color: inherit;
}

.chat-link:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(17, 24, 39, 0.1);
}

.chat-link:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px rgba(47, 111, 237, 0.22);
}

@media (max-width: 768px) {
  .chat-page {
    flex-direction: column;
    padding: 16px;
  }

  .sidebar,
  .chat-list {
    width: 100%;
    padding: 16px;
    border-radius: 12px;
  }

  .sidebar h2,
  .chat-list h2 {
    margin: 0 0 12px 0;
  }

  .sidebar label {
    margin-top: 8px;
  }

  .sidebar select {
    width: 100%;
  }

  .chat-link {
    margin-bottom: 12px;
  }

  .chat-link:hover {
    transform: none;
    box-shadow: none;
  }
}

@media (max-width: 480px) {
  .sidebar,
  .chat-list {
    border-radius: 10px;
  }

  .chat-link {
    margin-bottom: 0.75rem;
  }
}
</style>
