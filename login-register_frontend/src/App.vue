<template>
  <div id="app">
    <div class="mini-container">
      <router-link to="/"> <button>Home</button> </router-link>
      <router-link to="/login"> <button>Prijava</button> </router-link>
      <router-link to="/register"> <button>Registracija</button> </router-link>
    </div>

    <router-view />

  </div>
</template>

<script>
import Home from './components/Home.vue';
import Login from './components/Login.vue';
import Register from './components/Register.vue';


export default {
  components: {
    Home,
    Login,
    Register,
  },
  data() {
    return {
    };
  },
};
</script>
