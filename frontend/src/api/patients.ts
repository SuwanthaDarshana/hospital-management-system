import { apiClient } from './client';
import type { Patient, PatientUpdateRequest, StandardResponse } from '../types';

export const getAllPatients = () =>
  apiClient.get<StandardResponse<Patient[]>>('/api/v1/patients');

export const getPatientByAuthUserId = (authUserId: number) =>
  apiClient.get<StandardResponse<Patient>>(`/api/v1/patients/${authUserId}`);

export const updatePatient = (authUserId: number, data: PatientUpdateRequest) =>
  apiClient.put<StandardResponse<Patient>>(`/api/v1/patients/${authUserId}`, data);

export const deletePatient = (authUserId: number) =>
  apiClient.delete(`/api/v1/patients/${authUserId}`);

export const activatePatient = (authUserId: number) =>
  apiClient.patch(`/api/v1/patients/${authUserId}/activate`);

export const searchPatients = (params: Record<string, string>) =>
  apiClient.get<StandardResponse<Patient[]>>('/api/v1/patients/dynamic-search', { params });
