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

    <div v-if="!isLoggedIn" class="container">
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
    methods: {
        async login() {
            this.error = '';

            if (!this.username || !this.password || !this.selected) {
                this.error = 'Molimo unesite ime i lozinku.';
                return;
            }

            try {
                const response = await axios.post('http://localhost:8080/api/auth/google-login', {
                    username: this.username,
                    password: this.password
                });

                localStorage.setItem('token', response.data.token);
                alert('Uspješna prijava');
                this.isLoggedIn = true;
                this.$router.push('/');
            } catch (error) {
                console.error('greška u prijavi', error);
                this.error = 'Neuspješna prijava. Pokušajte ponovno.';
            }
        },

        handleCredentialResponse(response) {
            const idToken = response.credential;
            //console.log(idToken); //test - token je stvoren i primljen je
            console.log("Šaljem token na backend")
            axios.post('http://localhost:8080/api/auth/google-login', {
                idToken
            })
            .then((res) => {
                console.log("Spremam token u localStorage");
                localStorage.setItem('token', res.data.token);
                alert('Uspješna prijava');
                this.isLoggedIn = true;
                this.userName = res.data.name;
                this.$router.push('/');
            })
            .catch((error) => {
                console.error('Greška u prijavi s Googleom', error);
                alert('Dogodila se pogreška pri prijavi s Googleom. Molimo pokušajte ponovo.');
            });
        },

        googleLogout() {
            localStorage.removeItem('token');
            this.isLoggedIn = false;
            this.userName = '';
        }
    },
    mounted() {
        const script = document.createElement('script');
        script.src = 'https://accounts.google.com/gsi/client';
        script.async = true;
        script.defer = true;
        document.head.appendChild(script);

        window.handleCredentialResponse = this.handleCredentialResponse;
    },
};
</script>

