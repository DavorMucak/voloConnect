import { createRouter, createWebHistory } from "vue-router";

import Home from '@/components/Home.vue';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import NoviProjekt from '@/components/za_profile/NoviProjekt.vue';
import Projekt from '@/components/Projekt.vue';
import Profil from '@/components/Profil.vue';
import ListaProjekata from '@/components/za_profile/ListaProjekata.vue';

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
        props:true
    },
    {
        path: '/profil/:username',
        name: 'Profil',
        component: Profil,
        props:true
    },
    {
        path: '/moji-projekti',
        name: 'ListaProjekata',
        component: ListaProjekata,
        props:true
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;