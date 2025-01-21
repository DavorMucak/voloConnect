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
      <p class="success" v-if="success">{{ success }}</p>
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

    <p>Kod vrijedi: {{ timer }} sekundi</p>
    <div v-if="timer == 0">
      <button @click="resendCode" r> Trebam novi kod </button>
    </div>

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
      expectedCode: "123456",   //123546 radi testiranja
      timer: 10,
      timerInterval: null,
    };
  },

  methods: {
    async register() {
      // resetiranje error i success poruke
      this.error = '';
      this.success = '';
      this.showCode = true;   //showa se odmah radi testiranja, inace treba biti dolje

      this.startTimer();

      // provjera jesu li svi podaci uneseni
      if (this.selected === 'organizacija' && (!this.username || !this.email || !this.password)) {
        this.error = 'Molimo unesite sve informacije.';
        return;
      }

      if ((this.selected === 'volonter' || this.selected === 'admin') && (!this.username || !this.email || !this.password || !this.name || !this.surname || !this.phonenum)) {
        this.error = 'Molimo unesite sve informacije.';
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
    verifyCode() {
      const userCode = this.digits.join("");
      if (userCode.length !== 6) {
        this.verifError = "Kod mora biti šesteroznamenkasti broj.";
        this.verifSuccess = "";
        return;
      }
      if (userCode === this.expectedCode) {
        this.verifError = "";
        this.verifSuccess = "Kod je točan!";
        localStorage.setItem('isLoggedIn', true);
        this.isLoggedIn = true;
        this.clearForm();

        this.$root.fetchKorisnik();     // automatski updatea podatke o korisniku u root komponenti (Vue.js)

        alert('Kod je točan! Uspješna registracija.');

        this.$router
          .push({ path: '/' })
          .then(() => { this.$router.go(0) })   //redirect i reload

      } else {
        this.verifError = "Kod nije točan. Pokušajte ponovno.";
        this.verifSuccess = "";
      }
    },
    resendCode() {
      //slanje novog maila s kodom 
      this.expectedCode = "654321"; // simulacija dobivanja nogov koda radi testiranja
      alert('Novi kod je poslan na vašu email adresu: ');
      this.startTimer(); // reset timera
    },
    startTimer() {
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
      }

      this.timer = 10;

      this.timerInterval = setInterval(() => {
        if (this.timer > 0) {
          this.timer--;
        } else {
          clearInterval(this.timerInterval);
          this.expectedCode = null;   //"kod nevazeci"
          this.timerInterval = null;
        }
      }, 1000);
    },
  }
};
</script>


/*treba popravit/provjerit:
-nakon registracije se redirecta na home ali se ne refresha tj ne loada se isLoggedIn pravilno
-slanje koda mailom
-notif inbox