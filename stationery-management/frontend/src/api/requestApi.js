import axiosInstance from './axiosConfig'; //using this import and not the axios, now every function in this file automatically has the JWT bearer token attached to its request header

const BASE = 'http://localhost:8083'; //defines network url base address where request-service ms is running

//it receives stationery items requests in a variable (data). 
//Then, it makes HTTP POST request to the below url
export const submitRequest = (data) => {
  return axiosInstance.post(`${BASE}/api/requests`, data);
};

//this is for my requests page 
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

//admin-side update request status
export const updateRequestStatus = (id, data) => {
  return axiosInstance.put(`${BASE}/api/requests/${id}/status`, data);
};