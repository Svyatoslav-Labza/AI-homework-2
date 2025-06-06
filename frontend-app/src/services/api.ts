import axios from 'axios';
import { User } from '../types';

const API_BASE_URL = 'https://jsonplaceholder.typicode.com';

export const api = {
  getUsers: async (): Promise<User[]> => {
    const response = await axios.get<User[]>(`${API_BASE_URL}/users`);
    return response.data;
  },

  getUser: async (id: number): Promise<User> => {
    const response = await axios.get<User>(`${API_BASE_URL}/users/${id}`);
    return response.data;
  }
}; 