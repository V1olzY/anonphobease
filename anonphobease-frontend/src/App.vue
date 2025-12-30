<template>
  <div id="app">
    <header class="app-header">
      <RoleBasedNavbar />
    </header>

    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<style lang="scss">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: var(--color-text);

    &.router-link-exact-active {
      color: var(--color-secondary);
    }
  }
}
</style>

<script setup lang="ts">
import { useAuthStore } from "@/stores/authStore";
import RoleBasedNavbar from "@/components/nav/RoleBasedNavbar.vue";
import { onMounted, onUnmounted } from "vue";
import { getToken, removeToken } from "@/utils/jwt";
import { useRouter } from "vue-router";

const router = useRouter();
const apiUrl = process.env.VUE_APP_API_URL;

if (!getToken()) {
  router.replace("/");
}
const authStore = useAuthStore();

function handleTabClose() {
  const token = getToken();
  if (token && authStore.user?.role.name === "USER") {
    const data = JSON.stringify({ token });
    const blob = new Blob([data], { type: "application/json" });
    navigator.sendBeacon(`${apiUrl}/v1/auth/user-left`, blob);
    removeToken();
    authStore.clearUser();
  }
}

onMounted(() => {
  const script = document.createElement("script");
  script.src = "https://www.google.com/recaptcha/api.js";
  script.async = true;
  script.defer = true;
  document.head.appendChild(script);

  window.addEventListener("beforeunload", handleTabClose);
});

onUnmounted(() => {
  window.removeEventListener("beforeunload", handleTabClose);
});
</script>
