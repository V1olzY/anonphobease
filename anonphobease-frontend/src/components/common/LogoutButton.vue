<template>
  <button
    class="icon-btn icon-btn--logout"
    type="button"
    @click="handleLogout"
    :aria-label="$t('logout')"
    :title="$t('logout')"
  >
    <img :src="logoutIcon" alt="" aria-hidden="true" class="icon" />
    <span class="sr-only">{{ $t("logout") }}</span>
  </button>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import logoutIcon from "@/assets/logout.png";

const authStore = useAuthStore();
const router = useRouter();

async function handleLogout() {
  await authStore.logout();
  await router.push("/");
}
</script>

<style scoped>
.icon-btn {
  appearance: none;
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: 10px;
  padding: 0.45rem 0.6rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  cursor: pointer;
  line-height: 1;
}

.icon-btn .icon {
  width: 18px;
  height: 18px;
  display: block;
}

.icon-btn--logout:hover {
  border-color: var(--color-danger);
  background: var(--status-danger-bg);
}

.icon-btn--logout:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px rgba(214, 69, 69, 0.22);
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
