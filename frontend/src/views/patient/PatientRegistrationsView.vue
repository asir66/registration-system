<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="filterDay" placeholder="按星期筛选（1-5）" style="width:180px" />
      <el-button type="primary" @click="fetchList">刷新</el-button>
    </div>

    <el-table :data="filtered" stripe style="width:100%" v-loading="loading">
      <el-table-column prop="registrationTime" label="挂号时间" width="180">
        <template #default="{ row }">{{ formatTime(row.registrationTime) }}</template>
      </el-table-column>
      <el-table-column prop="doctorName" label="医生" min-width="140">
        <template #default="{ row }">{{ row.doctorName }} ({{ row.doctorId }})</template>
      </el-table-column>
      <el-table-column prop="departmentName" label="科室" width="140" />
      <el-table-column prop="diseaseName" label="疾病" width="160" />
      <el-table-column prop="weekday" label="星期" width="80" />
      <el-table-column prop="timeslot" label="时段" width="120" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="onCancel(row)" :disabled="row.status === 'CANCELLED'">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="!loading && items.length === 0" class="empty">暂无挂号记录</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { listPatientRegistrations, cancelRegistration, PatientRegistrationInfo } from '@/api/patient-registrations';
import { ElMessage, ElMessageBox } from 'element-plus';

const auth = useAuthStore();
const items = ref<PatientRegistrationInfo[]>([]);
const loading = ref(false);
const filterDay = ref<string>('');

function formatTime(t?: string) {
  if (!t) return '';
  return new Date(t).toLocaleString();
}

const filtered = computed(() => {
  if (!filterDay.value) return items.value;
  const d = parseInt(filterDay.value);
  if (isNaN(d)) return items.value;
  return items.value.filter(i => i.weekday === d);
});

async function fetchList() {
  const patientProfileId = auth.user?.patientId;
  if (!patientProfileId) {
    ElMessage.error('未找到患者档案，请登录患者账号');
    return;
  }
  loading.value = true;
  try {
    items.value = await listPatientRegistrations(patientProfileId);
  } catch (e) {
    ElMessage.error('获取挂号记录失败');
  } finally {
    loading.value = false;
  }
}

async function onCancel(row: PatientRegistrationInfo) {
  const patientProfileId = auth.user?.patientId;
  if (!patientProfileId) {
    ElMessage.error('未找到患者档案，请登录患者账号');
    return;
  }
  try {
    await ElMessageBox.confirm('确定要取消该挂号吗？', '确认', { type: 'warning' });
    await cancelRegistration(row.id, patientProfileId);
    ElMessage.success('取消成功');
    await fetchList();
  } catch (err) {
    // 用户取消或错误，若需要可显示错误
  }
}

fetchList();
</script>

<style scoped>
.page { padding: 16px; }
.toolbar { display:flex; gap: 8px; align-items:center; margin-bottom: 12px; }
.empty { margin-top:24px; color:#909399; }
</style>

