import http from './http';
import type { PatientDTO } from '@/views/patient/types';

export async function getPatientProfile(patientProfileId: number) {
  const { data } = await http.get<PatientDTO>(`/patient/profile/${patientProfileId}`);
  return data;
}

export async function getPatientProfileByUserId(userId: number) {
  const { data } = await http.get<PatientDTO>(`/patient/profile/by-user/${userId}`);
  return data;
}

// 后备：调用管理员查看患者详情的接口（项目中已有 admin/patients/{id}）
export async function getPatientProfileAdminFallback(patientProfileId: number) {
  const { data } = await http.get<PatientDTO>(`/admin/patients/${patientProfileId}`);
  return data;
}

export async function updatePatientProfile(patientProfileId: number, payload: Partial<PatientDTO>) {
  const { data } = await http.put<PatientDTO>(`/patient/profile/${patientProfileId}`, payload);
  return data;
}

export async function changePatientPassword(patientProfileId: number, newPassword: string) {
  await http.post(`/patient/profile/${patientProfileId}/change-password`, { newPassword });
}
