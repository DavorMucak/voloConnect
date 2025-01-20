<template>
  <div  v-if="projekt" class="home-container">
    <h2>{{ formatiranoIme }}</h2>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="projekt">
      <!-- prikaz detalja projekta -->
      <p>{{ projekt.opisProjekta }}</p>
      <p><strong>Broj ljudi:</strong> {{ projekt.brojLjudi }}</p>
      <p><strong>Datum početka:</strong> {{ projekt.datumPoc }}</p>
      <p><strong>Datum kraja:</strong> {{ projekt.datumKraj }}</p>
      <p><strong>Vrsta aktivnosti:</strong> {{ projekt.vrstaAktivnosti }}</p>
      <!-- !!!treba dodat jos organizaciju ciji je projekt -->
      <!-- ako je korisnik volonter moze se prijaviti na projekt, ili odjaviti ako je vec prijavljen -->
      <div v-if="uloga === 'volonter'">
        <button v-if="provjeriPrijavu()" @click="prijavaProjekt">Prijavi se!</button>
        <button v-else @click="odjavaProjekt">Odjavi projekt</button>
      </div>
      <!-- !!!!ako je korisnik organizacija=> uredi podatke + vidi prijavljene ako je njihov projekt inace view only -->
    </div>
    <div v-else>
      <p>Nema projekata.</p>
    </div>
  </div>
</template>


<script>
import axios from 'axios';

export default {
  props: {
    imeProjekta: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      // privremeni projekti dok ne povezemo s backendon
      projekti: [
        {
          projectId: 1,
          imeProjekta: "izrada-web-aplikacije",
          opisProjekta: "Razvoj interaktivne web aplikacije koristeći Vue.js.",
          brojLjudi: 5,
          datumPoc: "2024-01-15",
          datumKraj: "2024-03-15",
          vrstaAktivnosti: "Fizički poslovi"
        },
        {
          projectId: 2,
          imeProjekta: "analiza-podataka",
          opisProjekta: "Projekt fokusiran na analizu podataka koristeći Python i Pandas.",
          brojLjudi: 3,
          datumPoc: "2024-02-01",
          datumKraj: "2024-04-01",
          vrstaAktivnosti: "Administrativni poslovi"
        },
        {
          projectId: 3,
          imeProjekta: "mobilna-aplikacija",
          opisProjekta: "Razvoj mobilne aplikacije za Android i iOS platforme.",
          brojLjudi: 6,
          datumPoc: "2024-03-10",
          datumKraj: "2024-06-10",
          vrstaAktivnosti: "Podučavanje"
        },
      ],
      projekt: null,
      error: null,
      uloga: '',
    };
  },
  computed: {
    //ime je formatirano radi slanja backendu pa ga vracamo na normalno
    formatiranoIme() {
      return this.imeProjekta
          .replace(/-/g, ' ')
          .replace(/\b\w/g, char => char.toUpperCase());
    },
  },
  async created() {
    try {
      //ovo odkomentirat kad se spaja s backendon:
      const response = await axios.get('http://localhost:8080/api/projects');
      this.projekti = response.data;      
      const token = localStorage.getItem("token");

      this.uloga=localStorage.getItem('role');
      //pronalazi projekt tako da iz liste projekata nade onaj koji se poklapa s imenom projekta
      this.projekt = this.projekti.find(
          (projekt) => projekt.imeProjekta === this.imeProjekta.replace(/\s+/g, '-').toLowerCase()
      );
      console.log(this.projekt);
      
      if (!this.projekt) {
        throw new Error("Project not found");
      }
    } catch (error) {
      this.error = error.response ? error.response.data : 'Ne može se dohvatiti projekt.';
    }
  },
  methods: {
    prijavaProjekt() {
    const projectId = this.projekt.id;    
    const userId = localStorage.getItem('userID'); // ID ulogovanog korisnika
    axios.post(`/api/projects/${projectId}/apply`, null, {
        params: { userId: userId }
    })
    .then(response => {
        alert(response.data);
    })
    .catch(error => {
        alert("Failed to apply: " + error.response.data);
    });
  },

    async odjavaProjekt() {
      //!!!kod za odjavu projekta
    },
    provjeriPrijavu(){
      return true;
    }
  },
};
</script>