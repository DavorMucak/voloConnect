<template>
    <div>
      <div v-for="registracija in registracije" :key="registracija.username" class="registracija-box">
        <router-link :to="getProfileLink(registracija)">
          <p>{{ registracija.username }}</p>
        </router-link>
        <p>{{ registracija.name }} {{ registracija.surname || '' }}</p>
        <p>{{ registracija.role }}</p>
        <button @click="odobriRegistracija(registracija.username)">Odobri</button>
        <button @click="ponistiRegistracija(registracija.username)">Poništi</button>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        registracije: []
      };
    },
    created() {
      this.fetchRegistracije();
    },
    methods: {
      fetchRegistracije() {
        axios.get('http://localhost:8080/api/registracije')
          .then(response => {
            this.registracije = response.data;
          })
          .catch(error => {
            console.error('Error fetching registracije:', error);
          });
      },
      getProfileLink(registracija) {
        return registracija.role === 'volonteer' 
          ? `/volonteri/${registracija.username}` 
          : `/organizacije/${registracija.username}`;
      },
      odobriRegistracija(username) {
        axios.delete(`http://localhost:8080/api/registracije/${username}`)
          .then(() => {
            this.registracije = this.registracije.filter(reg => reg.username !== username);
          })
          .catch(error => {
            console.error('Error approving registracija:', error);
          });
      },
      ponistiRegistracija(username) {
        axios.delete(`http://localhost:8080/api/users/${username}`)
          .then(() => {
            this.registracije = this.registracije.filter(reg => reg.username !== username);
          })
          .catch(error => {
            console.error('Error deleting user:', error);
          });
      }
    }
  };
  </script>
  
  <style scoped>
  .registracija-box {
    border: 1px solid #ccc;
    padding: 10px;
    margin: 10px;
  }
  </style>