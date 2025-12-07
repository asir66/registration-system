<template>
  <div class="page">
    <el-card class="card" shadow="hover">
      <h2 class="title">我的信息</h2>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="身份证" prop="idCard">
              <el-input v-model="form.idCard" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="手机号" prop="phoneNumber">
              <el-input v-model="form.phoneNumber" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="0" :max="150" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio-button label="male">male</el-radio-button>
                <el-radio-button label="female">female</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="actions">
          <el-button type="primary" :loading="saving" @click="onSave">保存修改</el-button>
        </div>
      </el-form>

      <el-divider class="my-12" />

      <h3>修改密码</h3>
      <el-form :model="pwdForm" ref="pwdRef" label-position="top">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <div class="actions">
          <el-button type="primary" :loading="changingPwd" @click="onChangePwd">修改密码</el-button>
        </div>
      </el-form>

    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { useAuthStore } from '@/stores/auth';
import { getPatientProfile, updatePatientProfile, changePatientPassword, getPatientProfileByUserId, getPatientProfileAdminFallback } from '@/api/patient-profile';
import type { PatientDTO } from './types';

const auth = useAuthStore();
const formRef = ref<FormInstance>();
const pwdRef = ref<FormInstance>();
const saving = ref(false);
const changingPwd = ref(false);

const form = reactive<PatientDTO>({
  id: 0,
  username: '',
  idCard: '',
  name: '',
  phoneNumber: '',
  age: undefined,
  gender: 'male',
});

const pwdForm = reactive({ newPassword: '' });

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  phoneNumber: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
};

onMounted(async () => {
  auth.restore();
  if (!auth.user) {
    ElMessage.error('未检测到登录信息');
    return;
  }

  try {
    let data;
    if (auth.user.role === 'PATIENT' && auth.user.patientId) {
      try {
        data = await getPatientProfile(auth.user.patientId);
      } catch (e: any) {
        // 如果按 patientId 拉取失败，尝试用 userId 回退
        if (auth.user.userId) {
          try {
            data = await getPatientProfileByUserId(auth.user.userId);
          } catch (e2: any) {
            // 回退：尝试管理员接口（只读展示）
            try {
              data = await getPatientProfileAdminFallback(auth.user.patientId);
            } catch (e3: any) {
              throw e3 || e2 || e;
            }
          }
        } else {
          throw e;
        }
      }
    } else if (auth.user.userId) {
      // 回退：使用 userId 去查询患者档案
      try {
        data = await getPatientProfileByUserId(auth.user.userId);
      } catch (e: any) {
        // 再回退到管理员接口（需要 patientId）
        if (auth.user.patientId) data = await getPatientProfileAdminFallback(auth.user.patientId);
        else throw e;
      }
    } else {
      ElMessage.error('未检测到患者登录信息');
      return;
    }
    Object.assign(form, data);
  } catch (err: any) {
    ElMessage.error(err?.response?.data?.message || '获取个人信息失败');
  }
});

async function onSave() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  saving.value = true;
  try {
    const payload: Partial<PatientDTO> = {
      username: form.username,
      name: form.name,
      idCard: form.idCard,
      phoneNumber: form.phoneNumber,
      age: form.age,
      gender: form.gender,
    };
    const res = await updatePatientProfile(form.id!, payload);
    Object.assign(form, res);
    ElMessage.success('保存成功');
  } catch (err: any) {
    ElMessage.error(err?.response?.data?.message || '保存失败');
  } finally {
    saving.value = false;
  }
}

async function onChangePwd() {
  const valid = await pwdRef.value?.validate().catch(() => false);
  if (!valid) return;
  const profileId = form.id || auth.user?.patientId;
  if (!profileId) { ElMessage.error('未检测到患者档案ID，无法修改密码'); return; }
  changingPwd.value = true;
  const newPwd = pwdForm.newPassword;
  try {
    await changePatientPassword(profileId, newPwd);
    // 显示 Modal/弹窗，明确展示用户修改后的新密码，用户点击确认后才跳转登录
    await ElMessageBox.alert(`新密码：${newPwd}`, '修改密码成功', {
      confirmButtonText: '去登录',
      closeOnClickModal: false,
      closeOnPressEscape: false,
    });

    // 清空前端登录态并跳转到登录页面
    auth.setUser(null);
    window.location.href = '/login';
  } catch (err: any) {
    ElMessage.error(err?.response?.data?.message || '修改密码失败');
  } finally {
    changingPwd.value = false;
  }
}
</script>

<style scoped>
.page { padding: 24px; display:flex; justify-content:center; background:#f8fafc; }
.card { width:100%; max-width:760px }
.title { margin:0 0 6px }
.actions { margin-top: 12px }
.my-12 { margin-top: 12px }
</style>
