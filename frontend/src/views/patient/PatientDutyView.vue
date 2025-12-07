<template>
  <div class="page">
    <el-card class="card" shadow="hover">
      <div class="header">
        <h2>值班医生</h2>
        <div>
          <el-button type="text" @click="fetchList">刷新</el-button>
        </div>
      </div>

      <div class="meta" v-if="list.length">共 {{ list.length }} 条</div>
      <template v-if="list.length">
        <el-table :data="list" style="width: 100%">
          <el-table-column prop="departmentName" label="科室" />
          <el-table-column label="值班星期" :formatter="formatWeekend" />
          <el-table-column label="值班时段" :formatter="formatTimeslot" />
          <el-table-column prop="doctorName" label="医生">
            <template #default="{ row }">
              {{ row.doctorName }}{{ row.doctorTitle ? ' ' + row.doctorTitle : '' }}
            </template>
          </el-table-column>
        </el-table>
      </template>
      <template v-else>
        <div class="empty-note">
          <el-empty description="当前暂无值班医生安排。" />
        </div>
      </template>

    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { listDutySchedules, listDutySchedulesAll } from '@/api/patient-duty';

const list = ref<any[]>([]);

function formatWeekend(row: any) {
  const w = Number(row?.weekendType);
  if (Number.isNaN(w)) return '';
  if (w === 6) return '周六';
  if (w === 7) return '周日';
  return String(w);
}

function formatTimeslot(row: any) {
  const t = row?.dutyTimeslot;
  if (!t) return '';
  if (t === 'MORNING') return '早班';
  if (t === 'AFTERNOON') return '中班';
  if (t === 'NIGHT') return '夜班';
  return String(t);
}

onMounted(async () => {
  await fetchList();
});

async function fetchList() {
  try {
    const data = await listDutySchedules();
    console.debug('duty schedules fetched', data);
    list.value = data ?? [];
    // if empty, try the /all fallback to check backend
    if (!list.value || list.value.length === 0) {
      try {
        const all = await listDutySchedulesAll();
        console.debug('duty schedules fallback /all fetched', all);
        if (all && all.length > 0) {
          list.value = all;
          // @ts-ignore
          ElMessage?.info('已回退到全部值班数据（可能存在筛选/鉴权问题）');
        }
      } catch (e2) {
        console.debug('fallback /all failed', e2);
      }
    }
  } catch (e: any) {
    console.error('fetch duty schedules error', e);
    list.value = [];
    // show user-facing message
    const msg = e?.response?.data?.message || '获取值班信息失败';
    // Use Element Plus global message if available
    // @ts-ignore
    ElMessage?.error(msg);
  }
}
</script>

<style scoped>
.page { padding: 24px; background: #f8fafc; }
.card { width: 100%; }
.header { display:flex; justify-content:space-between; align-items:center; margin-bottom:12px }
.empty-note { text-align: center; padding: 50px 0; }
</style>
