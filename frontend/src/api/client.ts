import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const API_BASE = 'http://localhost:8080';

export const apiClient = axios.create({ baseURL: API_BASE });

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().tokens?.accessToken;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

apiClient.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      const refresh = useAuthStore.getState().tokens?.refreshToken;
      if (refresh) {
        try {
          const { data } = await axios.post(`${API_BASE}/api/v1/auth/refresh`, { refreshToken: refresh });
          const newTokens = { accessToken: data.data.accessToken, refreshToken: data.data.refreshToken };
          useAuthStore.getState().setTokens(newTokens);
          original.headers.Authorization = `Bearer ${newTokens.accessToken}`;
          return apiClient(original);
        } catch {
          useAuthStore.getState().logout();
        }
      }
    }
    return Promise.reject(error);
  }
);
