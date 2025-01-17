<template>
    <div>
      <h2>Iskustva i ciljevi</h2>
      <div v-if="isProfileOwner">
        <button @click="showNewBiljeskaForm = true">Nova bilješka</button>
        <div v-if="showNewBiljeskaForm" class="new-biljeska-form">
          <textarea v-model="newBiljeskaContent" placeholder="Unesite novu bilješku"></textarea>
          <button @click="createBiljeska">Objavi</button>
        </div>
      </div>
      <div v-if="biljeske.length">
        <div v-for="biljeska in biljeske" :key="biljeska.id" class="biljeska">
          <div v-if="editingBiljeskaId === biljeska.id">
            <textarea v-model="editingBiljeskaContent"></textarea>
            <button @click="saveBiljeska(biljeska.id)">Spremi promjene</button>
          </div>
          <div v-else>
            <p>{{ biljeska.content }}</p>
            <div v-if="isProfileOwner">
              <button @click="editBiljeska(biljeska)">Uredi</button>
              <button @click="deleteBiljeska(biljeska.id)">Izbriši</button>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <p>Nema bilješki.</p>
      </div>
      <div v-if="showEditConfirmation" class="edit-confirmation">
        <p>Već uređujete bilješku</p>
        <button @click="saveCurrentAndEditNew">Spremi promjene</button>
        <button @click="discardCurrentAndEditNew">Nemoj spremiti promjene</button>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    name: 'Biljeske',
    props: {
      username: {
        type: String,
        required: true
      }
    },
    data() {
      return {
        biljeske: [], // Polje za pohranu bilješki
        currentUsername: '', // Korisničko ime trenutnog korisnika
        isProfileOwner: false, // Provjera je li korisnik vlasnik profila
        showNewBiljeskaForm: false, // Vidljivost forme za novu bilješku
        newBiljeskaContent: '', // Sadržaj nove bilješke
        editingBiljeskaId: null, // ID bilješke koja se uređuje
        editingBiljeskaContent: '', // Sadržaj bilješke koja se uređuje
        showEditConfirmation: false, // Vidljivost potvrde za uređivanje
        newEditingBiljeska: null // Nova bilješka koja se uređuje
      };
    },
    async created() {
      await this.fetchCurrentUsername(); // Dohvati korisničko ime trenutnog korisnika
      this.isProfileOwner = this.currentUsername === this.username; // Provjeri je li korisnik vlasnik profila
      this.fetchBiljeske(); // Dohvati bilješke za korisnika
    },
    methods: {
      async fetchCurrentUsername() {
        try {
          const token = localStorage.getItem('token'); // Dohvati token iz localStorage
          if (!token) {
            throw new Error('Nema tokena');
          }
          const response = await axios.get('http://localhost:8080/api/user', {
            headers: { Authorization: `Bearer ${token}` }
          }); // Dohvati podatke o trenutnom korisniku s API-ja
          this.currentUsername = response.data.username; // Postavi korisničko ime trenutnog korisnika
        } catch (error) {
          console.error('Error fetching current username:', error); // Ispis pogreške ako dohvaćanje korisničkog imena ne uspije
        }
      },
      async fetchBiljeske() {
        try {
          const response = await axios.get(`http://localhost:8080/api/biljeske?username=${this.username}`); // Dohvati bilješke za korisnika
          this.biljeske = response.data.reverse(); // Postavi dohvaćene bilješke u obrnutom redoslijedu
        } catch (error) {
          console.error('Error fetching biljeske:', error); // Ispis pogreške ako dohvaćanje bilješki ne uspije
        }
      },
      async createBiljeska() {
        try {
          const token = localStorage.getItem('token'); // Dohvati token iz localStorage
          const response = await axios.post('http://localhost:8080/api/biljeske', {
            content: this.newBiljeskaContent
          }, {
            headers: { Authorization: `Bearer ${token}` }
          }); // Pošalji novu bilješku na backend
          this.biljeske.unshift(response.data); // Dodaj novu bilješku na početak liste
          this.newBiljeskaContent = ''; // Resetiraj sadržaj nove bilješke
          this.showNewBiljeskaForm = false; // Sakrij formu za novu bilješku
        } catch (error) {
          console.error('Error creating biljeska:', error); // Ispis pogreške ako stvaranje bilješke ne uspije
        }
      },
      editBiljeska(biljeska) {
        if (this.editingBiljeskaId !== null) {
          this.newEditingBiljeska = biljeska; // Postavi novu bilješku koja se uređuje
          this.showEditConfirmation = true; // Prikaži potvrdu za uređivanje
        } else {
          this.editingBiljeskaId = biljeska.id; // Postavi ID bilješke koja se uređuje
          this.editingBiljeskaContent = biljeska.content; // Postavi sadržaj bilješke koja se uređuje
        }
      },
      async saveBiljeska(id) {
        try {
          const token = localStorage.getItem('token'); // Dohvati token iz localStorage
          const response = await axios.put(`http://localhost:8080/api/biljeske/${id}`, {
            content: this.editingBiljeskaContent
          }, {
            headers: { Authorization: `Bearer ${token}` }
          }); // Pošalji ažuriranu bilješku na backend
          const index = this.biljeske.findIndex(biljeska => biljeska.id === id);
          this.$set(this.biljeske, index, response.data); // Ažuriraj bilješku u lokalnom stanju
          this.editingBiljeskaId = null; // Resetiraj ID bilješke koja se uređuje
          this.editingBiljeskaContent = ''; // Resetiraj sadržaj bilješke koja se uređuje
          this.showEditConfirmation = false; // Sakrij potvrdu za uređivanje
        } catch (error) {
          console.error('Error saving biljeska:', error); // Ispis pogreške ako spremanje bilješke ne uspije
        }
      },
      async deleteBiljeska(id) {
        try {
          const token = localStorage.getItem('token'); // Dohvati token iz localStorage
          await axios.delete(`http://localhost:8080/api/biljeske/${id}`, {
            headers: { Authorization: `Bearer ${token}` }
          }); // Pošalji zahtjev za brisanje bilješke
          this.biljeske = this.biljeske.filter(biljeska => biljeska.id !== id); // Ažuriraj lokalno stanje bilješki
        } catch (error) {
          console.error('Error deleting biljeska:', error); // Ispis pogreške ako brisanje bilješke ne uspije
        }
      },
      saveCurrentAndEditNew() {
        this.saveBiljeska(this.editingBiljeskaId); // Spremi trenutnu bilješku
        this.editBiljeska(this.newEditingBiljeska); // Uredi novu bilješku
        this.showEditConfirmation = false; // Sakrij potvrdu za uređivanje
      },
      discardCurrentAndEditNew() {
        this.editingBiljeskaId = null; // Resetiraj ID bilješke koja se uređuje
        this.editingBiljeskaContent = ''; // Resetiraj sadržaj bilješke koja se uređuje
        this.editBiljeska(this.newEditingBiljeska); // Uredi novu bilješku
        this.showEditConfirmation = false; // Sakrij potvrdu za uređivanje
      }
    }
  };
  </script>
  
  <style scoped>
  .biljeska {
    margin-bottom: 1em;
    padding: 1em;
    border: 1px solid #ccc;
    border-radius: 5px;
    background-color: #f6f4d2;
  }
  
  .new-biljeska-form,
  .feedback-form {
    margin-top: 10px;
  }
  
  .edit-confirmation {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background-color: white;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 5px;
    z-index: 1000;
  }
  
  button {
    margin-top: 10px;
    padding: 10px;
    background-color: #4c8139;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #3a6b2d;
  }
  </style>