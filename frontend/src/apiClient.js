import axios from 'axios';

// Kreiraj instancu Axios-a
const apiClient = axios.create({
    baseURL: 'http://localhost:8080', // Tvoj backend URL
});

// Dodaj interceptor za svaki zahtjev
apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    console.log(token);
    
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default apiClient;
