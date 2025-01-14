import './assets/styles/base.css';
import './assets/styles/main.css';

import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

createApp(App).use(router).mount('#app');