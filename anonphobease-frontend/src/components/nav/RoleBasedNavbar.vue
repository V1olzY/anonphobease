<template>
  <nav class="navbar">
    <div class="left-side">
      <img :src="logo" alt="AnonPhobEase logo" class="logo-img" />
      <component :is="navbarComponent" v-if="isLoggedIn" />
    </div>

    <div class="right-side">
      <LanguageSelector />
      <LogoutButton v-if="isLoggedIn" />
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useAuthStore } from "@/stores/authStore";
import logo from "@/assets/anonphobease.png";
import AdminNavbar from "@/components/nav/AdminNavbar.vue";
import ModeratorNavbar from "@/components/nav/ModeratorNavbar.vue";
import UserNavbar from "@/components/nav/UserNavbar.vue";
import LanguageSelector from "@/components/common/LanguageSelector.vue";
import LogoutButton from "@/components/common/LogoutButton.vue";

const authStore = useAuthStore();
const role = computed(() => authStore.user?.role.name);
const isLoggedIn = computed(() => !!authStore.token);

const navbarComponent = computed(() => {
  switch (role.value) {
    case "ADMIN":
      return AdminNavbar;
    case "MODERATOR":
      return ModeratorNavbar;
    case "USER":
      return UserNavbar;
    default:
      return null;
  }
});
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
}

.left-side,
.right-side {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo-img {
  height: 32px;
  width: auto;
  display: block;
}
</style>
