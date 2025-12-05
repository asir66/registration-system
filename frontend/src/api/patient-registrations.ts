import http from './http';

export interface PatientRegistrationInfo {
  id: number;
  patientProfileId: number;
  doctorProfileId: number;
  doctorId: string;
  doctorName: string;
  doctorTitle?: string;
  diseaseId: number;
  diseaseName: string;
  departmentId?: number;
  departmentName?: string;
  weekday: number;
  timeslot: string;
  status: string;
  registrationTime?: string;
}

export async function listPatientRegistrations(patientProfileId: number) {
  const { data } = await http.get<PatientRegistrationInfo[]>(`/registration/patient/${patientProfileId}`);
  return data;
}

export async function cancelRegistration(registrationId: number, patientProfileId: number) {
  await http.delete(`/registration/${registrationId}?patientProfileId=${patientProfileId}`);
}

