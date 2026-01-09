import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  base: './',
  build: {
    outDir: '../target/classes/META-INF/resources/sql/forge/console',
  },
  server: {
    proxy: {
      '/sql/forge': {
        target: 'http://localhost:8080', // 目标服务器地址
        changeOrigin: true, // 修改请求头中的 Origin
        // rewrite: (path) => path.replace(/^\/api/, ''), // 重写路径
      },
    },
  },
});
