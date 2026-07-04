import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/agent': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/recruitment': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/employee': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/attendance': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/payroll': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/leave': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/dashboard': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
})
