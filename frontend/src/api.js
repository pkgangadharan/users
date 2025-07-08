import axios from 'axios';

const instance = axios.create({
  baseURL: '/', // Adjust if backend is hosted separately
  headers: {
    'Content-Type': 'application/json'
  }
});

export default instance;
