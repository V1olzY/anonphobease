<template>
  <nav class="navbar">
    <div class="left-side">
      <img :src="logo" alt="AnonPhobEase logo" class="logo-img" />

      <div
        v-if="isLoggedIn"
        class="nav-links"
        :class="{ open: isMenuOpen }"
        @click="isMenuOpen = false"
      >
        <component :is="navbarComponent" />
      </div>
    </div>

    <div class="right-side">
      <button
        v-if="isLoggedIn"
        class="nav-toggle"
        type="button"
        @click="isMenuOpen = !isMenuOpen"
        aria-label="Toggle navigation"
      >
        ☰
      </button>

      <LanguageSelector />
      <LogoutButton v-if="isLoggedIn" />
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import logo from "@/assets/anonphobease.png";
import AdminNavbar from "@/components/nav/AdminNavbar.vue";
import ModeratorNavbar from "@/components/nav/ModeratorNavbar.vue";
import UserNavbar from "@/components/nav/UserNavbar.vue";
import LanguageSelector from "@/components/common/LanguageSelector.vue";
import LogoutButton from "@/components/common/LogoutButton.vue";

const authStore = useAuthStore();
const route = useRoute();

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

const isMenuOpen = ref(false);

watch(
  () => route.fullPath,
  () => {
    isMenuOpen.value = false;
  }
);
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 200;

  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  box-shadow: 0 1px 6px rgba(17, 24, 39, 0.06);

  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 6px 12px;
  min-height: 48px;
}

.left-side,
.right-side {
  display: flex;
  align-items: center;
  gap: 10px;
}

.left-side {
  flex: 1;
  min-width: 0;
}

.logo-img {
  height: 28px;
  width: auto;
  display: block;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  padding: 0;
  border-radius: 0;
}

.nav-toggle {
  display: none;
  align-items: center;
  justify-content: center;

  width: 36px;
  height: 34px;
  border-radius: 10px;

  border: 1px solid var(--color-border);
  background: var(--color-surface);
  cursor: pointer;

  font-size: 1.1rem;
  line-height: 1;
  padding: 0;
}

.nav-links :deep(a) {
  color: var(--color-text);
  text-decoration: none;

  padding: 6px 10px;
  border-radius: 10px;
  line-height: 1.1;
  white-space: nowrap;
}

@media (hover: hover) and (pointer: fine) {
  .nav-links :deep(a:hover) {
    background: var(--color-muted-bg);
  }
}

.nav-links :deep(a.router-link-exact-active) {
  color: var(--color-primary);
  background: rgba(47, 111, 237, 0.12);
}

@media (max-width: 768px) {
  .right-side {
    gap: 8px;
  }

  .nav-toggle {
    display: inline-flex;
  }

  .nav-links {
    position: absolute;
    left: 0;
    right: 0;
    top: 100%;

    display: none;
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;

    padding: 10px 12px;
    background: var(--color-surface);
    border-bottom: 1px solid var(--color-border);

    z-index: 300;
  }

  .nav-links.open {
    display: flex;
  }

  .nav-links :deep(a) {
    width: 100%;
    padding: 10px 10px;
  }
}
</style>
