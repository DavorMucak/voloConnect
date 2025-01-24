<template>
  <div class="container">
    <h2>Registracija</h2>
    <form @submit.prevent="register">
      <!-- forma za unos podataka -->
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
      <p class="success" v-if="success"></p>
    </form>
  </div>

  <div class="container" v-if="showCode">
    <h3>Molimo unesite kod za verifikaciju.</h3>
    <p>Šesteroznamenkasti kod je poslan na vašu email adresu.</p>

    <div class="code-input">
      <input v-for="(digit, index) in digits" :key="index" v-model="digits[index]" maxlength="1" ref="digitInputs"
        placeholder="-" @input="moveToNext(index, $event)" @keydown.backspace="moveToPrev(index, $event)"
        class="digit-box" />
    </div>

    <button @click="resendCode" r> Trebam novi kod </button>

    <button @click="verifyCode">Potvrdi kod</button>
    <p class="error" v-if="verifError">{{ verifError }}</p>
    <p class="success" v-if="verifSuccess">{{ verifSuccess }}</p>

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
      isLoggedIn: false,
      error: '',
      success: '',
      verifError: '',
      verifSuccess: '',
      selected: '',
      options: ["volonter", "organizacija", "admin"],
      showCode: false,
      digits: Array(6).fill(""),    //polje za displayanje znamenki koda
      userCode: "",
    };
  },
  methods: {
    async register() {
      // resetiranje error i success poruke
      this.error = '';
      this.success = '';

      // provjera jesu li svi podaci uneseni
      if (this.selected === 'organizacija' && (!this.username || !this.email || !this.password)) {
        this.error = 'Molimo unesite sve informacije.';
        return;
      }

      if ((this.selected === 'volonter' || this.selected === 'admin') && (!this.username || !this.email || !this.password || !this.name || !this.surname || !this.phonenum)) {
        this.error = 'Molimo unesite sve informacije.';
        return;
      }

      const passwordRegex = /^(?=.*[A-Z])(?=.*\d).{6,}$/;
      if (!passwordRegex.test(this.password)) {
        this.error = 'Lozinka mora imati najmanje 6 znakova, uključujući jedno veliko slovo i jednu znamenku.';
        return;
      }
      try {
        // slanje POST requesta backendu
        const response = await axios.post('http://localhost:8080/api/auth/register', {
          username: this.username,
          password: this.password,
          email: this.email,
          name: this.name,
          surname: this.surname,
          phonenum: this.phonenum,
          role: this.selected,
        });
        // ako je uspjesno


        this.success = response.data;
        this.showCode = true;
        this.userCode = '';
        this.digits = Array(6).fill('');

      } catch (error) {// ako nije uspjesno
        this.error = error.response ? error.response.data : 'Došlo je do greške.';
      }
    },
    clearForm() {
      // resetiranje forme nakon što su poslani podaci
      this.username = '';
      this.password = '';
      this.email = '';
      this.name = '';
      this.surname = '';
      this.phonenum = '';
      this.selected = '';
    },
    moveToNext(index, event) {
      const value = this.digits[index];
      if (value.length === 1 && index < this.digits.length - 1) {
        this.$refs.digitInputs[index + 1].focus();
      }
    },
    moveToPrev(index, event) {
      if (this.digits[index] === "" && index > 0) {
        this.$refs.digitInputs[index - 1].focus();
      }
    },
    async verifyCode() {
      const userCode = this.digits.join("");
      if (userCode.length !== 6) {
        this.verifError = "Kod mora biti šesteroznamenkasti broj.";
        this.verifSuccess = "";
        return;
      }
      try {
        const response = await axios.post('http://localhost:8080/api/auth/verify', {
          email: this.email,
          verificationCode: userCode,
        });
        this.verifSuccess = response.data;
        this.verifError = "";
        this.isLoggedIn = true;

        // Reset forme
        this.clearForm();
        alert('Kod je točan! Uspješna registracija.');

        // Redirect na početnu stranicu
        this.$router.push({ path: '/' }).then(() => this.$router.go(0));
      } catch (error) {
        this.verifError = error.response ? error.response.data : "Došlo je do greške.";
        this.verifSuccess = "";
      }
    },
    async resendCode() {
      try {
        await axios.post('http://localhost:8080/api/auth/resend', null, {
          params: { email: this.email },
        });
        alert('Novi kod je poslan na vašu email adresu.');
      } catch (error) {
        alert('Došlo je do greške prilikom slanja novog koda.');
      }
    },
  },
};
</script>