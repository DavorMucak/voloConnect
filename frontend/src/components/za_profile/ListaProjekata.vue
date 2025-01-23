<template>
  <div v-if="projects.length">
    <div v-for="project in projects" :key="project.id" class="project-box">
      <div v-if="!(isVolonteer === true && currentUser.username !== username && project.statusPrijave === 'cekas')"> <!-- ne prikazuju se projekti za koje volonter ceka odobrenje prijave -->
        <router-link :to="`/projekti/${encodeURIComponent(project.imeProjekta)}`"> <!-- ime projekta je jedinstveno -->
          <p>{{ project.imeProjekta }}</p>
        </router-link>
        <div v-if="isVolonteer">
          <router-link :to="`/profil/${encodeURIComponent(project.imeOrg)}`"> <!-- imeOrg je efektivno korisnicko ime organizacije -->
            <p>{{ orgText(project) }} {{ project.imeOrg }}</p>
          </router-link>
        </div>

        <p>Od {{ project.datumPoc }} do {{ project.datumKraj }}</p>
        <p v-if="isProjectUpcoming(project) && project.jeLiHitno">Hitno!</p>

        <p v-if="isProjectUpcoming(project)">Broj sudionika:
          {{ project.brojPrijava }} / {{ project.brojLjudi }}</p> <!-- brojPrijava: broj odobrenih prijava -->
        <p v-else>Broj sudionika: {{ project.brojPrijava }}</p>

        <div v-if="currentUser.username === username && currentUser.role === 'volonter'"> <!-- volonter vidi status prijave na projekt -->
          <p v-if="isProjectUpcoming(project) && project.statusPrijave === 'prijavljen'">Prijava odobrena</p> <!-- statusPrijave ostaje 'prijavljen' nakon kraja projekta -->
          <p v-else-if="isProjectUpcoming(project) && project.statusPrijave === 'cekas'">Prijava poslana</p>

          <div v-else-if="!leftReview(project.imeProjekta)">
            <button @click="review.projectName = project.imeProjekta">Napiši recenziju</button> <!-- ako vec nije, volonter moze ostaviti recenziju organizaciji putem projekta koji ih veze -->
            <div v-if="project.imeProjekta === review.projectName">
              <textarea v-model="review.description" :placeholder="`Opišite iskustvo s korisnikom ${project.imeOrg}`"></textarea>
              <input v-model="review.grade" type="number" min="1" max="5" placeholder="Ocjena" />
              <button @click="submitReview(project.id)">Objavi</button>
            </div>
          </div>
        </div>

        <div v-if="currentUser.username === username && currentUser.role === 'organizacija'
          || isVolonteer === false && currentUser.role === 'admin'">
          <button @click="deleteProject(project.id)">Obriši projekt</button> <!-- omoguceno organizacijama na vlastitom profilu i adminima -->
        </div>
      </div>
    </div>
  </div>
  <div v-else>
    <p v-if="currentUser.username === username && currentUser.role === 'organizacija'">Niste objavili nijedan projekt</p>
    <p v-else-if="currentUser.username === username && currentUser.role === 'volonter'">Niste prijavili nijedan projekt</p>
    <p v-else-if="isVolonteer === false">Organizacija nije objavila nijedan projekt</p>
    <p v-else>Korisnik nije sudjelovao ni u jednom projektu</p>
  </div>
</template>

<script>
import VueJwtDecode from 'vue-jwt-decode';
import apiClient from '@/apiClient';
import axios from 'axios';


export default {
  name: 'ListaProjekata',
  props: {
    username: { // korisnicko ime korisnika ciji se projekti pregledavaju
      type: String,
      required: true
    }
  },
  data() {
    return {
      projects: [],
      currentUser: {username: '', role: ''}, // podaci o trenutnom korisniku
      isVolonteer: false, // uloga korisnika ciji se projekti pregledavaju
      review: {
        projectName: '',
        description: '',
        grade: ''
      }
    };
  },
  created() {
    this.fetchUser();
    this.checkUserRole();
    this.fetchProjects();
  },
  methods: {
    fetchUser() { // dohvaca podatke o trenutnom korisniku
      try {
        const token = localStorage.getItem("token");
        if (token) {
          const decodedToken = VueJwtDecode.decode(token);
          this.currentUser.username = decodedToken.sub;
          this.currentUser.role = decodedToken.role;
        }
      } catch (error) {
        console.error('Error fetching current user:', error);
      }
    },
    checkUserRole() { // dohvaca ulogu korisnika ciji se projekti pregledavaju
      if (this.username === this.currentUser.username)
        this.isVolonteer = this.currentUser.role === 'volonter';
      else {
        axios.get(`http://localhost:8080/api/user/${this.username}`)
        .then(response => {
          const profile = response.data;
          this.isVolonteer = profile.role === 'volonter';
        })
        .catch(error => {
          console.error('Error fetching user:', error);
        });
      }
    },
    fetchProjects() { // dohvaca projekte koje je organizacija organizirala/u kojima je volonter sudjelovao
      axios.get(`http://localhost:8080/api/projects/owner/${this.username}`)
        .then(response => {
          this.projects = response.data;
        })
        .catch(error => {
          console.error('Error fetching projects:', error);
        });
    },
    orgText(project) { // formatira izjavu temeljeno na aktualnosti projekta
      const currentDate = new Date();
      const endDate = new Date(project.datumKraj);

      if (endDate < currentDate) {
        return 'Organizirali';
      } else {
        return 'Organizira';
      }
    },
    isProjectUpcoming(project) { // provjerava aktualnost projekta
      const currentDate = new Date();
      const startDate = new Date(project.datumPoc);
      return startDate > currentDate;
    },
    deleteProject(id) { // brise projekt
      const confirmation = window.confirm("Ova akcija se ne može poništiti. Želite li nastaviti?");
      if (confirmation) {
        axios.delete(`http://localhost:8080/api/projects/${id}`)
          .then(() => {
            this.projects = this.projects.filter(project => project.id !== id); // prikaz promjena
            alert('Projekt je uspješno obrisan.');
          })
          .catch(error => {
            console.error('Error deleting project:', error);
          });
      }
    },
    leftReview(name) { // provjerava je li vovlonter vec ostavio recenziju organizaciji za taj projekt
      axios.get(`http://localhost:8080/api/recenzije/${this.username}`)
        .then(response => {
          this.reviews = response.data;
          return this.reviews.some(review => review.projectName === name);
        })
        .catch(error => {
          console.error('Error fetching reviews:', error);
        });
    },
    submitReview(id) { // sprema novu recenziju u bazu
      axios.put(`http://localhost:8080/api/recenzije/${this.username}`, {
          projectId: id,
          review: {description: this.review.description, grade: this.review.grade}
        })
        .then(() => {
          this.review = { projectName: '', description: '', grade: '' };
        })
        .catch(error => {
          console.error('Error submitting review:', error);
        });
    }
  }
};
</script>

<style scoped>
.project-box {
  border: 1px solid #ccc;
  padding: 10px;
  margin: 10px;
}
</style>