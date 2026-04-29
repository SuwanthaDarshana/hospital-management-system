import { apiClient } from './client';
import type {
  LoginRequest, RegisterPatientRequest, RegisterDoctorRequest,
  RegisterStaffRequest, StandardResponse, AuthTokens,
} from '../types';

export const login = (data: LoginRequest) =>
  apiClient.post<StandardResponse<AuthTokens & { role: string; email: string; id: string }>>('/api/v1/auth/login', data);

export const registerPatient = (data: RegisterPatientRequest) =>
  apiClient.post('/api/v1/auth/register/patient', data);

export const registerDoctor = (data: RegisterDoctorRequest) =>
  apiClient.post('/api/v1/auth/register/doctor', data);

export const registerStaff = (data: RegisterStaffRequest) =>
  apiClient.post('/api/v1/auth/register/staff', data);

export const logout = (refreshToken: string) =>
  apiClient.post('/api/v1/auth/logout', { refreshToken });
