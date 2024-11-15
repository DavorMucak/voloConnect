<template>
  <div class="container">
    <h2>Novi projekt</h2>

    <form @submit.prevent="kreirajProjekt">
      <input type="text" v-model="imeProjekta" placeholder="ime projekta" />
      <textarea v-model="opisProjekta" placeholder="opis projekta" rows="5"></textarea>
      <input type="number" v-model="brojLjudi" min="1" placeholder="broj potrebnih ljudi" />
      <label>Datum početka</label>
      <input type="date" v-model="datumPoc" />
      <label>Datum kraja</label>
      <input type="date" v-model="datumKraj" />

      <p v-if="datumGreska" class="error">{{ datumGreska }}</p>

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
export default {
  data() {
    return {
      imeProjekta: '',
      opisProjekta: '',
      brojLjudi: '',
      datumPoc: '',
      datumKraj: '',
      jeLiHitno: false,
      error: '',
      success: '',
      datumGreska: '',
    };
  },
  computed: {
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
        this.error = 'Molimo unesite ispravan datum.';
        return;
      }
      if (!this.imeProjekta || !this.opisProjekta || !this.brojLjudi || !this.datumPoc || !this.datumKraj) {
        this.error = 'Molimo unesite sve informacije.';
        return;
      }
      try {
        const response = await axios.post('http://localhost:8080/api/projects', {
          imeProjekta: this.imeProjekta,
          opisProjekta: this.opisProjekta,
          brojLjudi: this.brojLjudi,
          datumPoc: this.datumPoc,
          datumKraj: this.datumKraj,
          jeLiHitno: this.jeLiHitno,
        });

        this.success = 'Projekt uspješno kreiran!';
        this.clearForm();
        this.$router.push('/');
      } catch (error) {
        this.error = error.response ? error.response.data : 'Došlo je do greške.';
      }
    },
    clearForm() {
      this.imeProjekta = '';
      this.opisProjekta = '';
      this.brojLjudi = '';
      this.datumPoc = '';
      this.datumKraj = '';
      this.jeLiHitno = false;
      this.error = '';
      this.success = '';
    },
  },
};
</script>
