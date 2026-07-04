<script setup lang="ts">
import { ref } from 'vue'
import type { Message } from './types'
import { sendChatMessage } from './api/agent'
import ChatHeader from './components/ChatHeader.vue'
import ChatMessages from './components/ChatMessages.vue'
import ChatInput from './components/ChatInput.vue'

const messages = ref<Message[]>([
  {
    id: 1,
    type: 'agent',
    content: '您好！我是您的企业 HR 数字员工，请问有什么可以帮您的吗？',
    timestamp: new Date().toLocaleTimeString()
  }
])

const isLoading = ref(false)

async function handleSend(message: string, file: File | null) {
  if (!message && !file) return

  const userMsg: Message = {
    id: Date.now(),
    type: 'user',
    content: message,
    timestamp: new Date().toLocaleTimeString()
  }
  messages.value.push(userMsg)

  isLoading.value = true

  try {
    const response = await sendChatMessage(
      {
        thread_id: 'default',
        query: message || (file ? `请帮我解析这份${file.name}文件` : '')
      },
      file
    )

    const agentMsg: Message = {
      id: Date.now() + 1,
      type: 'agent',
      content: response.data?.answer || response.reply || '抱歉，我暂时无法回答这个问题',
      timestamp: new Date().toLocaleTimeString()
    }
    messages.value.push(agentMsg)
  } catch (error) {
    const errorMsg: Message = {
      id: Date.now() + 1,
      type: 'agent',
      content: '连接失败，请稍后重试',
      timestamp: new Date().toLocaleTimeString()
    }
    messages.value.push(errorMsg)
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="app-container">
    <ChatHeader />
    <main class="chat-container">
      <ChatMessages :messages="messages" :is-loading="isLoading" />
      <ChatInput :is-loading="isLoading" @send="handleSend" />
    </main>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}
</style>