<template>
  <div class="home-container">
    <h2>Projekti</h2>
    <div class="filteri">
      <!-- filtracija projekata na tememlju datuma i vrsti aktivnosti-->
      <h3>Filtriranje projekata</h3>
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
      <h3 style="text-align: center;">Popis projekata</h3>
      <ul>
        <li v-for="project in filtriraniProjekti" :key="project.id">
          <!-- prikaz projekata, kad se stisne ode na stranicu projekta -->
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
import apiClient from '@/apiClient';

export default {
  data() {
    return {
      projects: [],
      filtriraniProjekti: [],
      filterDatumKraja: '',
      filterDatumPocetka: '',
      error: null,
    };
  },
  methods: {
    // metoda za filtraciju projekata
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
    //ime projekta se formatirano slalo backendu, sad ga vracamo
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
      //na pocetku se ne primjenjuje filter nego su svi projekti prikazani
      this.filtriraniProjekti = this.projects;
      //dohvat liste projekata s backenda
      const response = await axios.get('http://localhost:8080/api/projects');
      this.projects = response.data;
    } catch (error) {
      this.error = error.response ? error.response.data : 'Ne mogu se dohvatiti projekti.';
    }
  },
};
</script>