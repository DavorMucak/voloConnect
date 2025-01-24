import { createRouter, createWebHistory } from "vue-router";

import Home from '@/components/Home.vue';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import NoviProjekt from '@/components/za_profile/NoviProjekt.vue';
import Projekt from '@/components/Projekt.vue';
import Profil from '@/components/Profil.vue';
import Biljeske from '@/components/za_profile/Biljeske.vue';
import ListaProjekata from '@/components/za_profile/ListaProjekata.vue';
import Prituzbe from '@/components/za_profile/Prituzbe.vue';
import Recenzije from '@/components/za_profile/Recenzije.vue';

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
        path: '/projekt/:projektId',
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
        path: '/moji-projekti/:username',
        name: 'ListaProjekata',
        component: ListaProjekata,
        props:true
    }
    // !!!popravit rute
    /*   {
        path: '/volonteri/:username',
        component: Profil,
        children: [
          {
            path: '',
            name: 'Profil',
            component: Profil
          },
          {
            path: 'biljeske',
            name: 'Biljeske',
            component: Biljeske
          },
          {
            path: 'projekti',
            name: 'ListaProjekata',
            component: ListaProjekata
          },
          {
            path: 'recenzije',
            name: 'Recenzije',
            component: Recenzije
          },
          {
            path: 'projekt/:imeProjekta',
            name: 'ProfilProjekt',
            component: Projekt
          }
        ]
      },
      {
        path: '/organizacije/:username',
        component: Profil,
        children: [
          {
            path: '',
            name: 'Profil',
            component: Profil
          },
          {
            path: 'projekti',
            name: 'ListaProjekata',
            component: ListaProjekata
          },
          {
            path: 'novi-projekt',
            name: 'ProfilNoviProjekt',
            component: NoviProjekt
          },
          {
            path: 'recenzije',
            name: 'Recenzije',
            component: Recenzije
          },
          {
            path: 'projekt/:imeProjekta',
            name: 'ProfilProjekt',
            component: Projekt
          }
        ]
      },
      {
        path: '/admini/:username',
        component: Profil,
        children: [
          {
            path: '',
            name: 'Profil',
            component: Profil
          },
          {
            path: 'prituzbe',
            name: 'Prituzbe',
            component: Prituzbe
          },
          {
            path: 'registracije',
            name: 'Registracije',
            component: Registracije
          }
        ]
      } */
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;