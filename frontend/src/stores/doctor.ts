import { defineStore } from 'pinia';

export type Appointment = {
  id: number | string;
  patientId: number | string;
  patientName?: string;
  time?: string;
  // 新增取消相关字段
  canceled?: boolean;
  canceledAt?: string;
  // 其余字段按需补充
};

type CancelEventPayload = {
  type: 'appointment_cancelled';
  doctorId?: number | string;
  appointmentId?: number | string;
  patientId?: number | string;
  canceledAt?: string;
};

export const useDoctorStore = defineStore('doctor', {
  state: () => ({
    doctorId: null as number | string | null,
    queue: [] as Appointment[],      // 全部待接诊队列（用于内部逻辑）
    todayQueue: [] as Appointment[], // 今日待诊（界面显示使用，保留已取消项并标记）
    availableSlots: 0,               // 可接诊人数
  }),
  actions: {
    init(doctorId: number | string, queue: Appointment[] = [], todayQueue: Appointment[] = [], availableSlots = 0) {
      this.doctorId = doctorId;
      this.queue = queue;
      // 如果后端已带 canceled 字段，会直接显示
      this.todayQueue = todayQueue;
      this.availableSlots = availableSlots;
    },

    // 在数组中删除匹配项，返回删除数量
    removeMatching(arr: Appointment[], predicate: (a: Appointment) => boolean): number {
      let removedCount = 0;
      for (let i = arr.length - 1; i >= 0; i--) {
        if (predicate(arr[i])) {
          arr.splice(i, 1);
          removedCount++;
        }
      }
      return removedCount;
    },

    // 在数组中标记为已取消，返回标记数量
    markCanceled(arr: Appointment[], predicate: (a: Appointment) => boolean, canceledAt?: string): number {
      let marked = 0;
      for (let i = 0; i < arr.length; i++) {
        if (predicate(arr[i]) && !arr[i].canceled) {
          arr[i].canceled = true;
          arr[i].canceledAt = canceledAt ?? new Date().toISOString();
          marked++;
        }
      }
      return marked;
    },

    // 从后端拉取最新队列（需要后端提供该接口）
    async syncQueuesFromServer() {
      if (this.doctorId == null) return;
      try {
        const resp = await fetch(`/api/doctors/${this.doctorId}/queues`, { method: 'GET', credentials: 'include' });
        if (!resp.ok) return;
        const data = await resp.json();
        this.queue = Array.isArray(data.queue) ? data.queue : [];
        this.todayQueue = Array.isArray(data.todayQueue) ? data.todayQueue : [];
        if (typeof data.availableSlots === 'number') this.availableSlots = data.availableSlots;
      } catch (e) {
        // 静默处理网络错误
      }
    },

    // 收到后端广播的取消事件时调用
    async onAppointmentCancelled(payload: CancelEventPayload) {
      if (!payload || payload.type !== 'appointment_cancelled') return;
      if (this.doctorId != null && payload.doctorId != null && String(this.doctorId) !== String(payload.doctorId)) {
        return; // 非当前医生的事件
      }

      const predByAppointmentId = (id?: number | string) => (a: Appointment) => id != null && String(a.id) === String(id);
      const predByPatientId = (p?: number | string) => (a: Appointment) => p != null && String(a.patientId) === String(p);

      let removedFromQueue = 0;
      let markedInToday = 0;
      const canceledAt = payload.canceledAt ?? new Date().toISOString();

      // 先尝试按照 appointmentId 删除/标记（如果提供），若未匹配再尝试按照 patientId
      if (payload.appointmentId != null) {
        const predA = predByAppointmentId(payload.appointmentId);
        removedFromQueue += this.removeMatching(this.queue, predA);
        markedInToday += this.markCanceled(this.todayQueue, predA, canceledAt);

        // 如果没有匹配到任何项，但提供了 patientId，则再尝试按 patientId 匹配（兼容后端返回的 DTO 结构）
        if (removedFromQueue === 0 && markedInToday === 0 && payload.patientId != null) {
          const predP = predByPatientId(payload.patientId);
          removedFromQueue += this.removeMatching(this.queue, predP);
          markedInToday += this.markCanceled(this.todayQueue, predP, canceledAt);
        }
      } else if (payload.patientId != null) {
        const pred = predByPatientId(payload.patientId);
        removedFromQueue += this.removeMatching(this.queue, pred);
        markedInToday += this.markCanceled(this.todayQueue, pred, canceledAt);
      } else {
        // 若事件中不包含 id，可尝试其它字段或直接同步后端状态
        await this.syncQueuesFromServer();
        return;
      }

      // 决定可接诊人数的增量：
      // 若在 queue 中删除到 N 项，按 N 增加；否则若 queue 未删除但 todayQueue 标记到 M 项，则按 M 增加。
      const delta = removedFromQueue > 0 ? removedFromQueue : markedInToday;
      if (delta > 0) {
        this.availableSlots = (this.availableSlots ?? 0) + delta;
      } else {
        // 本地既未删除也未标记，可能数据不一致 -> 尝试从后端刷新
        await this.syncQueuesFromServer();
      }
    },

    // 本端发起取消后立即更新界面（并期望后端广播以同步其它客户端）
    async removeAppointmentLocal(appointmentId?: number | string, patientId?: number | string) {
      let removedFromQueue = 0;
      let markedInToday = 0;
      const canceledAt = new Date().toISOString();

      if (appointmentId != null) {
        const predA = (a: Appointment) => String((a as any).id) === String(appointmentId);
        removedFromQueue += this.removeMatching(this.queue, predA);
        markedInToday += this.markCanceled(this.todayQueue, predA, canceledAt);

        // 如果未匹配到，再尝试按 patientId
        if (removedFromQueue === 0 && markedInToday === 0 && patientId != null) {
          const predP = (a: Appointment) => String((a as any).patientId) === String(patientId);
          removedFromQueue += this.removeMatching(this.queue, predP);
          markedInToday += this.markCanceled(this.todayQueue, predP, canceledAt);
        }
      } else if (patientId != null) {
        const pred = (a: Appointment) => String((a as any).patientId) === String(patientId);
        removedFromQueue += this.removeMatching(this.queue, pred);
        markedInToday += this.markCanceled(this.todayQueue, pred, canceledAt);
      }

      const delta = removedFromQueue > 0 ? removedFromQueue : markedInToday;
      if (delta > 0) {
        this.availableSlots = (this.availableSlots ?? 0) + delta;
      } else {
        // 本地未找到 -> 尝试刷新
        await this.syncQueuesFromServer();
      }
    },

    setQueues(queue: Appointment[], todayQueue: Appointment[], availableSlots?: number) {
      this.queue = queue || [];
      this.todayQueue = todayQueue || [];
      if (availableSlots != null) this.availableSlots = availableSlots;
    },
  },
});