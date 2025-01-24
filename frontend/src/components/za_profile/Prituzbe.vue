<template>
  <div>
    <h2>Pritužbe</h2>
      <div v-if="complaints.length">
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
import apiClient from '@/apiClient';

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
      apiClient.get(`https://voloconnect.onrender.com/api/prituzbe`)
        .then(response => {
          this.complaints = response.data;
        })
        .catch(error => {
          console.error('Error fetching complaints:', error);
        });
    },
    resolveComplaint(id) { // brise prituzbu kad je razrijesena
      apiClient.delete(`https://voloconnect.onrender.com/api/prituzbe/${id}`)
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