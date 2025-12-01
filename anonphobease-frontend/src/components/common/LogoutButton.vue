<template>
  <button class="logout-button" @click="handleLogout">
    {{ $t("logout") }}
  </button>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import { useI18n } from "vue-i18n";

const authStore = useAuthStore();
const router = useRouter();
const { t } = useI18n();

async function handleLogout() {
  await authStore.logout();
  await router.push("/");
}
</script>

<style scoped>
.logout-button {
  padding: 0.5rem 1rem;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.logout-button:hover {
  background-color: #d32f2f;
}
</style>
