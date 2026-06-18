import axiosInstance from './axiosConfig';

const BASE = 'http://localhost:8081'; // auth-service port

export const registerUser = (data) => {
  return axiosInstance.post(`${BASE}/api/auth/register`, data);
};

export const loginUser = (data) => {
  return axiosInstance.post(`${BASE}/api/auth/login`, data);
};