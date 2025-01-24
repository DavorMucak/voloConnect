<template>
  <div  v-if="projekt" class="home-container">
    <h2>{{ formatiranoIme }}</h2>
    <div v-if="error" class="error">{{ error }}</div>      
      <div v-if="projekt && !urediDetalje">
      <!-- prikaz detalja projekta -->
      <p>{{ projekt.opisProjekta }}</p>
      <p><strong>Broj ljudi:</strong> {{ projekt.brojLjudi }}</p>
      <p><strong>Datum početka:</strong> {{ projekt.datumPoc }}</p>
      <p><strong>Datum kraja:</strong> {{ projekt.datumKraj }}</p>
      <p><strong>Vrsta aktivnosti:</strong> {{ projekt.vrstaAktivnosti }}</p>
      <p><strong>Organizacija:</strong> {{ projekt.ownerId}}</p>
      <div v-if="uloga === 'volonter'">
        <button @click="prijavaProjekt()">Prijavi se!</button>
      <!--  <button v-else @click="odjavaProjekt()">Odjavi projekt</button>-->
      </div>
      <!-- !!!!ako je korisnik organizacija=> uredi podatke + vidi prijavljene ako je njihov projekt inace view only -->
      <div v-if="provjeriVlasnika()">
        <button @click="dohvatiPrijave()">Dohvati Prijave</button>
        <div v-if="applications.length" class="application-box">
          <div v-for="application in applications" :key="application.id" class="prijava">
            <div v-if="!(application.status === 'declined')">
              <router-link :to="`/profil/${application.userName}`">{{ application.userName }}</router-link>
              <p v-if="application.status === 'accepted'">Korisnik je prijavljen na projekt</p>
              <button v-if="application.status === 'waiting'" @click="odobriPrijavu(application.id)">Odobri prijavu</button>
              <button v-if="application.status === 'waiting'" @click="odbaciPrijavu(application.id)">Odbaci prijavu</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <p>Nema prijava.</p>
    </div>
    <div v-if="provjeriVlasnika()">
      <div v-if="uloga === 'organizacija' && jeLiMojProjekt()">
          <button v-if="!urediDetalje" @click="pokreniUredivanje()">Promijeni detalje</button>
        </div>
        <div v-if="urediDetalje">
          <div>
            <label for="opisProjekta">Opis projekta:</label>
            <textarea id="opisProjekta" v-model="privremeniPodaci.opisProjekta"></textarea>
          </div>
          <div>
            <label for="brojLjudi">Broj ljudi:</label>
            <input type="number" id="brojLjudi" v-model="privremeniPodaci.brojLjudi" />
          </div>
          <div>
            <select id="vrstaAktivnosti" v-model="privremeniPodaci.vrstaAktivnosti">
              <option value="Administrativni poslovi">Administrativni poslovi</option>
              <option value="Fizički poslovi">Fizički poslovi</option>
              <option value="Podučavanje">Podučavanje</option>
              <option value="Kreativni poslovi">Kreativni poslovi</option>
              <option value="Informatičke usluge">Informatičke usluge</option>
              <option value="Ostalo">Ostalo</option>
            </select>
          </div>
            <button @click="spremiPromjene">Spremi</button>
            <button @click="prekiniUredivanje">Odustani</button>
         </div>
    </div>
  </div>
</template>


<script>
import axios from 'axios';
import VueJwtDecode from 'vue-jwt-decode';

