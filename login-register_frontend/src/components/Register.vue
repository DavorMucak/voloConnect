<template>
  <div class="container">
    <h2>Registracija</h2>
    <form @submit.prevent="register">

      <select v-model="selected" class="selection">
        <option disabled value="" class="placeholder">uloga</option>
        <option v-for="option in options" :key="option" :value="option"> {{ option }} </option>
      </select>

      <p v-if="selected == 'admin' || selected == 'volonter'">                  <!--ovisno o tome tko je korisnik, prikazuju se druga polja-->
        <input type="text" v-model="username" placeholder="korisničko ime" />
        <input type="email" v-model="email" placeholder="email" />
        <input type="password" v-model="password" placeholder="lozinka" />
        <input type="text" v-model="name" placeholder="ime" />
        <input type="text" v-model="surname" placeholder="prezime" />
        <input type="tel" v-model="phonenum" placeholder="broj mobitela" />
      </p>

      <p v-else-if="selected == 'organizacija'">
        <input type="text" v-model="username" placeholder="ime organizacije" />
         <input type="email" v-model="email" placeholder="email" />
         <input type="password" v-model="password" placeholder="lozinka" />
      </p>

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

      if(this.selected == 'organizacija' && (!this.username || !this.email || !this.password)){
        this.error = 'Molimo unesite sve informacije.';
        return;
      }

      else if ((this.selected == 'volonter' || this.selected == 'admin') && (!this.username || !this.email || !this.password || !this.name || !this.surname || !this.phonenum || !this.selected)) {
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