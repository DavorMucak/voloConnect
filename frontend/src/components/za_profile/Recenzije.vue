<template>
    <div>
      <h2>Recenzije</h2>
      <div v-if="groupedRecenzije.length">
        <div v-for="project in groupedRecenzije" :key="project.projectId" class="project-group">
          <h3>{{ getProjectName(project.projectId) }}</h3>
          <div v-for="pair in project.pairs" :key="pair.recenzija.id" class="recenzija-pair">
            <div class="recenzija">
              <p>
                <router-link :to="`/volonteri/${pair.recenzija.volonteerUsername}`">{{ getUserName(pair.recenzija.volonteerUsername) }}</router-link>
                >
                <router-link :to="`/organizacije/${pair.recenzija.organizationUsername}`">{{ getUserName(pair.recenzija.organizationUsername) }}</router-link>
              </p>
              <p>{{ pair.recenzija.text }}</p>
              <p><strong>Ocjena:</strong> {{ pair.recenzija.grade }}</p>
              <div v-if="!pair.feedback && isProfileOwner && userRole === 'organization'">
                <button @click="showFeedbackForm(pair.recenzija.id)">Odgovori na recenziju</button>
                <div v-if="feedbackFormVisible[pair.recenzija.id]" class="feedback-form">
                  <input v-model="newFeedback.grade" type="number" min="1" max="5" placeholder="Ocjena" />
                  <textarea v-model="newFeedback.info" placeholder="Info"></textarea>
                  <button @click="submitFeedback(pair.recenzija)">Pošalji</button>
                </div>
              </div>
            </div>
            <div v-if="pair.feedback" class="feedback">
              <p>
                <router-link :to="`/organizacije/${pair.feedback.organizationUsername}`">{{ getUserName(pair.feedback.organizationUsername) }}</router-link>
                >
                <router-link :to="`/volonteri/${pair.feedback.volonteerUsername}`">{{ getUserName(pair.feedback.volonteerUsername) }}</router-link>
              </p>
              <p>{{ pair.feedback.info }}</p>
              <p><strong>Ocjena:</strong> {{ pair.feedback.grade }}</p>
            </div>
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
        recenzije: [], // Polje za pohranu recenzija
        feedbacks: [], // Polje za pohranu feedbacka
        groupedRecenzije: [], // Polje za pohranu grupiranih recenzija i feedbacka
        users: {}, // Objekt za pohranu korisničkih podataka
        projects: {}, // Objekt za pohranu podataka o projektima
        userRole: '', // Uloga korisnika (volonter ili organizacija)
        isProfileOwner: false, // Provjera je li korisnik vlasnik profila
        feedbackFormVisible: {}, // Vidljivost forme za feedback
        newFeedback: {
          grade: '',
          info: ''
        } // Novi feedback
      };
    },
    async created() {
      await this.fetchUsers(); // Dohvati sve korisnike
      this.userRole = this.getUserRoleByUsername(this.username); // Dohvati ulogu korisnika prema korisničkom imenu
      this.isProfileOwner = this.username === this.$route.params.username; // Provjeri je li korisnik vlasnik profila
      await this.fetchRecenzije(); // Dohvati recenzije za korisnika
      await this.fetchFeedbacks(); // Dohvati feedback za korisnika
      await this.fetchProjects(); // Dohvati podatke o projektima
      this.groupRecenzije(); // Grupiraj recenzije i feedback
    },
    methods: {
      async fetchUsers() {
        try {
          const response = await axios.get('http://localhost:8080/api/users'); // Dohvati korisničke podatke s API-ja
          this.users = response.data.reduce((acc, user) => {
            acc[user.username] = user; // Pohrani korisničke podatke prema korisničkom imenu
            return acc;
          }, {});
        } catch (error) {
          console.error('Error fetching users:', error); // Ispis pogreške ako dohvaćanje korisničkih podataka ne uspije
        }
      },
      getUserRoleByUsername(username) {
        const user = this.users[username];
        return user ? user.role : ''; // Vrati ulogu korisnika ili prazan string ako korisnik nije pronađen
      },
      async fetchRecenzije() {
        try {
          const response = await axios.get(`http://localhost:8080/api/recenzije?username=${this.username}`); // Dohvati recenzije s API-ja filtrirane prema korisničkom imenu
          this.recenzije = response.data;
        } catch (error) {
          console.error('Error fetching recenzije:', error); // Ispis pogreške ako dohvaćanje recenzija ne uspije
        }
      },
      async fetchFeedbacks() {
        try {
          const response = await axios.get(`http://localhost:8080/api/feedbacks?username=${this.username}`); // Dohvati feedback s API-ja filtriran prema korisničkom imenu
          this.feedbacks = response.data;
        } catch (error) {
          console.error('Error fetching feedbacks:', error); // Ispis pogreške ako dohvaćanje feedbacka ne uspije
        }
      },
      async fetchProjects() {
        try {
          const response = await axios.get('http://localhost:8080/api/projects'); // Dohvati podatke o projektima s API-ja
          this.projects = response.data.reduce((acc, project) => {
            acc[project.id] = project; // Pohrani podatke o projektima prema ID-u
            return acc;
          }, {});
        } catch (error) {
          console.error('Error fetching projects:', error); // Ispis pogreške ako dohvaćanje podataka o projektima ne uspije
        }
      },
      groupRecenzije() {
        const grouped = {};
        if (this.userRole === 'volonter') {
          this.recenzije.forEach(recenzija => {
            const key = `${recenzija.volonteerUsername}-${recenzija.organizationUsername}-${recenzija.projectId}`;
            if (!grouped[key]) {
              grouped[key] = { projectId: recenzija.projectId, pairs: [] };
            }
            grouped[key].pairs.push({ recenzija, feedback: null });
          });
          this.feedbacks.forEach(feedback => {
            const key = `${feedback.volonteerUsername}-${feedback.organizationUsername}-${feedback.projectId}`;
            if (grouped[key]) {
              const pair = grouped[key].pairs.find(pair => pair.recenzija.volonteerUsername === feedback.volonteerUsername && pair.recenzija.organizationUsername === feedback.organizationUsername && pair.recenzija.projectId === feedback.projectId);
              if (pair) {
                pair.feedback = feedback;
              }
            }
          });
          this.groupedRecenzije = Object.values(grouped);
        } else {
          const projects = {};
          this.recenzije.forEach(recenzija => {
            const projectKey = recenzija.projectId;
            if (!projects[projectKey]) {
              projects[projectKey] = { projectId: recenzija.projectId, pairs: [] };
            }
            const key = `${recenzija.volonteerUsername}-${recenzija.organizationUsername}-${recenzija.projectId}`;
            projects[projectKey].pairs.push({ recenzija, feedback: null });
          });
          this.feedbacks.forEach(feedback => {
            const projectKey = feedback.projectId;
            if (projects[projectKey]) {
              const key = `${feedback.volonteerUsername}-${feedback.organizationUsername}-${feedback.projectId}`;
              const pair = projects[projectKey].pairs.find(pair => pair.recenzija.volonteerUsername === feedback.volonteerUsername && pair.recenzija.organizationUsername === feedback.organizationUsername && pair.recenzija.projectId === feedback.projectId);
              if (pair) {
                pair.feedback = feedback;
              }
            }
          });
          this.groupedRecenzije = Object.values(projects);
        }
      },
      getUserName(username) {
        const user = this.users[username];
        if (!user) return 'Unknown';
        return user.role === 'volonter' ? `${user.name} ${user.surname}` : user.name; // Vrati puno ime za volontera ili ime za organizaciju
      },
      getProjectName(id) {
        return this.projects[id] ? this.projects[id].imeProjekta : 'Unknown'; // Dohvati naziv projekta prema ID-u
      },
      showFeedbackForm(recenzijaId) {
        this.$set(this.feedbackFormVisible, recenzijaId, true); // Prikaži formu za feedback
      },
      async submitFeedback(recenzija) {
        try {
          const response = await axios.post('http://localhost:8080/api/feedbacks', {
            grade: this.newFeedback.grade,
            info: this.newFeedback.info,
            volonteerUsername: recenzija.volonteerUsername,
            organizationUsername: recenzija.organizationUsername,
            projectId: recenzija.projectId
          }); // Pošalji novi feedback na backend
          this.feedbacks.push(response.data); // Dodaj novi feedback u lokalno stanje
          this.groupRecenzije(); // Ponovno grupiraj recenzije i feedback
          this.newFeedback = { grade: '', info: '' }; // Resetiraj formu za feedback
          this.$set(this.feedbackFormVisible, recenzija.id, false); // Sakrij formu za feedback
        } catch (error) {
          console.error('Error submitting feedback:', error); // Ispis pogreške ako slanje feedbacka ne uspije
        }
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