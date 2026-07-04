<script setup lang="ts">
</script>

<template>
  <div class="loading-message">
    <span class="loading-dots">
      <span></span><span></span><span></span>
    </span>
  </div>
</template>

<style scoped>
.loading-message {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
}

.loading-dots span {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  margin: 0 2px;
  animation: loading 1.4s infinite ease-in-out;
}

.loading-dots span:nth-child(2) {
  animation-delay: 0.2s;
}

.loading-dots span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes loading {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}
</style>