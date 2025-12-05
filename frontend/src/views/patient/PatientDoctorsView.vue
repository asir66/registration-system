<template>
  <div class="page">
    <div class="filters">
      <el-select v-model="selected.departmentId" placeholder="全部科室" clearable style="width:220px" @change="loadDoctors">
        <el-option :key="0" :label="'全部科室'" :value="null" />
        <el-option v-for="d in departments" :key="d.id" :label="d.departmentName" :value="d.id" />
      </el-select>

      <el-select v-model="selected.diseaseId" placeholder="按疾病筛选" clearable style="width:220px" @change="loadDoctors">
        <el-option v-for="dis in diseases" :key="dis.id" :label="dis.name" :value="dis.id" />
      </el-select>

      <el-button type="primary" @click="loadDoctors">刷新</el-button>
    </div>

    <el-table :data="doctors" stripe style="width:100%" v-loading="loading">
      <el-table-column prop="doctorId" label="工号" width="120" />
      <el-table-column prop="name" label="姓名" width="140" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="gender" label="性别" width="100" />
      <el-table-column prop="title" label="职称" width="140" />
      <el-table-column prop="departmentName" label="所属科室" width="160" />
      <el-table-column label="可诊疾病">
        <template #default="{ row }">
          <el-tag v-for="dis in row.diseases" :key="dis.id" class="disease-tag">{{ dis.name }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="!loading && doctors.length === 0" class="empty">暂无医生信息</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { fetchDoctorsOverview, type DoctorForPatient } from '@/api/patient-doctors';
import { fetchDepartments, fetchDiseasesByDepartment } from '@/api/patient-schedule';
import { ElMessage } from 'element-plus';

const doctors = ref<DoctorForPatient[]>([]);
const departments = ref<{ id: number; departmentName: string }[]>([]);
const diseases = ref<{ id: number; name: string }[]>([]);
const loading = ref(false);

const selected = ref<{ departmentId?: number | null; diseaseId?: number | null }>({ departmentId: null, diseaseId: null });

async function loadDepartments() {
  try {
    departments.value = await fetchDepartments();
  } catch (e) {
    // ignore
  }
}

async function loadDiseasesForDept(deptId?: number | null) {
  if (!deptId) {
    diseases.value = [];
    return;
  }
  try {
    diseases.value = await fetchDiseasesByDepartment(deptId);
  } catch (e) {
    diseases.value = [];
  }
}

async function loadDoctors() {
  loading.value = true;
  try {
    const params: any = {};
    if (selected.value.departmentId) params.departmentId = selected.value.departmentId;
    if (selected.value.diseaseId) params.diseaseId = selected.value.diseaseId;
    doctors.value = await fetchDoctorsOverview(params);
  } catch (e) {
    ElMessage.error('获取医生信息失败');
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  await loadDepartments();
  await loadDoctors();
});

// watch department change to refresh disease list
watch(
  () => selected.value.departmentId,
  (val) => {
    loadDiseasesForDept(val as number | null);
  },
);
</script>

<style scoped>
.page { padding: 16px; }
.filters { display:flex; gap:12px; align-items:center; margin-bottom:12px; }
.disease-tag { margin: 4px; }
.empty { color: #909399; margin-top: 12px; }
</style>
