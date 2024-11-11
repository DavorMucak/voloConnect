<template>
    <div class="container">
        <h2>Login</h2>

        <form @submit.prevent="login">
            <select v-model="selected" class="selection">
                <option disabled value="">uloga</option>
                <option v-for="option in options" :key="option" :value="option">
                    {{ option }}
                </option>
            </select>

            <p v-if="selected == 'admin' || selected == 'volonter'">
                <input type="text" v-model="username" placeholder="korisničko ime" />
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

    <div id="login-screen" ref="loginScreen" v-if="!isLoggedIn" class="container">
        <button id="login-btn" @click="googleLogin">Prijavi se Google profilom</button>
    </div>

    <div id="profile-screen" ref="profileScreen" v-if="isLoggedIn" class="hidden">
        <p>Bok, <span id="user-name">{{ userName }}</span>!</p>
        <button id="logout-btn" @click="googleLogout">logout</button>
    </div>

</template>


<script>
import { account } from '../appwrite';      //importanje appwrite account objekta

export default {
    data() {
        return {
            username: '',
            password: '',
            error: '',
            selected: '',
            options: ["volonter", "organizacija", "admin"],
            isLoggedIn: false,
            userName: '',       //username za google login
            account: account,       //instanca appwrite objekta
        };
    },
    methods: {
        login() {       //login preko unosa podataka
            this.error = '';  //resetira error poruke na default(blank)

            if (!this.username || !this.password || !this.selected) {
                this.error = 'Molimo unesite ime i lozinku.';
                return;
            }

            if (this.username === 'user' && this.password === 'sifra') {
                alert('Uspješna prijava');
            } else {
                this.error = 'Pogrešno korisničko ime ili lozinka.';
            }
        },

        async googleLogin() {       //login preko googlea
            try {
                await this.account.createOAuth2Session(
                    'google',       //oauth provider
                    'http://localhost:5173/login',        //link na koji se redirecta nakon autorizacije
                    'http://localhost:5173/fail'    //ne postoji trenutno
                );

                await this.getUser();   // uzimanje informacija o korisniku
            } catch (error) {
                console.error('gtrešla u prijavi', error);
            }
        },
        async getUser() {
            try {
                const user = await this.account.get();      //dobavi podatke prijavljenog korisnika
                this.userName = user.name; // updateaj podataka o korisniku i otvori prozor prijave
                this.isLoggedIn = true;     //postavi login state (korisnik prijavljen)
            } catch (error) {
                console.error('greška pri dohvaćanju korisnika', error);
                this.isLoggedIn = false;
                this.renderLoginScreen();
            }
        },
        googleLogout() {
            this.isLoggedIn = false;
            this.userName = ''; // isprazni informacije o korisniku (spremno za novog)
            this.account.deleteSession('current');      //brisane sessiona
        },
        renderLoginScreen() {
            this.isLoggedIn = false;
        },
        renderProfileScreen(user) {
            this.userName = user.name;
            this.isLoggedIn = true;
        },
        mounted() {                 // appwrite sam stvara session, mounted interacta s third-party bibliotekama
            this.getUser();         // pmoramo samo provjeriti je li korisnik vec ulogiran,
        },                          // to se radi tako da funkcija getUser pokusa dobaviti podatke o korisniku s appwritea
    },
};
</script>
