import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('inventory_jwt');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const registerUser = (payload) => api.post('/auth/register', payload);
export const loginUser = (payload) => api.post('/auth/login', payload);

export const getAllItems = () => api.get('/items/all');
export const getLowStockItems = () => api.get('/items/low-stock');
export const addItem = (payload) => api.post('/item/add', payload);
export const updateStock = (id, payload) => api.put(`/item/update/${id}`, payload);
export const deleteItem = (id) => api.delete(`/item/${id}`);

export default api;
