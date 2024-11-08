<template>
  <div id="app">
    <div class="mini-container">
      <button @click="isLogin = true">Prijava</button>
      <button @click="isLogin = false">Registracija</button>
    </div>
    <div v-if="isLogin">
      <Login />
    </div>
    <div v-else>
      <Register />
    </div>
  </div>
</template>

<script>
import Login from './components/Login.vue';
import Register from './components/Register.vue';

export default {
  components: {
    Login,
    Register,
  },
  data() {
    return {
      isLogin: false, // register forma je default
    };
  },
};
</script>
