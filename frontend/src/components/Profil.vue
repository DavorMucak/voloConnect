<template>
    <!-- kad se pritisne na ovo ide prikaz projekata na koje je korisnik prijavljen(volonter) ili cije je vlasnik (organizacija) -->
    <router-link v-if="uloga !== 'admin'" to="/moji-projekti"> <button>Moji projekti</button> </router-link>
    

    <!-- prikaz biljezaka i recenzija -->
    <Biljeske />

    <Recenzije />

    <!-- ako je korisnik organizacija, ima opciju izrade novog projekta -->
    <router-link v-if="uloga === 'organizacija'" to="/novi-projekt"> <button>Novi projekt</button> </router-link>

    <!-- ako je korisnik admin, moze vidit i prituzbe i registracije -->
    <div v-if="uloga === 'admin'">
      <Prituzbe />
      <Registracije />
    </div>




    <!-- prikaz podataka o korisniku -->
    <div>
      <h2>Podaci o korisniku</h2>
      <p><strong>Korisničko ime:</strong> {{ korisnik.username }}</p>
      <p><strong>Email:</strong> {{ korisnik.email }}</p>
      <p><strong>Ime:</strong> {{ korisnik.name }}</p>
      <p><strong>Prezime:</strong> {{ korisnik.surname }}</p>
      <p><strong>Broj telefona:</strong> {{ korisnik.phone }}</p>
      <p><strong>Uloga:</strong> {{ korisnik.role }}</p>
    </div>
    <!-- ako je ovo profil prijavljenog korisnika moze prominit detalje -->
    <div v-if="jeLiMojProfil()">
      <!-- !!!napravit promjenu detalja -->
      <p>promjeni detalje</p>
    </div>
  </template>  
  
<script>
import axios from 'axios';
import Biljeske from './za_profile/Biljeske.vue';
import Recenzije from './za_profile/Recenzije.vue';
import Prituzbe from './za_profile/Prituzbe.vue';
import Registracije from './za_profile/Registracije.vue';


export default {
  components: {
    Biljeske,
    Recenzije,
    Prituzbe,
    Registracije,
  },
  data() {
    return {
      korisnik: {
        username: '',
        password: '',
        email: '',
        name: '',
        surname: '',
        phonenum: '',
        role: ["volonter", "organizacija", "admin"],
      },
      uloga: '',
      korisnickoIme: ' ',
    };
  },
  methods: {
    async fetchKorisnik() {
      try {
        // dohvat podataka o prijavljenon korisniku
        const token = localStorage.getItem("token");
        const response = await axios.get("http://localhost:8080/api/auth/users", {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.uloga=response.data.role
        this.korisnickoIme=response.data.username
        //!!!!triba dohvatit podatke o useru na kojoj se stranici nalazi
        //!!!ako se prijavljeni korisnik poklapa s time onda je drugacija stranica

        //!!!usporedi username iz headera sa usernameon prijavljenog korisnika


        /* this.korisnik.username = user.username;
        this.korisnik.password = user.password;
        this.korisnik.email = user.email;
        this.korisnik.name = user.name;
        this.korisnik.surname = user.surname;
        this.korisnik.phonenum = user.phonenum;
        this.korisnik.role = user.role; */

      } catch (error) {
        console.error('Greska u dohvavanju podataka:', error);
      }
    },
    jeLiMojProfil(){
      //provjerava je li ovo profil od prijavljenog korisnika
      if(this.korisnickoIme===this.$route.params.username){
        return true;
      }else{
        return false;
      }
    }
  },
  async created() {
    await this.fetchKorisnik();
  },
};
</script>