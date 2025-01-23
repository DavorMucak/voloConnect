<template>
  <div>
    <h2>Recenzije</h2>
    <div v-if="reviews.length">
      <div v-for="review in reviews" :key="review.id" class="recenzija-pair">
        <router-link :to="`/projekt/${encodeURIComponent(review.projectName)}`"><h3>{{ review.projectName }}</h3></router-link> <!-- ime projekta koji veze volontera i organizaciju -->

        <div class="recenzija"> <!-- sto je volonter napisao o organizaciji -->
          <p>
            <router-link :to="`/profil/${review.volonteerUsername}`">{{ review.volonteerUsername }}</router-link>
            >
            <router-link :to="`/profil/${encodeURIComponent(review.orgUsername)}`">{{ review.orgUsername }}</router-link>
          </p>
          <p>{{ review.review.description }}</p>
          <p><strong>Ocjena:</strong> {{ review.review.grade }}</p>

          <div v-if="!review.feedback && currentUser.username === username && currentUser.role === 'organizacija'"> <!-- organizacije mogu dati povrate informacije volonterima -->
            <button @click="feedback.id = review.id">Odgovori na recenziju</button>
            <div v-if="review.id === feedback.id" class="feedback-form">
              <textarea v-model="feedback.description" :placeholder="`Opišite iskustvo s korisnikom ${review.volonteerUsername}`"></textarea>
              <input v-model="feedback.grade" type="number" min="1" max="5" placeholder="Ocjena" />
              <button @click="submitFeedback(review.id)">Objavi</button>
            </div>
          </div>
        </div>
        
        <div v-if="review.feedback" class="feedback"> <!-- povratne informacije koje je organizacija dala volonteru -->
          <p>
            <router-link :to="`/profil/${encodeURIComponent(review.orgUsername)}`">{{ review.orgUsername }}</router-link>
            >
            <router-link :to="`/profil/${review.volonteerUsername}`">{{ review.volonteerUsername }}</router-link>
          </p>
          <p>{{ review.feedback.description }}</p>
          <p><strong>Ocjena:</strong> {{ review.feedback.grade }}</p>
        </div>
      </div>
    </div>
    <div v-else>
      <p>Nema recenzija.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiClient from '@/apiClient';

export default {
  name: 'Recenzije',
  props: {
    username: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      reviews: [], // recenzije koje je korisnik ciji se profil pregledava ostavio/dobio
      currentUser: {username: '', role: ''}, // podaci o trenutnom korisniku
      feedback: { // privremeni podaci o novim povratnim informacijama
        id: '',
        description: '',
        grade: ''
      }
    }
  },
  created() {
    this.fetchUser();
    this.fetchReviews();
  },
  methods: {
    fetchUser() { // dohvaca treutnog korisnika
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
    fetchReviews() { // dohvaca recenzije
      apiClient.get(`http://localhost:8080/api/recenzije/${this.username}`)
        .then(response => {
          this.reviews = response.data;
        })
        .catch(error => {
          console.error('Error fetching reviews:', error);
        });
    },
    submitFeedback(id) { // sprema nove povratne informacije lokalno i u bazu
      apiClient.put(`http://localhost:8080/api/recenzije/${this.username}`, {
          id: id,
          feedback: {description: this.feedback.description, grade: this.feedback.grade}
        })
        .then(() => {
          const index = this.reviews.findIndex(review => review.id === id);
          this.reviews[index].feedback = { // prikaz promjena
            description: this.feedback.description,
            grade: this.feedback.grade
          };
          this.feedback = { id: '', description: '', grade: '' };
        })
        .catch(error => {
          console.error('Error submitting review:', error);
        });
    }
  }
};
</script>

<style scoped>
.project-group {
  margin-bottom: 2em;
}

.recenzija-pair {
  margin-bottom: 2em;
}

.recenzija {
  margin-bottom: 1em;
  padding: 1em;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f6f4d2;
}

.feedback {
  margin-left: 20px; /* Pomakni malo udesno */
  background-color: #e0e0e0; /* Malo drugačija boja pozadine */
  font-size: 0.9em; /* Manji font */
  padding: 1em;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.feedback-form {
  margin-top: 10px;
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