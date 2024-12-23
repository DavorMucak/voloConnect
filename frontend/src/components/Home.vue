<template>
  <div class="home-container">
    <h2>Projekti</h2>
    <div class="filteri">
      <label for="datumPocetka">Datum početka:</label>
      <input type="date" id="datumPocetka" v-model="filterDatumPocetka" />
      <label for="datumKraja">Datum kraja:</label>
      <input type="date" id="datumKraja" v-model="filterDatumKraja" />
      <label>Vrsta aktivnosti</label>
      <select id="vrstaAktivnosti" v-model="filterVrstaAktivnosti">
        <option value="">Sve</option>
        <option value="Administrativni poslovi">Administrativni poslovi</option>
        <option value="Fizički poslovi">Fizički poslovi</option>
        <option value="Podučavanje">Podučavanje</option>
        <option value="Kreativni poslovi">Kreativni poslovi</option>
        <option value="Informatičke usluge">Informatičke usluge</option>
        <option value="Ostalo">Ostalo</option>
      </select>
      <button @click="filtrirano">Filtriraj projekte</button>
    </div>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="filtriraniProjekti.length">
      <ul>
        <li v-for="project in filtriraniProjekti" :key="project.id">
          <router-link :to="{ name: 'Projekt', params: { imeProjekta: project.imeProjekta } }">
            <h3> {{formatiranoIme(project.imeProjekta)}}</h3>
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
            imeProjekta: 'izrada-web-aplikacije',
            opisProjekta: 'Razvoj interaktivne web aplikacije koristeći Vue.js.',
            brojLjudi: 5,
            datumPoc: '2024-01-15',
            datumKraj: '2024-03-15',
            vrstaAktivnosti: "Fizički poslovi"
          },
          {
            id:2,
            imeProjekta: 'analiza-podataka',
            opisProjekta: 'Projekt fokusiran na analizu podataka koristeći Python i Pandas.',
            brojLjudi: 3,
            datumPoc: '2024-02-01',
            datumKraj: '2024-04-01',
            vrstaAktivnosti: "Administrativni poslovi"
          },
          {
            id:3,
            imeProjekta: 'mobilna-aplikacija',
            opisProjekta: 'Razvoj mobilne aplikacije za Android i iOS platforme.',
            brojLjudi: 6,
            datumPoc: '2024-03-10',
            datumKraj: '2024-06-10',
            vrstaAktivnosti: "Podučavanje"
          },
        ],
        filtriraniProjekti: [],
        filterDatumKraja: '',
        filterDatumPocetka: '',
        error: null,
      };
    },
    methods: {
      filtrirano(){
        this.filtriraniProjekti = [];
        const filterDatumPocetka = this.filterDatumPocetka ? new Date(this.filterDatumPocetka) : null;
        const filterDatumKraja = this.filterDatumKraja ? new Date(this.filterDatumKraja) : null;
        for (const projekt of this.projects) {
          const datumPoc = new Date(projekt.datumPoc);
          const datumKraj = new Date(projekt.datumKraj);
          if ((!filterDatumPocetka || datumPoc <= filterDatumPocetka) &&
          (!filterDatumKraja || datumKraj >= filterDatumKraja) &&
          (!this.filterVrstaAktivnosti || projekt.vrstaAktivnosti === this.filterVrstaAktivnosti)) {
            this.filtriraniProjekti.push(projekt);
          }
        }
      },
    },
    computed: {
      formatiranoIme() {
        return (name) => {
          return name
            .replace(/-/g, ' ')
            .replace(/\b\w/g, char => char.toUpperCase());
        };
      },
    },
    async created() {
      try {
        this.filtriraniProjekti = this.projects;
        const response = await axios.get('https://voloconnect.onrender.com/api/projects');
        this.projects = response.data;
      } catch (error) {
        this.error = error.response ? error.response.data : 'Ne mogu se dohvatiti projekti.';
      }
    },
  };
</script>
  

  