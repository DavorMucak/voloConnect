<template>
  <div class="home-container">
    <h2>Projekti</h2>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="projects.length">
      <ul>
        <li v-for="project in projects" :key="project.id">
          <router-link :to="{ name: 'Projekt', params: { imeProjekta: project.imeProjekta } }">
            <h3>{{ project.imeProjekta }}</h3>
            <p>{{ project.opisProjekta }}</p>
            <p><strong>Broj ljudi:</strong> {{ project.brojLjudi }}</p>
            <p><strong>Datum početka:</strong> {{ project.datumPoc }}</p>
            <p><strong>Datum kraja:</strong> {{ project.datumKraj }}</p>
          </router-link>
        </li>
      </ul>
    </div>
    <div v-else>
      <p>Nema projekata.</p>
    </div>
  </div>
</template>
  
<script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        projects: [
        {
          id:1,
          imeProjekta: 'Izrada Web Aplikacije',
          opisProjekta: 'Razvoj interaktivne web aplikacije koristeći Vue.js.',
          brojLjudi: 5,
          datumPoc: '2024-01-15',
          datumKraj: '2024-03-15',
        },
        {
          id:2,
          imeProjekta: 'Analiza Podataka',
          opisProjekta: 'Projekt fokusiran na analizu podataka koristeći Python i Pandas.',
          brojLjudi: 3,
          datumPoc: '2024-02-01',
          datumKraj: '2024-04-01',
        },
        {
          id:3,
          imeProjekta: 'Mobilna Aplikacija',
          opisProjekta: 'Razvoj mobilne aplikacije za Android i iOS platforme.',
          brojLjudi: 6,
          datumPoc: '2024-03-10',
          datumKraj: '2024-06-10',
        },
      ],
        error: null,
      };
    },
    async created() {
      try {
        const response = await axios.get('https://voloconnect.onrender.com/api/projects');
        this.projects = response.data;
      } catch (error) {
        this.error = error.response ? error.response.data : 'Ne mogu se dohvatiti projekti.';
      }
    }, 
  };
</script>
  

  