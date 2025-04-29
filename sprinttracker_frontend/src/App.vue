<template>
  <div id="app">
    <header class="app-header">
      <h1>DSR Sprint Tracker</h1>
      <img src="@/assets/PT_logo.svg" alt="Logo" class="app-logo" />
    </header>
    <JiraIssuesTable />
  </div>
</template>

<script>
import JiraIssuesTable from './components/JiraIssuesTable.vue';

export default {
  name: 'App',
  components: {
    JiraIssuesTable
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  background: #e7e9f1;
}

.app-header {
  background: #2E346F;
  color: white;
  text-align: center;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.app-header h1 {
  margin: 0;
  font-size: 2.5em;
}

.app-logo {
  height: 150px;
  width: 150px;
  object-fit: contain;
}
</style>