<template>
  <div class="container">
    <h2>Registracija</h2>
    <form @submit.prevent="register">
      <input type="text" v-model="username" placeholder="korisničko ime" />
      <input type="password" v-model="password" placeholder="lozinka" />
      <input type="email" v-model="email" placeholder="email" />
      <input type="text" v-model="name" placeholder="ime" />
      <input type="text" v-model="surname" placeholder="prezime" />
      <input type="tel" v-model="phonenum" placeholder="broj mobitela" />
      <select v-model="selected" class="selection">
        <option disabled value="">uloga</option>
        <option v-for="option in options" :key="option" :value="option">
          {{ option }}
        </option>
      </select>

      <button type="submit">Registriraj se</button>
      <p class="error" v-if="error">{{ error }}</p>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      email: '',
      name: '',
      surname: '',
      phonenum: '',
      error: '',
      selected: '',
      options: ["volonter", "organizacija", "admin"],
    };
  },
  methods: {
    register() {
      this.error = ''; //resetirane error poruke na default(blank)

      if (!this.username || !this.email || !this.password || !this.name || !this.surname || !this.phonenum || !this.selected) {
        this.error = 'Molimo unesite sve informacije.';
        return;
      }

      else
        alert('Uspješna registracija!');

      // resetiranje polja nakon registracije
      this.username = '';
      this.password = '';
      this.email = '';
      this.name = '';
      this.surname = '';
      this.phonenum = '';
    },
  },
};
</script>