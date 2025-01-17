<template>
  <div class="container">
    <h2>Novi projekt</h2>

    <form @submit.prevent="kreirajProjekt">
      <!-- input podataka -->
      <input type="text" v-model="imeProjekta" placeholder="ime projekta" />
      <textarea v-model="opisProjekta" placeholder="opis projekta" rows="5"></textarea>
      <input type="number" v-model="brojLjudi" min="1" placeholder="broj potrebnih ljudi" />
      <label>Datum početka</label>
      <input type="date" v-model="datumPoc" />
      <label>Datum kraja</label>
      <input type="date" v-model="datumKraj" />

      <label>Vrsta aktivnosti</label>
      <select id="vrstaAktivnosti" v-model="vrstaAktivnosti">
        <option value="Administrativni poslovi">Administrativni poslovi</option>
        <option value="Fizički poslovi">Fizički poslovi</option>
        <option value="Podučavanje">Podučavanje</option>
        <option value="Kreativni poslovi">Kreativni poslovi</option>
        <option value="Informatičke usluge">Informatičke usluge</option>
        <option value="Ostalo">Ostalo</option>
      </select>
      <!-- provjera jel datum ispravno unesen -->
      <p v-if="datumGreska" class="error">{{ datumGreska }}</p>

      <br>
      <label>Hitno</label>
      <input type="checkbox" v-model="jeLiHitno" />

      <button type="submit" :disabled="datumError">Kreiraj projekt</button>
      <p class="error" v-if="error">{{ error }}</p>
      <p class="success" v-if="success">{{ success }}</p>
    </form>
  </div>
</template>

<script>
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import VueJwtDecode from 'vue-jwt-decode';
export default {
  data() {
    return {
      imeProjekta: '',
      opisProjekta: '',
      brojLjudi: '',
      datumPoc: '',
      datumKraj: '',
      jeLiHitno: false,
      vrstaAktivnosti: 'Administrativni poslovi',
      ownerId: '',
      error: '',
      success: '',
      datumGreska: '',
    };
  },
  computed: {
    //metoda koja provjerava je li ispravno unesen datum (ogranicenje)
    datumError() {
      this.datumGreska = '';
      if (this.datumPoc) {
        const poc = new Date(this.datumPoc);
        const danas = new Date();
        danas.setHours(0, 0, 0, 0);
        if (poc < danas) {
          this.datumGreska = 'Datum početka ne smije biti u prošlosti.';
          return true;
        }
      }
      if (this.datumPoc && this.datumKraj) {
        const poc = new Date(this.datumPoc);
        const kraj = new Date(this.datumKraj);
        if (poc > kraj) {
          this.datumGreska = 'Datum kraja mora biti nakon datuma početka.';
          return true;
        }
      }
      return false;
    },
  },
  methods: {
    async kreirajProjekt() {
      if (this.datumGreska) {
        //ne da prijavu ako je krivo unesen datum
        this.error = 'Molimo unesite ispravan datum.';
        return;
      }
      //provjera jesu li svi podaci uneseni
      if (!this.imeProjekta || !this.opisProjekta || !this.brojLjudi || !this.datumPoc || !this.datumKraj) {
        this.error = 'Molimo unesite sve informacije.';
        return;
      }
      try {
        //ako je sve okej, salji podatke backendu
        const token = localStorage.getItem('token');
        this.ownerId = VueJwtDecode.decode(token).sub;
        console.log(this.ownerId);
        
        const response = await axios.post('http://localhost:8080/api/projects', {
          imeProjekta: this.imeProjekta,
          opisProjekta: this.opisProjekta,
          brojLjudi: this.brojLjudi,
          datumPoc: this.datumPoc,
          datumKraj: this.datumKraj,
          vrstaAktivnosti:this.vrstaAktivnosti,
          jeLiHitno: this.jeLiHitno,
          ownerId: this.ownerId
        });

        this.success = 'Projekt uspješno kreiran!';
        this.clearForm();
        //!!!treba stavit da se vrati nazad na profil
        this.$router.push('/');
      } catch (error) {
        this.error = error.response ? error.response.data : 'Došlo je do greške.';
      }
    },
    clearForm() {
      //ispraznjava formu
      this.imeProjekta = '';
      this.opisProjekta = '';
      this.brojLjudi = '';
      this.datumPoc = '';
      this.datumKraj = '';
      this.vrstaAktivnosti = 'Administrativni poslovi',
      this.jeLiHitno = false;
      this.error = '';
      this.success = '';
    },
  },
};
</script>