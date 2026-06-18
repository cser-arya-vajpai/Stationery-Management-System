import axiosInstance from './axiosConfig';

const BASE = 'http://localhost:8083'; // request-service port

export const submitRequest = (data) => {
  return axiosInstance.post(`${BASE}/api/requests`, data);
};

export const getMyRequests = (status) => {
  return axiosInstance.get(`${BASE}/api/requests/my`, {
    params: status ? { status } : {},
  });
};

export const getAllRequests = (status) => {
  return axiosInstance.get(`${BASE}/api/requests`, {
    params: status ? { status } : {},
  });
};

export const updateRequestStatus = (id, data) => {
  return axiosInstance.put(`${BASE}/api/requests/${id}/status`, data);
};