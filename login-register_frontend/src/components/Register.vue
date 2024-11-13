<template>
  <div class="container">
    <h2>Registracija</h2>
    <form @submit.prevent="register">
      <select v-model="selected" class="selection">
        <option disabled value="" class="placeholder">uloga</option>
        <option v-for="option in options" :key="option" :value="option">{{ option }}</option>
      </select>

      <p v-if="selected === 'admin' || selected === 'volonter'">
        <input type="text" v-model="username" placeholder="korisničko ime" />
        <input type="email" v-model="email" placeholder="email" />
        <input type="password" v-model="password" placeholder="lozinka" />
        <input type="text" v-model="name" placeholder="ime" />
        <input type="text" v-model="surname" placeholder="prezime" />
        <input type="tel" v-model="phonenum" placeholder="broj mobitela" />
      </p>

      <p v-else-if="selected === 'organizacija'">
        <input type="text" v-model="username" placeholder="ime organizacije" />
        <input type="email" v-model="email" placeholder="email" />
        <input type="password" v-model="password" placeholder="lozinka" />
      </p>

      <button type="submit">Registriraj se</button>
      <p class="error" v-if="error">{{ error }}</p>
      <p class="success" v-if="success">{{ success }}</p>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

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
      success: '',
      selected: '',
      options: ["volonter", "organizacija", "admin"],
    };
  },
  methods: {
    async register() {
      // Reset error and success messages
      this.error = '';
      this.success = '';

      // Basic validation
      if (this.selected === 'organizacija' && (!this.username || !this.email || !this.password)) {
        this.error = 'Molimo unesite sve informacije.';
        return;
      }

      if ((this.selected === 'volonter' || this.selected === 'admin') && (!this.username || !this.email || !this.password || !this.name || !this.surname || !this.phonenum)) {
        this.error = 'Molimo unesite sve informacije.';
        return;
      }

      try {
        // Sending POST request to the backend to register a new user
        const response = await axios.post('http://localhost:8080/api/auth/register', {
          username: this.username,
          password: this.password,
          email: this.email,
          name: this.name,
          surname: this.surname,
          phonenum: this.phonenum,
          role: this.selected,
        });

        // Handling success response
        this.success = response.data;
        this.clearForm();
      } catch (error) {
        // Handling error response
        this.error = error.response ? error.response.data : 'Došlo je do greške.';
      }
    },
    clearForm() {
      // Clear all form fields and selections
      this.username = '';
      this.password = '';
      this.email = '';
      this.name = '';
      this.surname = '';
      this.phonenum = '';
      this.selected = '';
    }
  }
};
</script>


