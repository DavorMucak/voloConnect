<template>
  <div>
    <h2>Pritužbe</h2>
    <div v-if="isAdmin">
      <div v-if="prituzbe.length">
        <div v-for="prituzba in prituzbe" :key="prituzba.id" class="prituzba">
          <p>
            <router-link :to="getProfileLink(prituzba.senderUsername)">{{ getUserName(prituzba.senderUsername) }}</router-link>
            podnio/la pritužbu protiv
            <router-link :to="getProfileLink(prituzba.receiverUsername)">{{ getUserName(prituzba.receiverUsername) }}</router-link>
          </p>
          <p>{{ prituzba.description }}</p>
          <button @click="resolvePrituzba(prituzba.id)">Razriješeno</button>
        </div>
      </div>
      <div v-else>
        <p>Nema pritužbi.</p>
      </div>
    </div>
    <div v-else>
      <p>Nemate ovlasti za pregled pritužbi.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Prituzbe',
  data() {
    return {
      isAdmin: false, // Provjera je li korisnik administrator
      prituzbe: [], // Polje za pohranu pritužbi
      users: {} // Objekt za pohranu korisničkih podataka
    };
  },
  created() {
    this.checkAdmin();
    this.fetchPrituzbe();
  },
  methods: {
    checkAdmin() {
      const token = localStorage.getItem('token');
      if (token) {
        const user = JSON.parse(atob(token.split('.')[1]));
        this.isAdmin = user.role === 'admin';
      }
    },
    fetchPrituzbe() {
      axios.get('/api/prituzbe')
        .then(response => {
          this.prituzbe = response.data;
          this.fetchUsers();
        })
        .catch(error => {
          console.error('Error fetching pritužbe:', error);
        });
    },
    fetchUsers() {
      const usernames = new Set();
      this.prituzbe.forEach(prituzba => {
        usernames.add(prituzba.senderUsername);
        usernames.add(prituzba.receiverUsername);
      });
      usernames.forEach(username => {
        axios.get(`/api/users/${username}`)
          .then(response => {
            this.$set(this.users, username, response.data);
          })
          .catch(error => {
            console.error('Error fetching user:', error);
          });
      });
    },
    getUserName(username) {
      return this.users[username] ? this.users[username].name : username;
    },
    getProfileLink(username) {
      if (this.users[username]) {
        const role = this.users[username].role;
        return role === 'volunteer' ? `/volonteri/${username}` : `/organizacije/${username}`;
      }
      return `/profil/${username}`;
    },
    resolvePrituzba(id) {
      axios.delete(`/api/prituzbe/${id}`)
        .then(() => {
          this.prituzbe = this.prituzbe.filter(prituzba => prituzba.id !== id);
        })
        .catch(error => {
          console.error('Error resolving pritužba:', error);
        });
    }
  }
};
</script>

<style scoped>
.prituzba {
  border: 1px solid #ccc;
  padding: 10px;
  margin: 10px;
}
</style>