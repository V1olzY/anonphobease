<template>
  <nav class="admin-navbar">
    <div
      class="dropdown"
      @mouseenter="canHover && (open = 'users')"
      @mouseleave="canHover && (open = null)"
    >
      <div class="dropdown-trigger">
        <router-link to="/users" class="top-link" @click="closeAll">
          {{ $t("users.page") }}
        </router-link>

        <button
          v-if="!canHover"
          type="button"
          class="caret"
          @click.stop="toggle('users')"
          aria-label="Toggle users submenu"
        >
          ▾
        </button>
      </div>

      <div
        v-show="open === 'users' || (!canHover && open === 'users')"
        class="dropdown-content"
        @click.stop
      >
        <router-link to="/roles" @click="closeAll">
          {{ $t("roles.page") }}
        </router-link>
      </div>
    </div>

    <div
      class="dropdown"
      @mouseenter="canHover && (open = 'chats')"
      @mouseleave="canHover && (open = null)"
    >
      <div class="dropdown-trigger">
        <router-link to="/admin/chats" class="top-link" @click="closeAll">
          {{ $t("chats.page") }}
        </router-link>

        <button
          v-if="!canHover"
          type="button"
          class="caret"
          @click.stop="toggle('chats')"
          aria-label="Toggle chats submenu"
        >
          ▾
        </button>
      </div>

      <div
        v-show="open === 'chats' || (!canHover && open === 'chats')"
        class="dropdown-content"
        @click.stop
      >
        <router-link to="/languages" @click="closeAll">
          {{ $t("languages.page") }}
        </router-link>
        <router-link to="/phobias" @click="closeAll">
          {{ $t("phobias.page") }}
        </router-link>
      </div>
    </div>

    <router-link to="/reports" @click="closeAll">{{
      $t("reports.page")
    }}</router-link>
    <router-link to="/logs" @click="closeAll">{{
      $t("logs.page")
    }}</router-link>
    <router-link to="/bans" @click="closeAll">{{
      $t("bans.page")
    }}</router-link>
  </nav>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from "vue";

const open = ref<null | "users" | "chats">(null);
const canHover = ref(true);

let mq: MediaQueryList | null = null;

function updateHoverCapability() {
  canHover.value = window.matchMedia(
    "(hover: hover) and (pointer: fine)"
  ).matches;

  if (!canHover.value && open.value === null) {
    open.value = null;
  }
}

function toggle(key: "users" | "chats") {
  open.value = open.value === key ? null : key;
}

function closeAll() {
  open.value = null;
}

function onOutsideClick() {
  if (!canHover.value) closeAll();
}

onMounted(() => {
  updateHoverCapability();
  mq = window.matchMedia("(hover: hover) and (pointer: fine)");
  mq.addEventListener?.("change", updateHoverCapability);
  window.addEventListener("click", onOutsideClick);
});

onBeforeUnmount(() => {
  mq?.removeEventListener?.("change", updateHoverCapability);
  window.removeEventListener("click", onOutsideClick);
});
</script>

<style scoped>
.admin-navbar {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: center;
}

.dropdown {
  position: relative;
}

.dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.caret {
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  border-radius: 8px;
  width: 32px;
  height: 28px;
  cursor: pointer;
  line-height: 1;
}

.dropdown-content {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  overflow: hidden;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  min-width: 180px;
}

.dropdown-content a {
  padding: 0.55rem 0.75rem;
  white-space: nowrap;
}

.dropdown-content a:hover {
  background: var(--color-muted-bg);
}

@media (max-width: 768px) {
  .admin-navbar {
    flex-direction: column;
    align-items: stretch;
    gap: 0.6rem;
  }

  .dropdown {
    width: 100%;
  }

  .dropdown-content {
    position: static;
    margin-top: 0.4rem;
    border: none;
    background: transparent;
    min-width: 0;
    padding-left: 1rem;
  }

  .dropdown-content a {
    padding: 0.35rem 0;
  }
}
@media (hover: hover) and (pointer: fine) {
  .dropdown::after {
    content: "";
    position: absolute;
    left: 0;
    right: 0;
    top: 100%;
    height: 10px;
  }
}
</style>
