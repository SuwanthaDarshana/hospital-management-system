export type Role = 'ADMIN' | 'DOCTOR' | 'PATIENT' | 'STAFF';

export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface User {
  email: string;
  role: Role;
  authUserId: number;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterPatientRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
  gender?: string;
  dateOfBirth?: string;
  bloodGroup?: string;
}

export interface RegisterDoctorRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  specialization: string;
}

export interface RegisterStaffRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: 'STAFF';
  department: string;
  address?: string;
  gender?: string;
  dateOfBirth?: string;
  bloodGroup?: string;
}

// matches DoctorResponseDTO exactly
export interface Doctor {
  id: number;
  authUserId: number;
  firstName: string;
  lastName: string;
  specialization: string;
  email: string;
  phone: string;
  availability?: string;
}

// fields a doctor can update (PUT /api/v1/doctors/{authUserId})
export interface DoctorUpdateRequest {
  // admin only
  firstName?: string;
  lastName?: string;
  email?: string;
  role?: string;
  // doctor + admin
  phone?: string;
  specialization?: string;
  availability?: string;
  password?: string;
}

export interface Patient {
  id: number;
  authUserId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address?: string;
  gender?: string;
  dateOfBirth?: string;   // LocalDate comes as string "yyyy-MM-dd"
  bloodGroup?: string;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// Matches PatientRequestDTO — required fields enforced by backend
export interface PatientUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  gender: string;
  dateOfBirth: string;   // "yyyy-MM-dd"
  bloodGroup?: string;
  password?: string;
  isActive: boolean;
}

// matches StaffResponseDTO exactly
export interface Staff {
  id: number;
  authUserId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  department: string;
  role: string;
  address?: string;
  gender?: string;
  dateOfBirth?: string;
  bloodGroup?: string;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// matches StaffRequestDTO — all required fields must be present
export interface StaffUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  department?: string;
  role?: string;
  isActive?: boolean;
  address: string;
  gender: string;
  dateOfBirth: string;
  bloodGroup?: string;
}

export interface Appointment {
  id: number;
  patientAuthUserId: number;
  patientEmail: string;
  patientName: string;
  doctorAuthUserId: number;
  doctorName: string;
  doctorSpecialization: string;
  appointmentDate: string;
  appointmentTime: string;
  status: AppointmentStatus;
  reason?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AppointmentRequest {
  patientAuthUserId: number;
  patientName: string;
  doctorAuthUserId: number;
  doctorName: string;
  doctorSpecialization?: string;
  appointmentDate: string;
  appointmentTime: string;
  reason?: string;
}

export interface StandardResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
