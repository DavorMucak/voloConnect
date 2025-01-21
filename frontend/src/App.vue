<template>
  <div id="app">
    <div class="mini-container">
      <!-- botuni na homeu -->
      <router-link to="/"> <button>Home</button> </router-link>

      <!-- ako korisnik nije prijavljen prikazuje se login i register -->
      <template v-if="!isLoggedIn">
        <router-link to="/login"> <button>Prijava</button> </router-link>
        <router-link to="/register"> <button>Registracija</button> </router-link>
      </template>

      <!-- ako je korisnik prijavljen prikazuju se logout i profil botuni -->
      <template v-else>
        <!-- kad se pritisne salje na profil od korisnickog imena prijavljenog korisnika (profil/:username) -->
        <router-link :to="{ name: 'Profil', params: { username: korisnickoIme } }"> <button>Profil</button>
        </router-link>

        <button @click="logout">Odjava</button>

        <button  style="float: right; margin-right: 15px;">🔔</button>
      </template>
    </div>

    <router-view />

  </div>
</template>

<script>
import Home from './components/Home.vue';
import Login from './components/Login.vue';
import Register from './components/Register.vue';
import NoviProjekt from './components/za_profile/NoviProjekt.vue';
import Profil from './components/Profil.vue';
import VueJwtDecode from 'vue-jwt-decode';


export default {
  components: {
    Home,
    Login,
    Register,
    NoviProjekt,
    Profil,
  },
  data() {
    return {
      korisnickoIme: ' ',
      isLoggedIn: false,      //prati je li korisnik ulogiran t.d. mozemo prikazati Login/Logout botun po potrebi
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
          this.isLoggedIn = true;   // korisnik prijavljen
        }

      } catch (error) {
        console.error('Greska u dohvavanju podataka:', error);
        this.isLoggedIn = false;    // error -> korisnik nije prijavljen
      }
    },
    created() {
        console.log('Aplikacija se pokreće, brišem token...');
        localStorage.removeItem('token');
        localStorage.removeItem('username'); // Ako koristiš username, možeš i njega izbrisati
        localStorage.removeItem('role'); // Ako koristiš role, i to izbriši
    },
    logout() {
      const confirmed = window.confirm("Jeste li sigurni da se želite odjaviti?");    //provjera

      if (confirmed) {
        localStorage.removeItem("token"); // uklanja token i podatke o korisniku
        localStorage.removeItem("isLoggedIn");
        localStorage.removeItem("username");
        this.korisnickoIme = '';
        this.isLoggedIn = false;
        this.$router.push('/');   // redirecta na home
      }
    }
  },
  mounted() {
    this.fetchKorisnik();  // Fetch the user info when the component is mounted
    const loggedIn = localStorage.getItem('isLoggedIn');
    if (loggedIn) {
      this.isLoggedIn = true;
      this.userName = localStorage.getItem('userName'); // Retrieve the username
    } else {
      this.isLoggedIn = false;
    }
  }
};
</script>