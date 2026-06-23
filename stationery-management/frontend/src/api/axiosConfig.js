import axios from 'axios';
//this file is a security guard for all apis
// Remove baseURL — each api file will use its own base
const axiosInstance = axios.create();  //creates a customised instance of axios named axiosInstance so that we can customize it with special filters

//config variable contains all details about the outgoing request (like the URL and method).
axiosInstance.interceptors.request.use((config) => {  //means, before any API request following code will run automatically
  const token = localStorage.getItem('token');       //saves the JWT token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;  //add token to header
  }
  return config;    //sends modified request to backend
});

//response interceptor, runs after backend sends a response
axiosInstance.interceptors.response.use(
  (response) => response,  //if request succeeds just return the response
  (error) => {
    if (error.response && error.response.status === 401) {   //401 means unauthorized
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      localStorage.removeItem('email');
      window.location.href = '/login';
    }
    return Promise.reject(error);   //passes error back to code that made the request
  }
);

export default axiosInstance;