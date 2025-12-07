export interface PatientDTO {
  id: number;
  userId?: number;
  username?: string;
  idCard?: string;
  name?: string;
  phoneNumber?: string;
  age?: number | null;
  gender?: 'male' | 'female';
  isActive?: boolean;
}

