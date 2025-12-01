import { createI18n } from "vue-i18n";
import est from "./est.json";
import rus from "./rus.json";
import eng from "./eng.json";

const DEFAULT_LANG = "est";

const i18n = createI18n({
  legacy: false,
  locale: DEFAULT_LANG,
  fallbackLocale: DEFAULT_LANG,
  messages: {
    est,
    rus,
    eng,
  },
});

export default i18n;
