<template>
  <div class="login-wrapper">
    <div class="login-box">
      <h1>{{ $t("login.welcome") }}</h1>
      <h2>{{ $t("login.page") }}</h2>

      <input
        v-model="username"
        :placeholder="$t('login.username_placeholder')"
        @blur="checkUsername"
        @keyup.enter="checkUsername"
      />

      <input
        v-if="requiresPassword"
        type="password"
        v-model="password"
        :placeholder="$t('login.password_placeholder')"
        @keyup.enter="handleLogin"
        ref="passwordInput"
      />

      <RecaptchaV2
        class="recaptcha"
        :sitekey="SITE_KEY"
        @widget-id="onWidgetId"
        @load-callback="onCaptchaVerified"
        @expired-callback="onCaptchaExpired"
        @error-callback="onCaptchaError"
      />

      <button @click="handleLogin">{{ $t("login.button") }}</button>

      <p v-if="error" class="error-text">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from "vue";
import axios from "@/api/axiosInstance";
import { useAuthStore } from "@/stores/authStore";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { RecaptchaV2, useRecaptcha } from "vue3-recaptcha-v2";

const username = ref("");
const password = ref("");
const requiresPassword = ref(false);
const error = ref("");

const SITE_KEY = ref(process.env.VUE_APP_RECAPTCHA_SITE_KEY || "");
const captchaToken = ref("");

const widgetId = ref<number | null>(null);
const { handleReset } = useRecaptcha();

const authStore = useAuthStore();
const router = useRouter();
const { t } = useI18n();

const passwordInput = ref<HTMLInputElement | null>(null);

watch(requiresPassword, async (val) => {
  if (val) {
    await nextTick();
    passwordInput.value?.focus();
  }
});

function onWidgetId(id: number) {
  widgetId.value = id;
}

function onCaptchaVerified(token: unknown) {
  captchaToken.value = String(token || "");
  error.value = "";
}

function onCaptchaExpired() {
  captchaToken.value = "";
}

function onCaptchaError() {
  captchaToken.value = "";
  error.value = "Captcha error. Please try again.";
}

async function checkUsername() {
  if (!username.value.trim()) return;

  try {
    const res = await axios.get("/v1/auth/check", {
      params: { nickname: username.value },
    });
    requiresPassword.value = res.data.requiresPassword;
  } catch (e) {
    console.error(e);
  }
}

async function handleLogin() {
  error.value = "";

  if (!username.value.trim()) return;

  if (!captchaToken.value) {
    error.value = "Please validate that you are not a robot.";
    return;
  }

  try {
    const params: any = {
      username: username.value,
      captcha: captchaToken.value,
    };

    if (requiresPassword.value) {
      if (!password.value.trim()) {
        error.value = t("login.required_password");
        return;
      }
      params.password = password.value;
    }

    const res = await axios.post("/v1/auth/login", params);

    const {
      token,
      username: uname,
      role,
      userId,
      roleId,
      isActive,
      createdAt,
    } = res.data;

    authStore.login(
      {
        id: userId,
        username: uname,
        role: { id: roleId, name: role },
        isActive: isActive,
        createdAt: createdAt,
      },
      token
    );

    await router.push(
      role === "ADMIN" ? "/users" : role === "MODERATOR" ? "/reports" : "/chats"
    );
  } catch (err: any) {
    error.value = err.response?.data || t("login.error");
  } finally {
    if (widgetId.value !== null) {
      handleReset(widgetId.value);
    }
    captchaToken.value = "";
  }
}
</script>

<style scoped>
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  background-color: var(--color-bg);
}

.login-box {
  background-color: white;
  padding: 2rem;
  border-radius: 10px;
  border: 1px solid #ccc;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-box input {
  display: block;
  width: 100%;
  padding: 0.5rem;
  margin-top: 1rem;
  margin-bottom: 1rem;
  border: 1px solid var(--color-border);
  border-radius: 5px;
}

.login-box button {
  width: 100%;
  padding: 0.6rem;
  background-color: var(--color-primary);
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.login-box button:hover {
  background-color: var(--color-primary-hover);
}

.recaptcha {
  padding-bottom: 2rem;
}

.error-text {
  color: red;
  margin-top: 0.5rem;
  text-align: center;
}
</style>
