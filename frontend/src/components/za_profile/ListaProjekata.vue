<template>
  <div v-if="projects.length">
    <div v-for="item in projects" :key="item.project?.id" class="project-box">
      <div v-if="!(isVolonteer === true && currentUser.username !== username)"> <!-- prikazuju se i projekti za koje volonter ceka odobrenje prijave -->
        <router-link :to="`/projekt/${item.project.id}`"> <!-- ime projekta je jedinstveno -->
          <p>{{ item.project.imeProjekta }}</p>
        </router-link>
        <div v-if="isVolonteer">
          <router-link :to="`/profil/${encodeURIComponent(item.project.ownerId)}`"> <!-- imeOrg je efektivno korisnicko ime organizacije -->
            <p>{{ orgText(item.project) }} {{ item.project.ownerId }}</p>
          </router-link>
        </div>

        <p>Od {{ item.project.datumPoc || "N/A" }} do {{ item.project.datumKraj || "N/A" }}</p>
        <p v-if="isProjectUpcoming(item.project) && item.project.jeLiHitno">Hitno!</p>

        <p v-if="isProjectUpcoming(item.project)">Broj sudionika:
          {{ item.project.brojLjudi }}</p> <!-- brojPrijava: broj odobrenih prijava -->
        <p v-else>Broj ljudi trazeno: {{ item.project.brojLjudi }}</p>

        <div v-if="currentUser.username === username && currentUser.role === 'volonter'"> <!-- volonter vidi status prijave na projekt -->
          <p v-if="item.status === 'accepted'">Prijava odobrena</p> <!-- statusPrijave ostaje 'prijavljen' nakon kraja projekta -->
          <p v-else-if="item.status === 'waiting'">Prijava poslana</p>
          <p v-if="item.status === 'declined'">Prijava odbijena</p> <!-- statusPrijave ostaje 'prijavljen' nakon kraja projekta -->

          <!-- Commented out reviews -->
          <!--
          <div v-else-if="!leftReview(item.project.imeProjekta)">
            <button @click="review.projectName = item.project.imeProjekta">Napiši recenziju</button>
            <div v-if="item.project.imeProjekta === review.projectName">
              <textarea v-model="review.description" :placeholder="`Opišite iskustvo s korisnikom ${item.project.ownerId}`"></textarea>
              <input v-model="review.grade" type="number" min="1" max="5" placeholder="Ocjena" />
              <button @click="submitReview(item.project.id)">Objavi</button>
            </div>
          </div>
          -->
        </div>

        <div v-if="currentUser.username === username && currentUser.role === 'organizacija'
          || isVolonteer === false && currentUser.role === 'admin'">
          <button @click="deleteProject(item.project.id)">Obriši projekt</button> <!-- omoguceno organizacijama na vlastitom profilu i adminima -->
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
    username: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      projects: [],
      currentUser: { username: '', role: '' },
      isVolonteer: false,
      // Commented out review-related data
      /*
      review: {
        projectName: '',
        description: '',
        grade: '',
      },
      */
      projectsWithStatus: [],
      error: null,
    };
  },
  created() {
    this.fetchUser();
    this.checkUserRole();
    this.fetchProjects();
  },
  methods: {
    fetchUser() {
      try {
        const token = localStorage.getItem('token');
        if (token) {
          const decodedToken = VueJwtDecode.decode(token);
          this.currentUser.username = decodedToken.sub;
          this.currentUser.role = decodedToken.role;
        }
      } catch (error) {
        console.error('Error fetching current user:', error);
      }
    },
    checkUserRole() {
      if (this.username === this.currentUser.username)
        this.isVolonteer = this.currentUser.role === 'volonter';
      else {
        axios
            .get(`http://localhost:8080/api/user/${this.username}`)
            .then((response) => {
              const profile = response.data;
              this.isVolonteer = profile.role === 'volonter';
            })
            .catch((error) => {
              console.error('Error fetching user:', error);
            });
      }
    },
    fetchProjects() {
      if (this.currentUser.role === 'organizacija') {
        this.fetchOrgProjects();
      } else if (this.currentUser.role === 'volonter') {
        this.fetchVolProjects();
      }
    },
    fetchOrgProjects() {
      axios
          .get(`http://localhost:8080/api/projects/owner/${this.username}`)
          .then((response) => {
            this.projects = [
              ...response.data.map((project) => ({
                ...project,
                status: project.status || null,
              })),
            ];
            console.log('Fetched Organization Projects:', this.projects);
          })
          .catch((error) => {
            console.error('Error fetching organization projects:', error);
          });
    },
    fetchVolProjects() {
      axios
          .get(
              `http://localhost:8080/api/projects/owner/${this.username}/withstatus`
          )
          .then((response) => {
            this.projects = [
              ...response.data.map((project) => ({
                ...project,
                status: project.status || null,
              })),
            ];
            console.log('Fetched Volunteer Projects:', this.projects);
          })
          .catch((error) => {
            this.error = error.response?.data || 'Failed to fetch projects.';
            console.error('Error fetching volunteer projects:', error);
          });
    },
    orgText(project) {
      if(!project) return false;
      const currentDate = new Date();
      const endDate = new Date(project.datumKraj);

      if (endDate < currentDate) {
        return 'Organizirali';
      } else {
        return 'Organizira';
      }
    },
    isProjectUpcoming(project) {
      if (!project) return false;
      const currentDate = new Date();
      const startDate = new Date(project.datumPoc);
      return startDate > currentDate;
    },
    deleteProject(id) {
      const confirmation = window.confirm(
          'Ova akcija se ne može poništiti. Želite li nastaviti?'
      );
      if (confirmation) {
        axios
            .delete(`http://localhost:8080/api/projects/${id}`)
            .then(() => {
              this.projects = this.projects.filter(
                  (project) => project.id !== id
              );
              alert('Projekt je uspješno obrisan.');
            })
            .catch((error) => {
              console.error('Error deleting project:', error);
            });
      }
    },
    /*
    leftReview(name) {
      axios
        .get(`http://localhost:8080/api/recenzije/${this.username}`)
        .then((response) => {
          this.reviews = response.data;
          return this.reviews.some(
            (review) => review.projectName === name
          );
        })
        .catch((error) => {
          console.error('Error fetching reviews:', error);
        });
    },
    submitReview(id) {
      axios
        .put(`http://localhost:8080/api/recenzije/${this.username}`, {
          projectId: id,
          review: {
            description: this.review.description,
            grade: this.review.grade,
          },
        })
        .then(() => {
          this.review = { projectName: '', description: '', grade: '' };
        })
        .catch((error) => {
          console.error('Error submitting review:', error);
        });
    },
    */
  },
};
</script>
