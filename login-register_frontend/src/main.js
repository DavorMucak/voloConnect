import './assets/styles/base.css';
import './assets/styles/main.css';

//import {account} from './appwrite'  //appwrite dio (potrebno za oauth)

import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

createApp(App).use(router).mount('#app');


