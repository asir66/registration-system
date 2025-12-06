import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

import App from './App.vue';
import router from './router';
import { useAuthStore } from '@/stores/auth';
import { useDoctorStore } from '@/stores/doctor';

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(ElementPlus);

app.mount('#app');

// 恢复基础登录态（无 token，仅示例用）
const authStore = useAuthStore();
authStore.restore();

// 建立 WebSocket，用于接收后端广播的排班/挂号变更事件
// 使用后端实际端口（开发时后端通常运行在 8080），避免连接到 vite dev server
const backendPort = (location.port && location.port !== '5173') ? location.port : '8080';
const wsUrl = (location.protocol === 'https:' ? 'wss' : 'ws') + '://' + location.hostname + ':' + backendPort + '/ws';
const socket = new WebSocket(wsUrl);

socket.addEventListener('open', () => {
  console.info('[ws] connected', wsUrl);
});

socket.addEventListener('message', (event) => {
  try {
    const data = JSON.parse(event.data);
    // 事件格式示例：{ type: 'appointment_cancelled', doctorId, appointmentId, patientId }
    if (data.type === 'appointment_cancelled') {
      const doctorStore = useDoctorStore();
      doctorStore.onAppointmentCancelled(data);
    }
    // 若有其他类型事件可继续分支处理
  } catch (e) {
    console.error('[ws] invalid message', event.data, e);
  }
});

socket.addEventListener('close', () => {
  console.warn('[ws] closed');
});