<template>
    <router-link to="/moji-projekti"> <button>Moji projekti</button> </router-link>
    <router-link v-if="isLoggedIn && korisnik.role === 'admin'" to="/registracije"> <button>Registracije</button> </router-link>
    <router-link v-if="isLoggedIn && korisnik.role === 'admin'" to="/prituzbe"> <button>Pritužbe</button> </router-link>
    <div v-if="isLoggedIn" class="user-details">
      <h2>Podaci o korisniku</h2>
      <p><strong>Korisničko ime:</strong> {{ korisnik.username }}</p>
      <p><strong>Email:</strong> {{ korisnik.email }}</p>
      <p><strong>Ime:</strong> {{ korisnik.name }}</p>
      <p><strong>Prezime:</strong> {{ korisnik.surname }}</p>
      <p><strong>Broj telefona:</strong> {{ korisnik.phone }}</p>
      <p><strong>Uloga:</strong> {{ korisnik.role }}</p>
    </div>
    <div v-else>
      <p>Molimo prijavite se kako biste vidjeli svoje podatke.</p>
    </div>
  </template>  
  
<script>
import { account } from '../appwrite'; 
import axios from 'axios';

export default {
  data() {
    return {
      isLoggedIn: false,
      korisnik: {
        username: '',
        password: '',
        email: '',
        name: '',
        surname: '',
        phonenum: '',
        role: ["volonter", "organizacija", "admin"],
      },
      account: account, // Instance of the Appwrite account object
    };
  },
  methods: {
    async fetchKorisnik() {
      try {
        const user = await this.account.get();
        this.isLoggedIn = true;

        this.korisnik.username = user.username;
        this.korisnik.password = user.password;
        this.korisnik.email = user.email;
        this.korisnik.name = user.name;
        this.korisnik.surname = user.surname;
        this.korisnik.phonenum = user.phonenum;
        this.korisnik.role = user.role;

        // If your backend also manages user details, fetch from backend:
        // const response = await axios.get('https://your-backend.com/api/user-details', {
        //   headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        // });
        // this.korisnik = response.data;
      } catch (error) {
        console.error('Error fetching user details:', error);
        this.isLoggedIn = false;
      }
    },
  },
  async created() {
    await this.fetchKorisnik();
  },
};
</script>

    
  
    