<template>
  <div>
    <h2>Pritužbe</h2>
      <div v-if="prituzbe.length">
        <div v-for="complaint in complaints" :key="prituzba.id" class="prituzba">
          <p>
            <router-link :to="`/profil/${encodeURIComponent(complaint.plaintiffUsername)}`">{{ complaint.plaintiffUsername }}</router-link>
            podnio/la pritužbu protiv
            <router-link :to="`/profil/${encodeURIComponent(complaint.defendantUsername)}`">{{ complaint.defendantUsername }}</router-link>
          </p>
          
          <p>{{ prituzba.description }}</p>
          <button @click="resolveComplaint(complaint.id)">Razriješeno</button> <!-- briše pritužbu -->
        </div>
      </div>
      <p v-else>Nema pritužbi.</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Prituzbe',
  data() {
    return {
      complaints: [], // prituzbe korisnika
    };
  },
  created() {
    this.fetchComplaints();
  },
  methods: {
    fetchComplaints() { // dohvaca prituzbe
      axios.get(`http://localhost:8080/api/prituzbe`)
        .then(response => {
          this.complaints = response.data;
        })
        .catch(error => {
          console.error('Error fetching complaints:', error);
        });
    },
    resolveComplaint(id) { // brise prituzbu kad je razrijesena
      axios.delete(`http://localhost:8080/api/prituzbe/${id}`)
        .then(() => {
          this.complaints = this.complaints.filter(complaint => complaint.id !== id);
        })
        .catch(error => {
          console.error('Error resolving complaint:', error);
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