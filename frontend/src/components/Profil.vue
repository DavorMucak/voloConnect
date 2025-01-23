<template>
  <!-- prikaz podataka o korisniku -->
  <div v-if="!urediDetalje">
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
    <button v-if="!urediDetalje" @click="pokreniUredivanje">Promijeni detalje</button>
  
    <div v-if="urediDetalje">
      <div>
        <label>Email:</label>
        <input v-model="privremeniPodaci.email" />
      </div>
      <div>
        <label>Ime:</label>
        <input v-model="privremeniPodaci.name" />
      </div>
      <div>
        <label>Prezime:</label>
        <input v-model="privremeniPodaci.surname" />
      </div>
    <div>
      <label>Broj telefona:</label>
      <input v-model="privremeniPodaci.phonenum" />
    </div>
      <button @click="spremiPromjene">Spremi</button>
      <button @click="prekiniUredivanje">Odustani</button>
    </div>
  </div>
  <!-- kad se pritisne na ovo ide prikaz projekata na koje je korisnik prijavljen(volonter) ili cije je vlasnik (organizacija) -->
  <div v-if="uloga !== 'admin'">

    <!-- prikaz biljezaka i recenzija -->
    <div v-if="uloga === 'volonter'">
      <Biljeske :username="korisnik.username"/>
    </div>

    <Recenzije :username="korisnik.username"/>

    <!-- ako je korisnik organizacija, ima opciju izrade novog projekta -->
    <router-link v-if="uloga === 'organizacija'" to="/novi-projekt"> <button>Novi projekt</button> </router-link>

    <router-link :to="{ name: 'ListaProjekata', params: { username: korisnik.username } }"> <button>Moji projekti</button> </router-link>

    <button @click="obrisiProfil">Obriši profil</button>
  </div>

  <!-- ako je korisnik admin, moze vidit i prituzbe i registracije -->
  <div v-if="uloga === 'admin'">
    <Prituzbe />
    <RegistracijeAdmina />
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
      privremeniPodaci: {},
      uloga: '',
      korisnickoIme: ' ',
      urediDetalje: false,
    };
  },
  methods: {
    pokreniUredivanje() {
      this.privremeniPodaci = { ...this.korisnik };
      this.urediDetalje = true;
    },
    prekiniUredivanje() {
      this.urediDetalje = false;
      this.privremeniPodaci = {};
    },
    async spremiPromjene() {
      try {
        const response = await axios.put(`http://localhost:8080/api/user/${this.korisnik.username}`, {
          email: this.privremeniPodaci.email,
          name: this.privremeniPodaci.name,
          surname: this.privremeniPodaci.surname,
          phonenum: this.privremeniPodaci.phonenum,
        });
        this.urediDetalje = false;
      } catch (error) {
        console.error('Greška pri spremanju promjena:', error);
      }
    },
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

          const response = await axios.get(`http://localhost:8080/api/user/${this.korisnik.username}`);
          this.korisnik.phonenum = response.data.phonenum;
          this.korisnik.email = response.data.email;
          this.korisnik.name = response.data.name;
          this.korisnik.surname = response.data.surname;
          
          
          console.log("Ovo ispisuje,: " + response.data);          
        }
        this.korisnik.name = "netko"
        this.korisnik.surname = "nesto"
        this.korisnik.phonenum = "dknsdkjsndfkjdn"
        this.korisnik.email = "dkjsndvkdjn"

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