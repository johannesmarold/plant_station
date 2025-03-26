<script setup>
import { ref, onMounted } from 'vue'
import HelloWorld from './components/HelloWorld.vue'
import TheWelcome from './components/TheWelcome.vue'

const latestData = ref([])

async function loadLatestData() {
    await fetch('/plant_station/data?' + new URLSearchParams({name: '2025-03-21',}).toString(), 
    {method: 'GET'})
    .then(res => {return res.json()})
    .then(data => {latestData.value = data})
    .catch(error => {console.error('Fetch error using loadLatestData():', error.message);});
}

async function loadLatestDHT() {
    await fetch('/plant_station/latest', 
    {method: 'GET'})
    .then(res => {console.log(res)})
    .catch(error => {console.error('Fetch error using loadLatestDHT():', error.message);});
}

onMounted(() => {
  loadLatestData()
  console.log(latestData)
})


</script>

<template>
  <header>
    <img alt="Vue logo" class="logo" src="./assets/logo.svg" width="125" height="125" />

    <div class="wrapper">
      <HelloWorld msg="You did it!" />
    </div>
  </header>

  <main>
    <p>latestData</p>
    <TheWelcome />
  </main>
</template>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>
