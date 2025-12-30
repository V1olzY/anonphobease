import { createApp, type Plugin } from "vue";
import { createPinia } from "pinia";
import piniaPersistedstate from "pinia-plugin-persistedstate";
import { useAuthStore } from "@/stores/authStore";
import i18n from "./i18n";
import App from "./App.vue";
import router from "./router";
import { install } from "vue3-recaptcha-v2";
import "@/styles/forms.scss";
import "@/styles/theme.scss";
import "@/styles/buttons.scss";
import "@/styles/tables.scss";

const pinia = createPinia();
pinia.use(piniaPersistedstate);

const savedLang = localStorage.getItem("language");
if (savedLang === "est" || savedLang === "rus" || savedLang === "eng") {
  i18n.global.locale.value = savedLang;
}

const app = createApp(App);
app.use(pinia);
app.use(router);
app.use(i18n);
app.use(install, {
  sitekey: process.env.VUE_APP_RECAPTCHA_SITE_KEY,
  cnDomains: false,
});
const authStore = useAuthStore();
authStore.initialize();

app.mount("#app");
