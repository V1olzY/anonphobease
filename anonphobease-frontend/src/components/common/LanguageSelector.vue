<template>
  <div class="language-selector">
    <select v-model="selectedLang" @change="updateLanguage">
      <option value="est">EST</option>
      <option value="rus">RUS</option>
      <option value="eng">ENG</option>
    </select>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const { locale } = useI18n();

const selectedLang = ref(localStorage.getItem("language") || "est");

function updateLanguage() {
  locale.value = selectedLang.value;
  localStorage.setItem("language", selectedLang.value);
}

watch(selectedLang, (newLang) => {
  locale.value = newLang;
});
</script>

<style scoped>
.language-selector select {
  padding: 0.25rem 0.5rem;
  font-size: 1rem;
}
</style>
