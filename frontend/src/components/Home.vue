<template>
    <div class="home-container">
      <h2>All Projects</h2>
      <div v-if="error" class="error">{{ error }}</div>
      <div v-if="projects.length">
        <ul>
          <li v-for="project in projects" :key="project.id">
            <h3>{{ project.imeProjekta }}</h3>
            <p>{{ project.opisProjekta }}</p>
            <p><strong>Broj ljudi:</strong> {{ project.brojLjudi }}</p>
            <p><strong>Datum početka:</strong> {{ project.datumPoc }}</p>
            <p><strong>Datum kraja:</strong> {{ project.datumKraj }}</p>
          </li>
        </ul>
      </div>
      <div v-else>
        <p>No projects available.</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        projects: [],
        error: null,
      };
    },
    async created() {
      try {
        const response = await axios.get('http://localhost:8080/api/projects');
        this.projects = response.data;
      } catch (error) {
        this.error = error.response ? error.response.data : 'Could not fetch projects';
      }
    },
  };
  </script>
  

  