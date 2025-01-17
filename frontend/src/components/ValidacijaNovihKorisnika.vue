<template>
  <div class="new-users">
    <h2>Novi korisnici</h2>
      <div v-if="usersWaiting.length">
        <ul>
          <li v-for="user in usersWaiting">
            <h3></h3>
            <p><strong></strong> {{  }}</p>
            <p><strong></strong> {{  }}</p>
            <p><strong></strong> {{  }}</p>
          </li>
        </ul>
      </div>
      <div v-else>
        <p>Nema korisnika koji čekaju validaciju.</p>
      </div>
  </div>


</template>

<script>
import axios from 'axios';
import apiClient from '@/apiClient';

export default {

  methods: {
    fetchNewUsers(){
      this.usersWaiting = [];

      for(const user of this.users) {
        if(!user.isValidated()){
          this.usersWaiting.push(user);
        }
      }

    }
  },

  async created() {
    try {
      //na pocetku se ne primjenjuje filter nego su svi projekti prikazani
      this.usersWaiting = this.users;
      //dohvat liste usera s backenda
      const response = await apiClient.get('http://localhost:8080/api/validate-users');
      this.users = response.data;
    } catch (error) {
      this.error = error.response ? error.response.data : 'Ne mogu se dohvatiti korisnici.';
    }
  },
};
</script>
}
