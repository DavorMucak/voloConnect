projekt.vue 
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
      <p><strong>Organizacija:</strong> {{ projekt.korisnickoIme}}</p>
      <!-- ako je korisnik volonter moze se prijaviti na projekt, ili odjaviti ako je vec prijavljen -->
      <div v-if="uloga === 'volonter'">
        <button v-if="!provjeriPrijavu()" @click="prijavaProjekt">Prijavi se!</button>
        <button v-else @click="odjavaProjekt">Odjavi projekt</button>
      </div>
      <div v-if="uloga === 'organizacija' && jeLiMojProjekt()">
        <button v-if="!urediDetalje" @click="pokreniUredivanje">Promijeni detalje</button>

        <div v-if="urediDetalje">
          <label for="opisProjekta">Opis projekta:</label>
          <textarea id="opisProjekta" v-model="privremeniPodaci.opisProjekta"></textarea>

          <label for="brojLjudi">Broj ljudi:</label>
          <input type="number" id="brojLjudi" v-model="privremeniPodaci.brojLjudi" />

          <label for="vrstaAktivnosti">Vrsta aktivnosti:</label>
          <input type="text" id="vrstaAktivnosti" v-model="privremeniPodaci.vrstaAktivnosti" />
            
          <button @click="spremiPromjene">Spremi</button>
          <button @click="prekiniUredivanje">Odustani</button>
        </div>
      </div>
    </div>
  </div>
  <div v-else>
    <p>Nema projekata.</p>
  </div>
</template>


<script>
import axios from 'axios';

export default {
  props: {
    imeProjekta: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      projekti: [],
      projekt: null,
      error: null,
      uloga: 'organizacija',
      korisnickoIme: ' ',
      privremeniPodaci: {},
      urediDetalje: false,
    };
  },
  computed: {
    //ime je formatirano radi slanja backendu pa ga vracamo na normalno
    formatiranoIme() {
      return this.imeProjekta
          .replace(/-/g, ' ')
          .replace(/\b\w/g, char => char.toUpperCase());
    },
  },
  async created() {
    try {
      //ovo odkomentirat kad se spaja s backendon:
      const response = await axios.get('http://localhost:8080/api/projects');
      this.projekti = response.data;  
      /* const token = localStorage.getItem("token");

      this.uloga=localStorage.getItem('role'); */
      //pronalazi projekt tako da iz liste projekata nade onaj koji se poklapa s imenom projekta
      this.projekt = this.projekti.find(
          (projekt) => projekt.imeProjekta === this.imeProjekta.replace(/\s+/g, '-').toLowerCase()
      );
      console.log(this.projekt);
      
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
      this.urediDetalje = true;
    },
    prekiniUredivanje() {
      this.urediDetalje = false;
    },
    async spremiPromjene() {
      try {
        const response = await axios.put('http://localhost:8080/api/projects', {
          imeProjekta: this.privremeniPodaci.imeProjekta.replace(/\s+/g, '-').toLowerCase(),
          opisProjekta: this.privremeniPodaci.opisProjekta,
          brojLjudi: this.privremeniPodaci.brojLjudi,
          datumPoc: this.privremeniPodaci.datumPoc,
          datumKraj: this.privremeniPodaci.datumKraj,
          jeLiHitno: this.privremeniPodaci.jeLiHitno,
          korisnickoIme: this.privremeniPodaci.korisnickoIme
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
        const response = await axios.post('http://localhost:8080/api/signup',
            { 
              korisnik: this.korisnickoIme,
              id: this.projekt.id 
            }
        );
        alert("Uspješna prijava projekta!");
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
    async odjavaProjekt() {
      try {
        await axios.delete("http://localhost:8080/api/odjavaprojekta", {
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
    async provjeriPrijavu(){
      try {
        // dohvati popis prijava za projekt
        const response = await axios.get(`http://localhost:8080/api/projects/${this.projekt.projectId}/signups`);
        
        // provjeri je li logirani korisnik prijavljen na projekt
        const korisnikPrijavljen = response.data.some(signup => signup.korisnickoIme === this.korisnickoIme);
        return korisnikPrijavljen;
      } catch (error) {
        console.error('Greška pri provjeri prijave:', error);
        return false; // ako dode do greske, pretpostavljamo da korisnik nije prijavljen
      }
    },
    jeLiMojProjekt(){
      return this.korisnickoIme && this.projekt && this.projekt.korisnickoIme === this.korisnickoIme;
    },
  },
};
</script>