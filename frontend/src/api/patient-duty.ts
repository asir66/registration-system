import http from './http';

export interface PatientDutyItem {
  id: number;
  departmentName?: string;
  departmentId?: number;
  doctorId?: number;
  doctorName?: string;
  doctorTitle?: string;
  weekendType?: number;
  dutyTimeslot?: string;
  createdTime?: string;
  updatedTime?: string;
}

export async function listDutySchedules(departmentId?: number) {
  if (departmentId != null) {
    const url = `/patient/duty-schedules?departmentId=${departmentId}`;
    const { data } = await http.get<PatientDutyItem[]>(url);
    return data;
  }
  // call explicit /all endpoint when no department filter to avoid 404 on root mapping in some deployments
  const { data } = await http.get<PatientDutyItem[]>(`/patient/duty-schedules/all`);
  return data;
}

export async function listDutySchedulesAll() {
  const { data } = await http.get<PatientDutyItem[]>(`/patient/duty-schedules/all`);
  return data;
}
