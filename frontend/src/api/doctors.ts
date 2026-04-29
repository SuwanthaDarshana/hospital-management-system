import { apiClient } from './client';
import type { Doctor, DoctorUpdateRequest, StandardResponse } from '../types';

export const getAllDoctors = () =>
  apiClient.get<StandardResponse<Doctor[]>>('/api/v1/doctors');

export const getDoctorByAuthUserId = (authUserId: number) =>
  apiClient.get<StandardResponse<Doctor>>(`/api/v1/doctors/${authUserId}`);

export const searchDoctors = (specialization: string) =>
  apiClient.get<StandardResponse<Doctor[]>>(`/api/v1/doctors/search?specialization=${encodeURIComponent(specialization)}`);

export const updateDoctor = (authUserId: number, data: DoctorUpdateRequest) =>
  apiClient.put<StandardResponse<Doctor>>(`/api/v1/doctors/${authUserId}`, data);
