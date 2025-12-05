import http from './http';
import type { Disease } from './patient-schedule';

export interface DoctorForPatient {
  id: number;
  doctorId: string;
  name: string;
  age?: number;
  gender?: string;
  title?: string;
  departmentId?: number;
  departmentName?: string;
  diseases?: Disease[];
}

export async function fetchDoctorsOverview(params?: { departmentId?: number; diseaseId?: number }) {
  const { data } = await http.get<DoctorForPatient[]>('/schedule/doctors', { params });
  return data;
}

