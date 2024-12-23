<template>
  <div  v-if="projekt" class="home-container">
    <h2>{{ formatiranoIme }}</h2>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="projekt">
      <p>{{ projekt.opisProjekta }}</p>
      <p><strong>Broj ljudi:</strong> {{ projekt.brojLjudi }}</p>
      <p><strong>Datum početka:</strong> {{ projekt.datumPoc }}</p>
      <p><strong>Datum kraja:</strong> {{ projekt.datumKraj }}</p>
      <p><strong>Vrsta aktivnosti:</strong> {{ projekt.vrstaAktivnosti }}</p>
      <button @click="prijavaProjekt">Prijavi se!</button>
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
        projekti: [
          {
            imeProjekta: "izrada-web-aplikacije",
            opisProjekta: "Razvoj interaktivne web aplikacije koristeći Vue.js.",
            brojLjudi: 5,
            datumPoc: "2024-01-15",
            datumKraj: "2024-03-15",
            vrstaAktivnosti: "Fizički poslovi"
          },
          {
            imeProjekta: "analiza-podataka",
            opisProjekta: "Projekt fokusiran na analizu podataka koristeći Python i Pandas.",
            brojLjudi: 3,
            datumPoc: "2024-02-01",
            datumKraj: "2024-04-01",
            vrstaAktivnosti: "Administrativni poslovi"
          },
          {
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
      };
    },
      computed: {
      formatiranoIme() {
        return this.imeProjekta
          .replace(/-/g, ' ')
          .replace(/\b\w/g, char => char.toUpperCase());
        },
      },
    async created() {
      try {
        const response = await axios.get('https://voloconnect.onrender.com/api/projects');
        this.projekti = response.data; 
        this.projekt = this.projekti.find(
        (projekt) => projekt.imeProjekta === this.imeProjekta.replace(/\s+/g, '-').toLowerCase()
        //(projekt) => projekt.imeProjekta === this.imeProjekta.replace(/%20/g, '-').toLowerCase()
        );
        if (!this.projekt) {
          throw new Error("Project not found");
        }
      } catch (error) {
        this.error = error.response ? error.response.data : 'Ne može se dohvatiti projekt.';
      }
    },
    methods: {
      async prijavaProjekt() {
        try {
          const response = await axios.post('https://voloconnect.onrender.com/api/signup',
            { id: this.projekt.id }
          );
          alert("Uspješna prijava!");
        } catch (error) {
          alert(
            "Greška."
          );
        }
      },
    },
  };
</script>
  

  