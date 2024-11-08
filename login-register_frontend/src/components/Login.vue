<template>
    <div class="container">
        <h2>Prijava</h2>

        <form @submit.prevent="login">
            <input type="text" v-model="username" placeholder="korisničko ime" />
            <input type="password" v-model="password" placeholder="lozinka" />

            <select v-model="selected" class="selection">
                <option disabled value="">uloga</option>
                <option v-for="option in options" :key="option" :value="option">
                    {{ option }}
                </option>
            </select>

            <button type="submit">Prijava</button>
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
            error: '',
            selected: '', 
            options: ["volonter", "organizacija", "admin"],
        };
    },
    methods: {
        login() {
            this.error = '';  //resetira error poruke na default(blank)

            if (!this.username || !this.password || !this.selected) {
                this.error = 'Molimo unesite korisničko ime i lozinku.';
                return;
            }

            if (this.username === 'user' && this.password === 'sifra') {
                alert('Uspješna prijava');
            } else {
                this.error = 'Pogrešno korisničko ime ili lozinka.';
            }
        },
    },
};
</script>
