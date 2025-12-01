<template>
  <div class="unauthorized">
    <h1>{{ $t("unauthorized.page") }}</h1>
    <p>{{ $t("unauthorized.message") }}</p>
    <router-link to="/">{{ $t("unauthorized.back?home") }}</router-link>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.unauthorized {
  max-width: 600px;
  margin: 100px auto;
  text-align: center;
}

.unauthorized h1 {
  font-size: 2rem;
  color: #d9534f;
}

.unauthorized p {
  margin: 20px 0;
  font-size: 1.2rem;
}

.unauthorized a {
  color: #0275d8;
  text-decoration: underline;
}
</style>
