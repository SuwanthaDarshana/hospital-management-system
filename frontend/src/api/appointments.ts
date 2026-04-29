import { apiClient } from './client';
import type { Appointment, AppointmentRequest, StandardResponse } from '../types';

export const bookAppointment = (data: AppointmentRequest) =>
  apiClient.post<StandardResponse<Appointment>>('/api/v1/appointments', data);

export const getAllAppointments = () =>
  apiClient.get<StandardResponse<Appointment[]>>('/api/v1/appointments');

export const getMyAppointments = () =>
  apiClient.get<StandardResponse<Appointment[]>>('/api/v1/appointments/my');

export const getAppointmentById = (id: number) =>
  apiClient.get<StandardResponse<Appointment>>(`/api/v1/appointments/${id}`);

export const getAppointmentsByDoctor = (doctorAuthUserId: number) =>
  apiClient.get<StandardResponse<Appointment[]>>(`/api/v1/appointments/doctor/${doctorAuthUserId}`);

export const getAppointmentsByPatient = (patientAuthUserId: number) =>
  apiClient.get<StandardResponse<Appointment[]>>(`/api/v1/appointments/patient/${patientAuthUserId}`);

export const updateAppointmentStatus = (id: number, status: string, notes?: string) =>
  apiClient.put<StandardResponse<Appointment>>(`/api/v1/appointments/${id}/status`, { status, notes });

export const cancelAppointment = (id: number) =>
  apiClient.delete(`/api/v1/appointments/${id}`);
