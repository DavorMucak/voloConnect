import { createRouter, createWebHistory } from "vue-router";

import Home from  '../components/Home.vue';
import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import NoviProjekt from "@/components/NoviProjekt.vue";
import Projekt from "@/components/Projekt.vue";
import Profil from "@/components/Profil.vue";
import MojiProjekti from "@/components/MojiProjekti.vue";
import Registracije from "@/components/Registracije.vue";
import Prituzbe from "@/components/Prituzbe.vue";


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
        props: true
    },
    {
        path: '/profil',
        name: 'Profil',
        component: Profil
    },
    {
        path: '/moji-projekti',
        name: 'MojiProjekti',
        component: MojiProjekti
    },
    {
        path: '/registracije',
        name: 'Registracije',
        component: Registracije
    },
    {
        path: '/prituzbe',
        name: 'Prituzbe',
        component: Prituzbe
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;