<template>
  <div id="app">
    <div class="mini-container"> 
      <!-- botuni na homeu -->
      <router-link to="/"> <button>Home</button> </router-link>
      <router-link to="/login"> <button>Prijava</button> </router-link>
      <router-link to="/register"> <button>Registracija</button> </router-link>
      <!-- kad se pritisne salje na profil od korisnickog imena prijavljenog korisnika (profil/:username) -->
      <router-link :to="{ name: 'Profil', params: { username: korisnickoIme } }"> <button>Profil</button> </router-link>
      <!-- !!!!treba jos namistit da se prijava i registracija i profil maknu nakon prijave i onda da se pojavi odjava -->
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
    };
  },
  methods: {
      async fetchKorisnik() {
        try {
          // dohvat podataka o prijavljenon korisniku
          const token = localStorage.getItem("token");
          const response = await axios.get("http://localhost:8080/api/auth/users", {
            headers: { Authorization: `Bearer ${token}` },
          });
          //sprema username
          this.korisnickoIme=response.data.username;
        }catch (error) {
          console.error('Greska u dohvavanju podataka:', error);
        }
      }
  },
  mounted() {
    this.fetchKorisnik();  // Fetch the user info when the component is mounted
  }
};
</script>