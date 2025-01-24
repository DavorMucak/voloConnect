import axios from 'axios';

// Kreiraj instancu Axios-a
const apiClient = axios.create({
    baseURL: 'https://voloconnect.onrender.com', // Tvoj backend URL
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
