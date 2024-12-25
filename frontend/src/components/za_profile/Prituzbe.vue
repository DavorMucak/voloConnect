<template>
  <div>
    <h2>Pritužbe</h2>
    <div v-if="isAdmin">
      <div v-if="prituzbe.length">
        <div v-for="prituzba in prituzbe" :key="prituzba.id" class="prituzba">
          <p>
            <router-link :to="`/profil/${prituzba.senderUsername}`">{{ getUserName(prituzba.senderUsername) }}</router-link>
            podnio/la pritužbu protiv
            <router-link :to="`/profil/${prituzba.receiverUsername}`">{{ getUserName(prituzba.receiverUsername) }}</router-link>
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
  async created() {
    await this.checkAdmin(); // Provjera je li korisnik administrator
    if (this.isAdmin) {
      await this.fetchPrituzbe(); // Dohvati pritužbe ako je korisnik administrator
      await this.fetchUsers(); // Dohvati korisničke podatke
    }
  },
  methods: {
    async checkAdmin() {
      try {
        const token = localStorage.getItem('token'); // Dohvati token iz localStorage
        if (!token) {
          throw new Error('Nema tokena');
        }
        const response = await axios.get('http://localhost:8080/api/user', {
          headers: { Authorization: `Bearer ${token}` }
        }); // Dohvati podatke o trenutnom korisniku s API-ja
        this.isAdmin = response.data.role === 'admin'; // Provjeri je li korisnik administrator
      } catch (error) {
        console.error('Error checking admin status:', error); // Ispis pogreške ako provjera ne uspije
      }
    },
    async fetchPrituzbe() {
      try {
        const response = await axios.get('http://localhost:8080/api/prituzbe'); // Dohvati pritužbe s API-ja
        this.prituzbe = response.data; // Postavi dohvaćene pritužbe
      } catch (error) {
        console.error('Error fetching prituzbe:', error); // Ispis pogreške ako dohvaćanje pritužbi ne uspije
      }
    },
    async fetchUsers() {
      try {
        const response = await axios.get('http://localhost:8080/api/users'); // Dohvati korisničke podatke s API-ja
        this.users = response.data.reduce((acc, user) => {
          acc[user.username] = user; // Pohrani korisničke podatke prema korisničkom imenu
          return acc;
        }, {});
      } catch (error) {
        console.error('Error fetching users:', error); // Ispis pogreške ako dohvaćanje korisničkih podataka ne uspije
      }
    },
    getUserName(username) {
      const user = this.users[username];
      if (!user) return 'Unknown';
      return user.role === 'volonter' ? `${user.name} ${user.surname}` : user.name; // Vrati puno ime za volontera ili ime za organizaciju
    },
    async resolvePrituzba(id) {
      try {
        await axios.delete(`http://localhost:8080/api/prituzbe/${id}`); // Pošalji zahtjev za brisanje pritužbe
        this.prituzbe = this.prituzbe.filter(prituzba => prituzba.id !== id); // Ažuriraj lokalno stanje pritužbi
      } catch (error) {
        console.error('Error resolving prituzba:', error); // Ispis pogreške ako brisanje pritužbe ne uspije
      }
    }
  }
};
</script>

<style scoped>
.prituzba {
  margin-bottom: 1em;
  padding: 1em;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f6f4d2;
}

button {
  margin-top: 10px;
  padding: 10px;
  background-color: #4c8139;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #3a6b2d;
}
</style>