<template>
  <div>
    <h2>Iskustva i ciljevi</h2>
    <div v-if="currentUser.username === username">
      <button @click="showNoteForm = true">Nova bilješka</button> <!-- volonter može dodavat biljeske na vlastiti profil -->
      <div v-if="showNoteForm" class="new-biljeska-form">
        <textarea v-model="newNote" placeholder="Napišite nešto o svojim iskustvima i ciljevima"></textarea>
        <button @click="createNote()">Objavi</button>
      </div>
    </div>

    <div v-if="notes.length">
      <div v-for="note in notes" :key="note.id" class="biljeska">
        <div v-if="note.id === editedNote.id"> <!-- editedNote je biljeska koju uređujemo, ne moze se uređivati vise biljeski odjednom -->
          <textarea v-model="editedNote.content"></textarea>
          <button @click="saveNote(note.id)">Spremi promjene</button>
        </div>
        <div v-else>
          <p>{{ note.content}}</p>
          <button v-if="currentUser.username === username" @click="editNote(note)">Uredi</button> <!-- volonter moze i naknadno urediti objavljene biljeske -->
          <button v-if="currentUser.username === username ||
            currentUser.role === 'admin'" @click="deleteNote(note.id)">Izbriši</button> <!-- volonter cije su biljeske i admini ih mogu brisati -->
        </div>
      </div>
    </div>
    <div v-else>
      <p>Nema bilješki.</p>
    </div>
  </div>
</template>

<script>

import apiClient from '@/apiClient';
import VueJwtDecode from 'vue-jwt-decode';
import axios from 'axios';

export default {
  name: 'Biljeske',
  props: {
    username: { // korisnicko ime korisnika cije su biljeske
      type: String,
      required: true
    }
  },
  data() {
    return {
      notes: [], // biljeske korisnika ciji se profil pregledava
      currentUser: {username: '', role: ''}, // trenutni korisnik
      showNoteForm: false, // vidljivost formulara za novu biljesku
      newNote: '', // sadrzaj nove biljeske
      editedNote: {content: '', id: null} // biljeska koja se uređuje
    };
  },
  created() {
    this.fetchCurrentUser();
    this.fetchNotes();
  },
  methods: {
    fetchCurrentUser() { // dohvaca trenutnog korisnika
      try {
        const token = localStorage.getItem("token");
        if (token) {
          const decodedToken = VueJwtDecode.decode(token);
          this.currentUser.username = decodedToken.sub;
          this.currentUser.role = decodedToken.role;
        }
      } catch (error) {
        console.error('Error fetching current username:', error);
      }
    },
    fetchNotes() { // dohvaca biljeske
      console.log("pokušavam dohvatiti bilješke za: " + this.username);
      
      axios.get(`https://voloconnect.onrender.com/api/biljeske?username=${this.username}`)
          .then(response => {
            this.notes = response.data.reverse();
          })
          .catch(error => {
            console.error('Error fetching notes:', error);
          });
    },
    createNote() { // stvara novu biljesku i sprema ju u bazu podataka
      axios.post(`https://voloconnect.onrender.com/api/biljeske/${this.username}`,
        this.newNote
      )
          .then(response => {
            const note = {
              id: response.data.id, // apiClient request vraca id nove biljeske
              content: this.newNote
            };
            this.notes.unshift(note);
            this.newNote = '';
            this.showNoteForm = false;
          })
          .catch(error => {
            console.error('Error creating note:', error);
          });
    },
    editNote(note) { // omogucuje uređivanje biljeske
      if (this.editedNote.id !== null) { // ako se neka biljeska vec uređuje obavijestava se korisnika
        const confirmation = window.confirm("Već uređujete bilješku. Želite li spremiti promjene i nastaviti?");
        if (confirmation) {
          axios.put(`https://voloconnect.onrender.com/api/biljeske/${this.username}`, { // potvrdom se sprema stara biljeska i omogucuje uređivanje nove
            id: note.id,
            content: this.editedNote.content
          })
              .then(() => {
                this.editedNote = {content: note.content, id: note.id};
              })
              .catch(error => {
                console.error('Error editing note:', error);
              });
        }
      } else {
        this.editedNote = {content: note.content, id: note.id};
      }
    },
    saveNote(id) { // Spremanje promjena nakon uređivanja
      axios.put(`https://voloconnect.onrender.com/api/biljeske/${id}`, {
        content: this.editedNote.content // šaljemo content kao objekat sa ključem 'content'
      })
        .then(() => {
          const index = this.notes.findIndex(note => note.id === id);
          this.notes[index].content = this.editedNote.content; // ažuriraj sadržaj bilješke
          this.editedNote = {content: '', id: null}; // resetiranje editNote
        })
        .catch(error => {
          console.error('Error editing note:', error);
        });
    },
    async deleteNote(id) { // brisanje biljeske
      try {
        await axios.delete(`https://voloconnect.onrender.com/api/biljeske/${this.username}/${id}`); // id ide direktno u URL
        this.notes = this.notes.filter(biljeska => biljeska.id !== id); // prikaz promjena
      } catch (error) {
        console.error('Error deleting note:', error);
      }
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