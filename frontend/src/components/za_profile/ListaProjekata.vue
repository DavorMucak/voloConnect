<template>
    <div>
      <router-link v-if="showObjaviProjektButton" to="/za_profile/NoviProjekt">
        <button>Objavi projekt</button>
      </router-link>
      <div v-for="project in projects" :key="project.imeProjekta" class="project-box">
        <router-link :to="`/projects/${encodeURIComponent(project.imeProjekta)}`">
          <p>{{ project.imeProjekta }}</p>
        </router-link>
        <div v-if="isViewingVolunteerProjects">
          <p>Organizacija: {{ project.name }} ({{ project.username }})</p>
        </div>
        <p>Datum početka: {{ project.datumPoc }}</p>
        <p>Datum kraja: {{ project.datumKraj }}</p>
        <p>Broj ljudi: {{ project.brojLjudi }}</p>
        <p v-if="project.jeLiHitno">Hitno</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    props: {
      username: {
        type: String,
        required: true
      }
    },
    data() {
      return {
        projects: [],
        showObjaviProjektButton: false,
        isViewingVolunteerProjects: false
      };
    },
    created() {
      this.fetchProjects();
      this.checkUserRole();
    },
    methods: {
      fetchProjects() {
        axios.get(`http://localhost:8080/projects/${this.username}`)
          .then(response => {
            this.projects = response.data;
          })
          .catch(error => {
            console.error('Error fetching projects:', error);
          });
      },
      checkUserRole() {
        const token = localStorage.getItem('token');
        if (token) {
          const user = JSON.parse(atob(token.split('.')[1]));
          if (user.role === 'organization' && user.username === this.username) {
            this.showObjaviProjektButton = true;
          } else if (user.role === 'volunteer' && user.username === this.username) {
            this.isViewingVolunteerProjects = true;
          }
        }
      }
    }
  };
  </script>
  
  <style scoped>
  .project-box {
    border: 1px solid #ccc;
    padding: 10px;
    margin: 10px;
  }
  </style>