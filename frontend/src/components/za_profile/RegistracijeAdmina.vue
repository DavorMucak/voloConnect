<template>
  <div class="new-users">
    <h2>Nepodobreni admini</h2>
    <div v-if="adminsWaiting.length">
      <ul>
        <li v-for="korisnik in this.adminsWaiting">
          <h3></h3>
          <p><strong></strong> korisničko ime: {{ korisnik.username }}</p>
          <p><strong></strong> email: {{ korisnik.email }}</p>
          <p><strong></strong> ime: {{ korisnik.name }}</p>
          <p><strong></strong> prezime: {{ korisnik.surname }}</p>
          <button @click="odobriAdmina(korisnik.username)">ODOBRI ADMINA</button>
          <button @click="izbrisiAdminZahtjev(korisnik.username)">IZBRIŠI ZAHTJEV</button>
        </li>
      </ul>
    </div>
    <div v-else>
      <p>Nema korisnika koji čekaju validaciju.</p>
    </div>
  </div>



</template>

<script>
import axios from 'axios';
import apiClient from '@/apiClient';

export default {
  data() {
    return {
      korisnik: {
        username: '',
        password: '',
        email: '',
        name: '',
        surname: '',
        phonenum: '',
        role: ["volonter", "organizacija", "admin"],
      },
      adminsWaiting: []
    };
  },
  methods: {
    fetchNewUsers(){
      this.adminsWaiting = [];
    },
    async odobriAdmina(username) {
      //const token = localStorage.getItem('token');
      console.log("Započinjem odobravanje admina.");
      const response = await axios.post(`http://localhost:8080/api/user/approve-admins/${username}`);
      console.log(response.data);
    },
    async izbrisiAdminZahtjev(username) {
      console.log("Započinjem brisanje admina.");
      const response = await axios.delete(`http://localhost:8080/api/user/approve-admins/${username}`);
      console.log(response.data);
    }
  },

  async created() {
    try {

      //dohvat liste nevalidiranih admina s backenda
      const response = await apiClient.get(`http://localhost:8080/api/user/approve-admins`);
      this.adminsWaiting = response.data;
    } catch (error) {
      this.error = error.response ? error.response.data : 'Ne mogu se dohvatiti korisnici.';
    }
  },
};
</script>

