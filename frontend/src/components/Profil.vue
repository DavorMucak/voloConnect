<template>
  <!-- prikaz podataka o korisniku -->
  <div v-if="!urediDetalje">
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
    <button v-if="!urediDetalje" @click="pokreniUredivanje">Promijeni detalje</button>
  
    <div v-if="urediDetalje">
      <div>
        <label>Email:</label>
        <input v-model="privremeniPodaci.email" />
      </div>
      <div>
        <label>Ime:</label>
        <input v-model="privremeniPodaci.name" />
      </div>
      <div>
        <label>Prezime:</label>
        <input v-model="privremeniPodaci.surname" />
      </div>
    <div>
      <label>Broj telefona:</label>
      <input v-model="privremeniPodaci.phonenum" />
    </div>
      <button @click="spremiPromjene">Spremi</button>
      <button @click="prekiniUredivanje">Odustani</button>
    </div>
  </div>
  <!-- kad se pritisne na ovo ide prikaz projekata na koje je korisnik prijavljen(volonter) ili cije je vlasnik (organizacija) -->
  <div v-if="uloga !== 'admin'">

    <!-- prikaz biljezaka i recenzija -->
    <div v-if="uloga === 'volonter'">
      <Biljeske :username="korisnik.username"/>
    </div>

    <Recenzije :username="korisnik.username"/>

    <!-- ako je korisnik organizacija, ima opciju izrade novog projekta -->
    <router-link v-if="uloga === 'organizacija'" to="/novi-projekt"> <button>Novi projekt</button> </router-link>

    <!-- <router-link :to="{ name: 'ListaProjekata', params: { username: korisnik.username } }"> <button>Moji projekti</button> </router-link> -->

    <button @click="obrisiProfil">Obriši profil</button>
    <!-- prituzba botun -->
    <button v-if="!prituzbiranje" @click="prikaziFormu = true">Prijavite Pritužbu</button>
    <div v-if="prikaziFormu">
      <label for="username">Ime korisnika:</label>
      <input type="text" id="username" v-model="prituzba.username" placeholder="Unesite ime korisnika" required />

      <label for="opis">Opis problema:</label>
      <textarea id="opis" v-model="prituzba.opis" placeholder="Unesite opis problema" required></textarea>

      <button @click="submitComplaint">Pošaljite Pritužbu</button>
      <button @click="cancelComplaint">Odustani</button>
    </div>

  </div>


  <!-- ako je korisnik admin, moze vidit i prituzbe i registracije -->
  <div v-if="uloga === 'admin'">
    <Prituzbe />
    <RegistracijeAdmina />
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
      privremeniPodaci: {},
      uloga: '',
      korisnickoIme: ' ',
      urediDetalje: false,
      prikaziFormu: false,
      prituzba: {
        username: '', 
        opis: ''
      },
    };
  },
  methods: {
    cancelComplaint() {
      this.prikaziFormu = false;
      this.prituzba.username= '';
      this.prituzba.opis = '';
    },
    async submitComplaint() {
      if (!this.prituzba.username || !this.prituzba.opis) {
        alert('Molimo popunite sve podatke!');
        return;
      }
      const complaint= {
        plaintiffUsername: this.korisnickoIme, 
        defendantUsername: this.prituzba.username,
        description: this.prituzba.opis,
      };
      apiClient.post('http://localhost:8080/api/prituzbe', complaint)
        .then(response => {
          alert('Complaint submitted successfully!');
          this.cancelComplaint();
        })
        .catch(error => {
          console.error('Greska pri slanju prituzbe:', error);
          alert('Greska pri slanju prituzbe.');
        });
      
    },
    pokreniUredivanje() {
      this.privremeniPodaci = { ...this.korisnik };
      this.urediDetalje = true;
    },
    prekiniUredivanje() {
      this.urediDetalje = false;
    },
    async spremiPromjene() {
      try {
        const response = await apiClient.put(`http://localhost:8080/api/user/${this.korisnik.username}`, {
          email: this.privremeniPodaci.email,
          name: this.privremeniPodaci.name,
          surname: this.privremeniPodaci.surname,
          phonenum: this.privremeniPodaci.phonenum,
        });
        this.korisnik={ ...this.privremeniPodaci };
        this.urediDetalje = false;
      } catch (error) {
        console.error('Greška pri spremanju promjena:', error);
      }
    },
    async fetchKorisnik() {
      try {
        // dohvat podataka o prijavljenon korisniku
        const token = localStorage.getItem("token");
        if (token) {    //ako postoji token korisnik je prijavljen
          
          //sprema username
          this.korisnickoIme = VueJwtDecode.decode(token).sub;
          this.uloga = VueJwtDecode.decode(token).role;
          this.korisnik.role = VueJwtDecode.decode(token).role;
          this.korisnik.username = VueJwtDecode.decode(token).sub;

          const response = await apiClient.get(`http://localhost:8080/api/user/${this.korisnik.username}`);
          console.log(response.data);
          
          Object.assign(this.korisnik, response.data);
        }

      } catch (error) {
        console.error('Greska u dohvavanju podataka:', error);
      }
    },
    jeLiMojProfil() {
      //provjerava je li ovo profil od prijavljenog korisnika
      if (this.korisnickoIme === this.$route.params.username) {
        return true;
      } else {
        return false;
      }
    },
    async obrisiProfil() {   
      const confirmation = window.confirm("Jeste li sigurni da želite obrisati svoj profil? Bit će trajno izbrisan.");
      if (confirmation) {
        try {
          const token = localStorage.getItem('token');
          const response = await axios.delete('http://localhost:8080/api/auth/delete-account', {
            headers: { Authorization: `Bearer ${token}` },
          });

          alert(response.data.message);  // poruka (uspjeh)

          localStorage.removeItem('token');
          localStorage.removeItem('role');
          localStorage.removeItem('userID');
          this.isLoggedIn = false;  // vise nije ulogiran
          this.$router.push('/login'); // redirectaj na home

        } catch (error) {
          console.error('Greška u brisanju profila', error);
          alert('Došlo je do greške pri brisanju profila. Molimo pokušajte ponovno.');
        }
      }
    }
  },
  async created() {
    await this.fetchKorisnik();
  },
};
</script>