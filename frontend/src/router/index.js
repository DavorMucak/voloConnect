import { createRouter, createWebHistory } from "vue-router";

import Home from  '../components/Home.vue';
import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import NoviProjekt from "@/components/NoviProjekt.vue";
import Projekt from "@/components/Projekt.vue";


const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/novi-projekt',
        name: 'NoviProjekt',
        component: NoviProjekt
    },
    {
        path: '/projekt/:imeProjekta',
        name: 'Projekt',
        component: Projekt,
        props: true,
      },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;