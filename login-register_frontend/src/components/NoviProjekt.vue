<template>
    <div class="container">
      <h2>Novi projekt</h2>
  
      <form @submit.prevent="kreirajProjekt"> 
        <!-- forma za unos podataka -->
        <input type="text" v-model="imeProjekta" placeholder="ime projekta"/>
        <textarea type="text" class="form-grop" v-model="opisProjekta" placeholder="opis projekta" rows="5"></textarea>
        <input type="number" v-model="brojLjudi" min="1" placeholder="broj potrebnih ljudi"/>
        <label>Datum početka</label>
        <input type="date" v-model="datumPoc"/>
        <label>Datum kraja</label>
        <input type="date" v-model="datumKraj"/>

        <!-- error ako je krivo unesen datum-->
        <p v-if="datumGreska" class="error">{{ datumGreska }}</p>

        <label for="slika">Slika</label>
        <input type="file" @change="onFileChange" accept="image/*" />
        <img v-if="imagePreview" :src="imagePreview" alt="Image Preview" class="image-preview" />
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
export default {
  data() {
    return {
      imeProjekta: '',
      opisProjekta: '',
      brojLjudi: '', 
      datumPoc: '',
      datumKraj: '',
      imageFile: null,
      imagePreview: null,
      jeLiHitno: false,
      error: '',
      success: '',
      datumGreska: '',
    };
  },
  computed: {
    datumError() {
      this.datumGreska='';
      if (this.datumPoc) {
        //provjera je li datumpoc u proslosti
        const poc = new Date(this.datumPoc);
        const danas = new Date();
        danas.setHours(0, 0, 0, 0);
        if (poc < danas) {
          this.datumGreska = 'Datum početka ne smije biti u prošlosti.';
        }
      }
      if (this.datumPoc && this.datumKraj) {
        //provjera je li datumkraj poslije datumapoc
        const poc = new Date(this.datumPoc);
        const kraj = new Date(this.datumKraj);
        if (poc > kraj) {
          this.datumGreska = 'Datum kraja mora biti nakon datuma početka.';
        }
      }
    },
  },
  methods: {
    onFileChange(event) {
      //ovo je za kad se uploada file
      const file = event.target.files[0];
      if (file) {
        this.imageFile = file;

        this.imagePreview = URL.createObjectURL(file);
      }
    },
    async kreirajProjekt() {
      if(this.datumGreska){
        this.error = 'Molimo unesite ispravan datum.';
        return;
      }
      // provjera jesu li svi podaci uneseni
      if (!this.imeProjekta || !this.opisProjekta || !this.brojLjudi || 
      !this.datumPoc || !this.datumKraj || !this.brojLjudi || !this.imageFile) {
        this.error = 'Molimo unesite sve informacije.';
        return;
      }
      try {
        // slanje POST requesta backendu
        const response = await axios.post('http://localhost:8080/api/projects', {
          imeProjekta: this.imeProjekta,
          opisProjekta: this.opisProjekta,
          brojLjudi: this.brojLjudi,
          datumPoc: this.datumPoc,
          datumKraj: this.datumKraj, 
          imageFile: this.imageFile,
          jeLiHitno: this.jeLiHitno,
        });

        // ako je uspjesno
        this.success = response.data;
        this.clearForm();
        this.$router.push('/');
      } catch (error) {
        // ako nije uspjesno
        this.error = error.response ? error.response.data : 'Došlo je do greške.';
      }
    },
    clearForm(){
      // resetiranje forme nakon što su poslani podaci
      this.imeProjekta = '';
      this.opisProjekta = '';
      this.brojLjudi = '';
      this.datumPoc = '';
      this.datumKraj = '';
      this.imageFile = null;
      this.imagePreview = null;
      this.jeLiHitno = false;
      this.error = '';
      this.success = '';
    }
  },
};
</script>
