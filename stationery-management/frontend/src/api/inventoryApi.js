import axiosInstance from './axiosConfig';

const BASE = 'http://localhost:8082'; // inventory-service port

export const getAllItems = (page = 0, size = 20, sortBy = 'name') => {
  return axiosInstance.get(`${BASE}/api/inventory`, {
    params: { page, size, sortBy },
  });
};

export const getItemById = (id) => {
  return axiosInstance.get(`${BASE}/api/inventory/${id}`);
};

export const getLowStockItems = () => {
  return axiosInstance.get(`${BASE}/api/inventory/low-stock`);
};

export const addItem = (data) => {
  return axiosInstance.post(`${BASE}/api/inventory`, data);
};

export const updateItem = (id, data) => {
  return axiosInstance.put(`${BASE}/api/inventory/${id}`, data);
};

export const deleteItem = (id) => {
  return axiosInstance.delete(`${BASE}/api/inventory/${id}`);
};