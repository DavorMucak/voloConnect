<template>
  <div>
    <h2>Iskustva i ciljevi</h2>
    <div v-if="currentUser === username">
      <button @click="showNoteForm = true">Nova bilješka</button> <!-- volonter može dodavat biljeske na vlastiti profil -->
      <div v-if="showNoteForm" class="new-biljeska-form">
        <textarea v-model="newNote" placeholder="Napišite nešto o svojim iskustvima i ciljevima"></textarea>
        <button @click="createNote()">Objavi</button>
      </div>
    </div>

    <div v-if="notes.length">
      <div v-for="note in notes" :key="note.id" class="biljeska">
        <div v-if="note.id === editNote.id"> <!-- editNote je biljeska koju uređujemo, ne moze se uređivati vise biljeski odjednom -->
          <textarea v-model="editNote.content"></textarea>
          <button @click="saveNote(note.id)">Spremi promjene</button>
        </div>
        <div v-else>
          <p>{{ note.content }}</p>
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
      editNote: {content: '', id: null} // biljeska koja se uređuje
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
      apiClient.get(`http://localhost:8080/api/biljeske?username=${this.username}`)
          .then(response => {
            this.notes = response.data.reverse();
          })
          .catch(error => {
            console.error('Error fetching notes:', error);
          });
    },
    createNote() { // stvara novu biljesku i sprema ju u bazu podataka
      apiClient.post(`http://localhost:8080/api/biljeske/${this.username}`, {
        sadrzaj: this.newNote
      })
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
      if (this.editNote.id !== null) { // ako se neka biljeska vec uređuje obavijestava se korisnika
        const confirmation = window.confirm("Već uređujete bilješku. Želite li spremiti promjene i nastaviti?");
        if (confirmation) {
          apiClient.put(`http://localhost:8080/api/biljeske/${this.username}`, { // potvrdom se sprema stara biljeska i omogucuje uređivanje nove
            id: note.id,
            sadrzaj: this.editNote.content
          })
              .then(() => {
                editNote = {content: note.content, id: note.id};
              })
              .catch(error => {
                console.error('Error editing note:', error);
              });
        }
      } else {
        editNote = {content: note.content, id: note.id};
      }
    },
    saveNote(id) { // klasicno spremanje promjena nakon uređivanja
      apiClient.put(`http://localhost:8080/api/biljeske/${this.username}`, {
        id: id,
        sadrzaj: this.editNote.content
      })
          .then(() => {
            const index = this.notes.findIndex(note => note.id === id);
            this.notes[index].content = editNote.content; // prikaz promjena
            this.editNote = {content: '', id: null};
          })
          .catch(error => {
            console.error('Error editing note:', error);
          });
    },
    async deleteNote(id) { // brisanje biljeske
      apiClient.delete(`http://localhost:8080/api/biljeske/${this.username}`, {
        id: id
      })
          .then(() => {
            this.notes = this.notes.filter(biljeska => biljeska.id !== id); // prikaz promjena
          })
          .catch(error => {
            console.error('Error deleting note:', error);
          });
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