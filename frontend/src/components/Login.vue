<template>
  <div class="container">
    <h2>Login</h2>

    <form @submit.prevent="login">
      <!-- Form for entering user credentials -->
      <select v-model="selected" class="selection">
        <option disabled value="">uloga</option>
        <option v-for="option in options" :key="option" :value="option">
          {{ option }}
        </option>
      </select>

      <p v-if="selected == 'admin' || selected == 'volonter'">
        <input type="text" v-model="username" placeholder="korisničko ime"/>
        <input type="password" v-model="password" placeholder="lozinka" />
      </p>

      <p v-if="selected == 'organizacija'">
        <input type="text" v-model="username" placeholder="ime organizacije" />
        <input type="password" v-model="password" placeholder="lozinka" />
      </p>

      <button type="submit">Prijava</button>
      <p class="error" v-if="error">{{ error }}</p>
    </form>

  </div>

  <div v-show="!isLoggedIn && selected" class="container">
    <div id="g_id_onload"
         data-client_id="368455952414-n0qaeppdv3gu4qpofn5f6jkc0gu4l19u.apps.googleusercontent.com"
         data-callback="handleCredentialResponse">
    </div>
    <div class="g_id_signin" data-type="standard"></div>
  </div>

  <div v-if="isLoggedIn" class="hidden">
    <p>Bok, <span id="user-name">{{ userName }}</span>!</p>
    <button @click="googleLogout">logout</button>
  </div>
</template>

<script>
import axios from 'axios';
import VueJwtDecode from 'vue-jwt-decode';
import { useRouter } from 'vue-router';

export default {
  data() {
    return {
      username: '',       // Username for manual login
      password: '',
      error: '',
      selected: '',       // Selected role
      options: ["volonter", "organizacija", "admin"],
      isLoggedIn: false,
      userName: '',       // Username for Google login
    };
  },
  setup() {
    const router = useRouter();
    return { router }; // Vraćamo router kako bi bio dostupan u metodama
  },
  methods: {
    async login() {
      this.error = '';

      if (!this.username || !this.password || !this.selected) {
        this.error = 'Molimo unesite ime i lozinku.';
        return;
      }

      try {
        console.log("Saljem post request: " + this.username + "  " + this.password);
        const response = await axios.post('https://voloconnect.onrender.com/api/auth/login', {
          username: this.username,
          password: this.password
        });
        console.log("primljen token: " + response.data);
        let token = response.data.substring("<CustomJWT>".length);
        if(token.startsWith("<CustomJWT>"))
          token = token.substring("<CustomJWT>".length)
        console.log("token: " + token);
        console.log(VueJwtDecode.decode(token));
        const decodedToken = VueJwtDecode.decode(token);
        localStorage.setItem('token', token);
        localStorage.setItem('username', decodedToken.sub); // username
        localStorage.setItem('role', decodedToken.role);    // role
        localStorage.setItem('userID', decodedToken.userID);


        this.$root.fetchKorisnik();     // automatski updatea podatke o korisniku u root komponenti (Vue.js)

        alert('Uspješna prijava');
        this.isLoggedIn = true;
        this.userName = response.data

        console.log("name: " + this.userName);
        this.router.push('/');
      } catch (error) {
        console.error('Greška u prijavi', error);
        this.error = 'Neuspješna prijava. Pokušajte ponovno.';
      }
    },

    handleCredentialResponse(response) {
      const idToken = response.credential;
      if (idToken) {
        console.log("Primljen idToken: ", idToken);
        // Slanje na backend...
      } else {
        console.error("Nije primljen idToken!");
      }
      console.log("Šaljem token na backend")
      axios.post('https://voloconnect.onrender.com/api/auth/google-login', {
        idToken: response.credential, //saljem ID token tako da ne ovisi o autorizacijskom kodu
        role: this.selected,        //slanje rolea u back
      })
          .then((res) => { //u responeseu poslan token ("token"), ime korisnika ("name") i njegova uloga ("role")
            console.log("Spremam token u localStorage");
            alert('Uspješna Google prijava!');
            let token = res.data.token;

            console.log(token);
            if(token)
              if(token.startsWith("<CustomJWT>"))
                token = token.substring("<CustomJWT>".length)
            console.log(VueJwtDecode.decode(token));
            const decodedToken = VueJwtDecode.decode(token);
            localStorage.setItem('token', token);
            localStorage.setItem('username', decodedToken.sub); // username
            localStorage.setItem('role', decodedToken.role);    // role
            localStorage.setItem('userID', decodedToken.userID);
            this.$root.fetchKorisnik();     // automatski updatea podatke o korisniku u root komponenti (Vue.js)

            this.isLoggedIn = true;
            this.router.push('/');

          })
          .catch((error) => {
            console.error('Greška u prijavi s Googleom', error);
            alert('Dogodila se pogreška pri prijavi s Googleom. Molimo pokušajte ponovo.');
          });
    },

    googleLogout() {
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      localStorage.removeItem('userID');
      localStorage.removeItem('username');
      this.isLoggedIn = false;
      this.userName = '';
    }
  },
  mounted() {
    console.log("Učitavam Google Client library...");
    const script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    document.head.appendChild(script);

    window.handleCredentialResponse = this.handleCredentialResponse;
    console.log("Postavljen handleCredentialResponse callback.");
  },
};
</script>