export default {
  props: {
    projektId: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      // privremeni projekti dok ne povezemo s backendon
      projekti: [],
      projekt: {
        imeProjekta: "",
        opisProjekta: "",
        brojLjudi: 0,
        datumPoc: null,
        datumKraj: null,
        jeLiHitno: false,
        ownerId: "",
        vrstaAktivnosti: "",
      },
      error: null,
      uloga: ' ',
      korisnickoIme: ' ',
      privremeniPodaci: {
        imeProjekta: "",
        opisProjekta: "",
        brojLjudi: 0,
        datumPoc: null,
        datumKraj: null,
        jeLiHitno: false,
        ownerId: "",
        vrstaAktivnosti: "",
      },
      urediDetalje: false,
      applications: [],
      application: null
    };
  },
  computed: {
    //ime je formatirano radi slanja backendu pa ga vracamo na normalno
    formatiranoIme() {
      return this.projekt.imeProjekta
          .replace(/-/g, ' ')
          .replace(/\b\w/g, char => char.toUpperCase());
    },
  },
  async created() {
    try {
      await this.fetchKorisnik();
      //ovo odkomentirat kad se spaja s backendon:
      const response = await axios.get(`https://voloconnect.onrender.com/api/projects/${this.projektId}`);
      this.projekt = response.data;      
      const token = localStorage.getItem("token");

      this.uloga=localStorage.getItem('role');
      /*
      //pronalazi projekt tako da iz liste projekata nade onaj koji se poklapa s imenom projekta
      this.projekt = this.projekti.find(
          (projekt) => projekt.id === this.projektId
      );
      */
      console.log("projekt: " + this.projekt);
      
      if (!this.projekt) {
        throw new Error("Project not found");
      }
    } catch (error) {
      this.error = error.response ? error.response.data : 'Ne može se dohvatiti projekt.';
    }
  },
  methods: {
    pokreniUredivanje() {
      this.privremeniPodaci = { ...this.projekt };
      console.log("Privremeni podaci za uređivanje:", this.privremeniPodaci);
      this.urediDetalje = true;
      console.log("urediDetalje postavljen na:", this.urediDetalje);
    },
    prekiniUredivanje() {
      this.urediDetalje = false;
    },
    async spremiPromjene() {
      try {
        const response = await axios.put('https://voloconnect.onrender.com/api/projects', {
          imeProjekta: this.privremeniPodaci.imeProjekta.replace(/\s+/g, '-').toLowerCase(),
          opisProjekta: this.privremeniPodaci.opisProjekta,
          brojLjudi: this.privremeniPodaci.brojLjudi,
          datumPoc: this.privremeniPodaci.datumPoc,
          datumKraj: this.privremeniPodaci.datumKraj,
          jeLiHitno: this.privremeniPodaci.jeLiHitno,
          ownerId: this.korisnickoIme
        });
        this.projekt={ ...this.privremeniPodaci };
        this.urediDetalje = false;
      } catch (error) {
        console.error('Greška pri spremanju promjena:', error);
      }
    },
    async prijavaProjekt() {
      try {
        //kad korisnik stisne prijava onda se salje id backendu
        //!!!!tribalo bi jos poslat koji profil se prijavljuje??? znaci id korisnika i projekta??? not sure
        console.log("Prijavljujem usera: " + localStorage.getItem('userID'));
        const response = await axios.post(`https://voloconnect.onrender.com/api/projects/${this.projekt.id}/apply`,
            {
              userId: localStorage.getItem('userID')
            }
        );
        alert("Uspješna prijava!");
      } catch (error) {
        alert(
            "Greška."
        );
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
          this.isLoggedIn = true;   // korisnik prijavljen
        }

      } catch (error) {
        console.error('Greska u dohvavanju podataka:', error);
        this.isLoggedIn = false;    // error -> korisnik nije prijavljen
      }
    },
    /*
    async odjavaProjekt() {
      try {
        this.korisnickoIme = localStorage.getItem("username");
        await axios.delete("https://voloconnect.onrender.com/api/odjavaprojekta", {
          korisnik: this.korisnickoIme,
          id: this.projekt.id 
        });
        alert("Uspješna odjava projekta!");
        this.projekt.prijavljen = false;

      } catch (error) {
        console.error('Greška u odjavi projekta', error);
        alert('Došlo je do greške pri odjavi projekta. Molimo pokušajte ponovno.');
      }
    },
    */
    /*async provjeriPrijavu(){
      
      try {
        // dohvati popis prijava za projekt
        const response = await axios.get(`https://voloconnect.onrender.com/api/projects/${this.projectId}/signups`);
        
        // provjeri je li logirani korisnik prijavljen na projekt
        const korisnikPrijavljen = response.data.some(signup => signup.korisnickoIme === this.korisnickoIme);
        console.log(korisnikPrijavljen + "korisnik prijavljen");
        return korisnikPrijavljen;
      } catch (error) {
        console.error('Greška pri provjeri prijave:', error);
        return false; // ako dode do greske, pretpostavljamo da korisnik nije prijavljen
      }
    },*/
    jeLiMojProjekt(){
      console.log("Boolean misterij: " + this.korisnickoIme && this.projekt && this.projekt.ownerId === this.korisnickoIme);
      console.log("Boolean misterij: " + this.korisnickoIme);
      console.log("Boolean misterij 2: " + this.projekt.ownerId);
      
      
      return this.korisnickoIme && this.projekt && this.projekt.ownerId === this.korisnickoIme;
    },
    provjeriVlasnika() {
      const currentUserId = localStorage.getItem("userID");
      if (localStorage.getItem("role") !== 'organizacija' && currentUserId !== this.projekt.ownerId)
        return false;
      return true;
    },

    async dohvatiPrijave() {
      try {
        const response = await axios.get(`https://voloconnect.onrender.com/api/projects/${this.projekt.id}/applications`);
        console.log(response.data);
        this.applications = response.data;

      } catch (error) {
        console.error(error);
      }
    },

    async odobriPrijavu(id) {
      try{
        const response = await axios.post(`https://voloconnect.onrender.com/api/projects/${this.projekt.id}/${id}/accept`);
        const applicationIndex = this.applications.findIndex(application => application.id === id);
        if (applicationIndex !== -1)
          this.applications[applicationIndex].status = 'accepted';
        alert("Uspješno odobrena prijava!");
      } catch (error) {
        alert("Greška pri odobravanju prijave.");
        console.error(error);
      }
    },
    async odbaciPrijavu(id) {
      try{
        const response = await axios.post(`https://voloconnect.onrender.com/api/projects/${this.projekt.id}/${id}/decline`);
        const applicationIndex = this.applications.findIndex(application => application.id === id);
        if (applicationIndex !== -1) {
          this.applications[applicationIndex].status = 'declined';
        }
        alert("Uspješno odbačena prijava!");
      } catch (error) {
        alert("Greška pri odbacivanju prijave.");
        console.error(error);
      }
    }
  },
};
</script>
<script setup lang="ts">
</script>

<style scoped>
.application-box {
  border: 1px solid black;
  padding: 10px;
  margin: 10px;
  background-color: #e5e2a2;
  border-radius: 8px;
  text-align: center;
  font-weight: bold;
}

button {
  padding: 7px;
  margin-left: 7px;
  margin-top: 7px;
  margin-bottom: 30px;
  background-color: #aeae53;
  color: black;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #3a6b2d;
}

textarea {
  width: 90%;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid black;
  border-radius: 4px;
  background-color: #f6f4d2;
}

input[type="number"] {
  width: 90%;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid black;
  border-radius: 4px;
  background-color: #f6f4d2;
}
</style>