import { apiClient } from './client';
import type { StandardResponse, Payment, PaymentRequest, PaymentConfirmRequest } from '../types';

export const getAllPayments = () =>
  apiClient.get<StandardResponse<Payment[]>>('/api/v1/payments');

export const createPaymentIntent = (data: PaymentRequest) =>
  apiClient.post<StandardResponse<Payment>>('/api/v1/payments/create-intent', data);

export const confirmPayment = (data: PaymentConfirmRequest) =>
  apiClient.post<StandardResponse<Payment>>('/api/v1/payments/confirm', data);

export const refundPayment = (id: number) =>
  apiClient.post<StandardResponse<Payment>>(`/api/v1/payments/${id}/refund`);

export const getPaymentsByPatient = (patientId: number) =>
  apiClient.get<StandardResponse<Payment[]>>(`/api/v1/payments/patient/${patientId}`);

export const getPaymentsByAppointment = (appointmentId: number) =>
  apiClient.get<StandardResponse<Payment[]>>(`/api/v1/payments/appointment/${appointmentId}`);
