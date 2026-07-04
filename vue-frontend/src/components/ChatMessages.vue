<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import type { Message } from '../types'
import MessageItem from './MessageItem.vue'
import LoadingIndicator from './LoadingIndicator.vue'

const props = defineProps<{
  messages: Message[]
  isLoading: boolean
}>()

const messagesContainer = ref<HTMLElement | null>(null)

watch(() => props.messages.length, async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
})
</script>

<template>
  <div ref="messagesContainer" class="chat-messages">
    <MessageItem
      v-for="msg in messages"
      :key="msg.id"
      :message="msg"
    />
    <LoadingIndicator v-if="isLoading" />
  </div>
</template>

<style scoped>
.chat-messages {
  flex: 1;
  background: white;
  border-radius: 16px;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
}
</style>