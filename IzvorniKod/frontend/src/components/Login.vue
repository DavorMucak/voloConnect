<template>
    <div class="container">
        <h2>Login</h2>

        <form @submit.prevent="login">
            <!-- forma za unos podataka -->
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

    <div v-if="!isLoggedIn" class="container">
        <button @click="googleLogin">Prijavi se Google profilom</button>
    </div>

    <div v-if="isLoggedIn" class="hidden">
        <p>Bok, <span id="user-name">{{ userName }}</span>!</p>
        <button @click="googleLogout">logout</button>
    </div>

</template>


<script>
import axios from 'axios';
import { account } from '../appwrite';      //importanje appwrite account objekta

export default {
    data() {
        return {
            username: '',       //username za obicni login
            password: '',
            error: '',
            selected: '',       //odabir uloge
            options: ["volonter", "organizacija", "admin"],
            isLoggedIn: false,
            userName: '',       //username za google login
            account: account,       //instanca appwrite objekta
        };
    },
    methods: {
        async login() {       //login preko unosa podataka
            this.error = '';  //resetira error poruke na default(blank)

            if (!this.username || !this.password || !this.selected) {
                this.error = 'Molimo unesite ime i lozinku.';
                return;
            }

            try {   // saljem post request za login backendu
                const response = await axios.post('http://localhost:8080/api/auth/login', {
                    username: this.username,
                    password: this.password
                });

                // spremanje tokena kojeg dobijemo u localStorage
                //localStorage.setItem('token', response.data.token);
                alert('Uspješna prijava');
                this.isLoggedIn = true;
                this.$router.push('/');

            } catch (error) {
                console.error('greška u prijavi', error);
            }
        },

      async googleLogin() {
        try {
          console.log("Starting Google OAuth login...");

          await this.account.createOAuth2Session(
              'google',
              'http://localhost:5173',
              'http://localhost:5173/fail'
          );
          console.log("Uspostavljam session...");
          const session = await this.account.getSession('current');
          console.log('Current session:', session);

// If the session is valid, fetch the user
          if (session) {
            const user = await this.account.get();
            console.log('User retrieved:', user);
            this.userName = user.name;
            this.isLoggedIn = true;
            this.$router.push({ path: '/dashboard' });
          }

          // Start Google OAuth session
          await this.account.createOAuth2Session(
              'google',
              'http://localhost:5173',  // Success URL
              'http://localhost:5173/fail'  // Failure URL
          );

          console.log("Session created. Attempting to fetch user details...");

          // Fetch user details immediately
          const user = await this.getUser();

          console.log("Dohvacen user?");
          if (user) {
            console.log("User retrieved:", user);

            // Send user data to backend for token generation or additional verification
            const response = await axios.post('http://localhost:8080/api/auth/google-login', {
              name: user.name,
              email: user.email
            });

            // Store token in local storage if backend provides it
            localStorage.setItem('token', response.data.token);
            alert('Successful login');

            this.isLoggedIn = true;
            this.userName = user.name;

            // Optional: Navigate to another page after login
            this.$router.push({ path: '/dashboard' });
          } else {
            console.error("User data not available.");
          }
        } catch (error) {
          console.error("Login error:", error);
          alert("An unexpected error occurred during login.");

          if (error.response && error.response.status === 401) {
            // If backend response indicates an unregistered user, prompt to register
            alert(error.response.data);
            this.$router.push('/register');
          } else {
            alert("An unexpected error occurred during login.");
          }
        }
      }
      ,
      async getUser() {
            console.log("pozvana metoda getUser");
            try {
              const user = await this.account.get(); // Fetches user details if session exists
              this.userName = user.name;
              this.isLoggedIn = true;
              return user;
            } catch (error) {
              console.error('Error fetching user:', error);
              this.isLoggedIn = false;
              return null;
            }
        },
        googleLogout() {
            this.isLoggedIn = false;
            this.userName = ''; // isprazni informacije o korisniku (spremno za novog)
            this.account.deleteSession('current');      //brisane sessiona
        },

        mounted() {         // appwrite sam stvara session, mounted interacta s third-party bibliotekama
            if (this.$route.query.name && this.$route.query.email) {
                this.userName = this.$route.query.name;
                this.username = this.$route.query.email;
                this.isLoggedIn = true;
            }    //zelimo da se u formi automatski napune podatci ispunjeni u google prijavi                     
        },
    },
};
</script>
