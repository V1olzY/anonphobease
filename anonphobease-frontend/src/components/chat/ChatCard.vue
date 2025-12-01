<template>
  <div class="chat-card">
    <p>
      {{
        chat.phobia ? t("phobiaNames." + chat.phobia.name.toUpperCase()) : ""
      }}
    </p>
    <img :src="getFlagIcon(chat.language)" alt="language" class="flag" />
  </div>
</template>

<script setup lang="ts">
import estFlag from "@/assets/est-flag.png";
import rusFlag from "@/assets/rus-flag.png";
import engFlag from "@/assets/eng-flag.png";

import type { Chat } from "@/types/Chat";
import type { Language } from "@/types/Language";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const props = defineProps<{
  chat: Chat;
}>();

function getFlagIcon(lang: Language | undefined) {
  if (!lang) return "";

  const code = (lang.code || "").toLowerCase();

  if (code === "est") return estFlag;
  if (code === "rus") return rusFlag;
  if (code === "eng") return engFlag;

  return "";
}
</script>

<style scoped>
.chat-card {
  background: white;
  border-radius: 0.5rem;
  padding: 1rem;
  margin-bottom: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flag {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 2px solid #333;
  object-fit: cover;
}
</style>
