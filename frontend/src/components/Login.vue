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
                const response = await axios.post('https://voloconnect.onrender.com/api/auth/login', {
                    username: this.username,
                    password: this.password
                });

                // spremanje tokena kojeg dobijemo u localStorage
                localStorage.setItem('token', response.data.token);
                alert('Uspješna prijava');
                this.isLoggedIn = true;
                this.$router.push('/');

            } catch (error) {
                console.error('greška u prijavi', error);
            }
        },

        async googleLogin() {       //login preko googlea
            try {
                await this.account.createOAuth2Session(     //stvaranje google oauth sessiona
                    'google',       //oauth provider
                    'https:///voloconnectview.onrender.com',        //link na koji se redirecta nakon autorizacije
                    'http://voloconnectview.onrender.com/fail'    //ne postoji trenutno
                );

                const user = await this.getUser();      //dobavi podatke o korisniku nakon google logina

                const response = await axios.post('voloconnectview.onrender.com//api/auth/login', {      //backend server za handlanje autha
                    name: user.name,    // podatci koje uzimamo od google prijave
                    email: user.email
                });

                // spremanje tokena kojeg dobijemo u localStorage
                localStorage.setItem('token', response.data.token);
                alert('Uspješna prijava');
                this.isLoggedIn = true;

                //redirectanje na /login ali s podatcima o korisniku u queryju
                this.$router.push({ path: '/login', query: { name: user.name, email: user.email } });

            } catch (error) {
                console.error('greška u prijavi', error);
            }
        },

        async getUser() {
            try {
                const user = await this.account.get();      //dobavi podatke prijavljenog korisnika
                this.userName = user.name; // updateaj podataka o korisniku i otvori prozor prijave
                this.isLoggedIn = true;     //postavi login state (korisnik prijavljen)
                return user;
            } catch (error) {
                console.error('greška pri dohvaćanju korisnika', error);
                this.isLoggedIn = false;
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
