<template>
  <!-- kad se pritisne na ovo ide prikaz projekata na koje je korisnik prijavljen(volonter) ili cije je vlasnik (organizacija) -->
  <div v-if="uloga !== 'admin'">
    <router-link to="/moji-projekti"> <button>Moji projekti</button> </router-link>

    <button @click="obrisiProfil">Obriši profil</button>

    <!-- prikaz biljezaka i recenzija -->
    <Biljeske />

    <Recenzije />

    <!-- ako je korisnik organizacija, ima opciju izrade novog projekta -->
    <router-link v-if="uloga === 'organizacija'" to="/novi-projekt"> <button>Novi projekt</button> </router-link>

  </div>

  <!-- ako je korisnik admin, moze vidit i prituzbe i registracije -->
  <div v-if="uloga === 'admin'">
    <Prituzbe />
    <RegistracijeAdmina />
  </div>




  <!-- prikaz podataka o korisniku -->
  <div>
    <h2>Podaci o korisniku</h2>
    <p><strong>Korisničko ime:</strong> {{ korisnik.username }}</p>
    <p><strong>Email:</strong> {{ korisnik.email }}</p>
    <p><strong>Ime:</strong> {{ korisnik.name }}</p>
    <p><strong>Prezime:</strong> {{ korisnik.surname }}</p>
    <p><strong>Broj telefona:</strong> {{ korisnik.phonenum }}</p>
    <p><strong>Uloga:</strong> {{ korisnik.role }}</p>
  </div>
  <!-- ako je ovo profil prijavljenog korisnika moze prominit detalje -->
  <div v-if="jeLiMojProfil()">
    <!-- !!!napravit promjenu detalja -->
    <p>promjeni detalje</p>
  </div>
</template>

<script>
import axios from 'axios';
import apiClient from '@/apiClient';
import Biljeske from './za_profile/Biljeske.vue';
import Recenzije from './za_profile/Recenzije.vue';
import Prituzbe from './za_profile/Prituzbe.vue';
import RegistracijeAdmina from './za_profile/RegistracijeAdmina.vue';
import VueJwtDecode from 'vue-jwt-decode';


export default {
  components: {
    Biljeske,
    Recenzije,
    Prituzbe,
    RegistracijeAdmina,
  },
  data() {
    return {
      korisnik: {
        username: '',
        password: '',
        email: '',
        name: '',
        surname: '',
        phonenum: '',
        role: ["volonter", "organizacija", "admin"],
      },
      uloga: '',
      korisnickoIme: ' ',
    };
  },
  methods: {
    async fetchKorisnik() {
      try {
        // dohvat podataka o prijavljenon korisniku
        const token = localStorage.getItem("token");
        if (token) {    //ako postoji token korisnik je prijavljen
          
          //sprema username
          this.korisnickoIme = VueJwtDecode.decode(token).sub;
          this.uloga = VueJwtDecode.decode(token).role;
          this.korisnik.role = VueJwtDecode.decode(token).role;
          this.korisnik.username = VueJwtDecode.decode(token).sub;

          const response = await apiClient.get(`http://localhost:8080/api/user/${this.korisnik.username}`);
          this.korisnik.phonenum = response.data.phonenum;
          this.korisnik.email = response.data.email;
          this.korisnik.name = response.data.name;
          this.korisnik.surname = response.data.surname;
          
          
          console.log("Ovo ispisuje,: " + response.data);          
        }

      } catch (error) {
        console.error('Greska u dohvavanju podataka:', error);
      }
    },
    jeLiMojProfil() {
      //provjerava je li ovo profil od prijavljenog korisnika
      if (this.korisnickoIme === this.$route.params.username) {
        return true;
      } else {
        return false;
      }
    },
    async obrisiProfil() {
      const confirmation = window.confirm(
        "Jeste li sigurni da želite obrisati svoj profil? Bit će trajno izbrisan."
      );
      if (confirmation) {
        try {
          const username = localStorage.getItem("username"); // Dohvaćanje username-a iz localStorage

          if (!username) {
            alert("Korisničko ime nije pronađeno.");
            return;
          }

          const response = await axios.delete(
            `http://localhost:8080/api/auth/delete-account/${username}`
          );

          alert(response.data.message); // Poruka o uspjehu

          // Brisanje podataka iz localStorage i redirekcija
          localStorage.removeItem("username");
          localStorage.removeItem("role");
          localStorage.removeItem("token");
          this.$router.push("/login"); // Preusmjeri na login
        } catch (error) {
          console.error("Greška u brisanju profila", error);
          alert("Došlo je do greške pri brisanju profila. Molimo pokušajte ponovno.");
        }
      }
    }

  },
  async created() {
    await this.fetchKorisnik();
  },
};
</script>