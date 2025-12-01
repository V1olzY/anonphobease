<template>
  <nav class="admin-navbar">
    <div
      class="dropdown"
      @mouseenter="showUsers = true"
      @mouseleave="showUsers = false"
    >
      <router-link to="/users">{{ $t("users.page") }}</router-link>
      <div v-if="showUsers" class="dropdown-content">
        <router-link to="/roles">{{ $t("roles.page") }}</router-link>
      </div>
    </div>

    <div
      class="dropdown"
      @mouseenter="showChats = true"
      @mouseleave="showChats = false"
    >
      <router-link to="/admin/chats">{{ $t("chats.page") }}</router-link>
      <div v-if="showChats" class="dropdown-content">
        <router-link to="/languages">{{ $t("languages.page") }}</router-link>
        <router-link to="/phobias">{{ $t("phobias.page") }}</router-link>
      </div>
    </div>

    <router-link to="/reports">{{ $t("reports.page") }}</router-link>
    <router-link to="/logs">{{ $t("logs.page") }}</router-link>
    <router-link to="/bans">{{ $t("bans.page") }}</router-link>
  </nav>
</template>

<script setup lang="ts">
import { ref } from "vue";

const showUsers = ref(false);
const showChats = ref(false);
</script>

<style scoped>
.admin-navbar {
  display: flex;
  gap: 1rem;
}

.dropdown {
  position: relative;
}

.dropdown-content {
  position: absolute;
  top: 100%;
  left: 0;
  background: white;
  border: 1px solid #ccc;
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.dropdown-content a {
  padding: 0.5rem;
  white-space: nowrap;
}
</style>
