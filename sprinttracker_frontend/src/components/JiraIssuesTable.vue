<template>
  <div>
    <div class="button-container">
      <button @click="downloadTableAsExcel" class="download-button">
        Download {{ sprints[selectedSprintId - 1]?.name || '' }} to Excel
      </button>
    </div>
    <div>
      <select v-model="selectedSprintId" @change="fetchIssues" class="custom-select">
        <option v-for="sprint in [...sprints].reverse()" :key="sprint.id" :value="sprint.id">{{ sprint.name }}</option>
      </select>
    </div>
    <div>
      <label><input type="checkbox" v-model="filters.evt"/> EVT</label>
      <label><input type="checkbox" v-model="filters.se"/> SE</label>
      <label><input type="checkbox" v-model="filters.re"/> RE</label>
      <label><input type="checkbox" v-model="filters.dm"/> DM</label>
      <label><input type="checkbox" v-model="filters.qa"/> QA</label>
    </div>
    <div class="cards">
      <div v-if="filters.evt || noFilters" class="card">
        <h3>EVT</h3>
        <p>Sprint Estimated/Completed: {{ getEstimatedCompletedString(totalEstimations.evt, sprintCompleted.evt) }}</p>
        <p>Late Estimated/Completed: {{ getEstimatedCompletedString(lateEstimations.evt, lateCompleted.evt) }}</p>
        <p>Completed outside of sprint: {{ completedOutsideSprint.evt }}</p>
        <p>Total Completed: {{ totalCompleted.evt }}</p>
        <p>Time logged sprint/non-sprint:<br>{{ formatTime(totalTimeLoggedSprint.evt) }} /
          {{ formatTime(totalTimeLoggedNonSprint.evt) }}</p>
      </div>
      <div v-if="filters.se || noFilters" class="card">
        <h3>SE</h3>
        <p>Sprint Estimated/Completed: {{ getEstimatedCompletedString(totalEstimations.se, sprintCompleted.se) }}</p>
        <p>Late Estimated/Completed: {{ getEstimatedCompletedString(lateEstimations.se, lateCompleted.se) }}</p>
        <p>Completed outside of sprint: {{ completedOutsideSprint.se }}</p>
        <p>Total Completed: {{ totalCompleted.se }}</p>
        <p>Time logged sprint/non-sprint:<br>{{ formatTime(totalTimeLoggedSprint.se) }} /
          {{ formatTime(totalTimeLoggedNonSprint.se) }}</p>
      </div>
      <div v-if="filters.re || noFilters" class="card">
        <h3>RE</h3>
        <p>Sprint Estimated/Completed: {{ getEstimatedCompletedString(totalEstimations.re, sprintCompleted.re) }}</p>
        <p>Late Estimated/Completed: {{ getEstimatedCompletedString(lateEstimations.re, lateCompleted.re) }}</p>
        <p>Completed outside of sprint: {{ completedOutsideSprint.re }}</p>
        <p>Total Completed: {{ totalCompleted.re }}</p>
        <p>Time logged sprint/non-sprint:<br>{{ formatTime(totalTimeLoggedSprint.re) }} /
          {{ formatTime(totalTimeLoggedNonSprint.re) }}</p>
      </div>
      <div v-if="filters.dm || noFilters" class="card">
        <h3>DM</h3>
        <p>Sprint Estimated/Completed: {{ getEstimatedCompletedString(totalEstimations.dm, sprintCompleted.dm) }}</p>
        <p>Late Estimated/Completed: {{ getEstimatedCompletedString(lateEstimations.dm, lateCompleted.dm) }}</p>
        <p>Completed outside of sprint: {{ completedOutsideSprint.dm }}</p>
        <p>Total Completed: {{ totalCompleted.dm }}</p>
        <p>Time logged sprint/non-sprint:<br>{{ formatTime(totalTimeLoggedSprint.dm) }} /
          {{ formatTime(totalTimeLoggedNonSprint.dm) }}</p>
      </div>
      <div v-if="filters.qa || noFilters" class="card">
        <h3>QA</h3>
        <p>Sprint Estimated/Completed: {{ getEstimatedCompletedString(totalEstimations.qa, sprintCompleted.qa) }}</p>
        <p>Late Estimated/Completed: {{ getEstimatedCompletedString(lateEstimations.qa, lateCompleted.qa) }}</p>
        <p>EVT Estimated/Completed: {{ qaEstimations.evt }} / {{ qaCompleted.evt }}</p>
        <p>SE Estimated/Completed: {{ qaEstimations.se }} / {{ qaCompleted.se }}</p>
        <p>RE Estimated/Completed: {{ qaEstimations.re }} / {{ qaCompleted.re }}</p>
        <p>DM Estimated/Completed: {{ qaEstimations.dm }} / {{ qaCompleted.dm }}</p>
        <p>QA Estimated/Completed: {{ qaEstimations.qa }} / {{ qaCompleted.qa }}</p>
        <p>Completed outside of sprint: {{ completedOutsideSprint.qa }}</p>
        <p>Total Completed: {{ totalCompleted.qa }}</p>
        <p>Time logged sprint/non-sprint:<br>{{ formatTime(totalTimeLoggedSprint.qa) }} /
          {{ formatTime(totalTimeLoggedNonSprint.qa) }}</p>
      </div>
    </div>
    <h2>Sprint Issues</h2>
    <div class="table-container">
      <table>
        <thead>
        <tr>
          <th @click="sortBy('id')">ID</th>
          <th @click="sortBy('title')">Title</th>
          <th @click="sortBy('priority')">Priority</th>
          <th @click="sortBy('epic')">Epic</th>
          <th @click="sortBy('team')">Team</th>
          <th @click="sortBy('issueType')">Issue Type</th>
          <th @click="sortBy('status')">Status</th>
          <th v-if="filters.evt || noFilters" @click="sortBy('evt_estimated')">EVT Estimated</th>
          <th v-if="filters.evt || noFilters" @click="sortBy('evt_logged')">EVT Logged</th>
          <th v-if="filters.evt || noFilters" @click="sortBy('evt_completed')">EVT Completed</th>
          <th v-if="filters.evt || noFilters" @click="sortBy('evt_correct')">EVT Correct (Dif)</th>
          <th v-if="filters.se || noFilters" @click="sortBy('se_estimated')">SE Estimated</th>
          <th v-if="filters.se || noFilters" @click="sortBy('se_logged')">SE Logged</th>
          <th v-if="filters.se || noFilters" @click="sortBy('se_completed')">SE Completed</th>
          <th v-if="filters.se || noFilters" @click="sortBy('se_correct')">SE Correct (Dif)</th>
          <th v-if="filters.re || noFilters" @click="sortBy('re_estimated')">RE Estimated</th>
          <th v-if="filters.re || noFilters" @click="sortBy('re_logged')">RE Logged</th>
          <th v-if="filters.re || noFilters" @click="sortBy('re_completed')">RE Completed</th>
          <th v-if="filters.re || noFilters" @click="sortBy('re_correct')">RE Correct (Dif)</th>
          <th v-if="filters.dm || noFilters" @click="sortBy('dm_estimated')">DM Estimated</th>
          <th v-if="filters.dm || noFilters" @click="sortBy('dm_logged')">DM Logged</th>
          <th v-if="filters.dm || noFilters" @click="sortBy('dm_completed')">DM Completed</th>
          <th v-if="filters.dm || noFilters" @click="sortBy('dm_correct')">DM Correct (Dif)</th>
          <th v-if="filters.qa || noFilters" @click="sortBy('qa_estimated')">QA Estimated</th>
          <th v-if="filters.qa || noFilters" @click="sortBy('qa_logged')">QA Logged</th>
          <th v-if="filters.qa || noFilters" @click="sortBy('qa_completed')">QA Completed</th>
          <th v-if="filters.qa || noFilters" @click="sortBy('qa_correct')">QA Correct (Dif)</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="issue in initialIssuesWithEstimations" :key="issue.id"
            :class="{ 'completed-status': isCompletedStatus(issue.status) }">
          <td><a :href="`https://jira.playtech.com/browse/${issue.id}`" target="_blank">{{ issue.id }}</a></td>
          <td>{{ issue.title }}</td>
          <td>{{ issue.priority }}</td>
          <td>
            <a v-if="issue.epic" :href="`https://jira.playtech.com/browse/${issue.epic.key}`" target="_blank">
              {{ issue.epic.name }}
            </a>
          </td>
          <td>{{ issue.team }}</td>
          <td>{{ issue.issueType }}</td>
          <td>{{ issue.status }}</td>
          <td v-if="filters.evt || noFilters">{{ getEstimated(issue.evt_estimated, issue.evt_late) }}</td>
          <td v-if="filters.evt || noFilters">{{ formatTime(issue.evt_logged) }}</td>
          <td v-if="filters.evt || noFilters">
            {{ calculateCompleted(issue.evt_estimated, issue.evt_estimated_new, issue.status, 'evt') }}
          </td>
          <td v-if="filters.evt || noFilters" v-html="calculateCorrectEstimationForSprint(issue.evt_logged, issue.status, calculateCompleted(issue.evt_estimated, issue.evt_estimated_new, issue.status, 'evt'), 'evt')"></td>
          <td v-if="filters.se || noFilters">{{ getEstimated(issue.se_estimated, issue.se_late) }}</td>
          <td v-if="filters.se || noFilters">{{ formatTime(issue.se_logged) }}</td>
          <td v-if="filters.se || noFilters">
            {{ calculateCompleted(issue.se_estimated, issue.se_estimated_new, issue.status, 'se') }}
          </td>
          <td v-if="filters.se || noFilters" v-html="calculateCorrectEstimationForSprint(issue.se_logged, issue.status, calculateCompleted(issue.se_estimated, issue.se_estimated_new, issue.status, 'se'), 'se')"></td>
          <td v-if="filters.re || noFilters">{{ getEstimated(issue.re_estimated, issue.re_late) }}</td>
          <td v-if="filters.re || noFilters">{{ formatTime(issue.re_logged) }}</td>
          <td v-if="filters.re || noFilters">
            {{ calculateCompleted(issue.re_estimated, issue.re_estimated_new, issue.status, 're') }}
          </td>
          <td v-if="filters.re || noFilters" v-html="calculateCorrectEstimationForSprint(issue.re_logged, issue.status, calculateCompleted(issue.re_estimated, issue.re_estimated_new, issue.status, 're'), 're')"></td>
          <td v-if="filters.dm || noFilters">{{ getEstimated(issue.dm_estimated, issue.dm_late) }}</td>
          <td v-if="filters.dm || noFilters">{{ formatTime(issue.dm_logged) }}</td>
          <td v-if="filters.dm || noFilters">
            {{ calculateCompleted(issue.dm_estimated, issue.dm_estimated_new, issue.status, 'dm') }}
          </td>
          <td v-if="filters.dm || noFilters" v-html="calculateCorrectEstimationForSprint(issue.dm_logged, issue.status, calculateCompleted(issue.dm_estimated, issue.dm_estimated_new, issue.status, 'dm'), 'dm')"></td>
          <td v-if="filters.qa || noFilters">{{ getEstimated(issue.qa_estimated, issue.qa_late) }}</td>
          <td v-if="filters.qa || noFilters">{{ formatTime(issue.qa_logged) }}</td>
          <td v-if="filters.qa || noFilters">
            {{ calculateCompleted(issue.qa_estimated, issue.qa_estimated_new, issue.status, 'qa') }}
          </td>
          <td v-if="filters.qa || noFilters" v-html="calculateCorrectEstimationForSprint(issue.qa_logged, issue.status, calculateCompleted(issue.qa_estimated, issue.qa_estimated_new, issue.status, 'qa'), 'qa')"></td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="table-container">
      <h2>Other tasks</h2>
      <table>
        <thead>
        <tr>
          <th @click="sortBy('id')">ID</th>
          <th @click="sortBy('title')">Title</th>
          <th @click="sortBy('priority')">Priority</th>
          <th @click="sortBy('epic')">Epic</th>
          <th @click="sortBy('team')">Team</th>
          <th @click="sortBy('issueType')">Issue Type</th>
          <th @click="sortBy('status')">Status</th>
          <th v-if="filters.evt || noFilters" @click="sortBy('evt_logged')">EVT Logged</th>
          <th v-if="filters.evt || noFilters" @click="sortBy('evt_correct')">EVT Correct</th>
          <th v-if="filters.se || noFilters" @click="sortBy('se_logged')">SE Logged</th>
          <th v-if="filters.se || noFilters" @click="sortBy('se_correct')">SE Correct</th>
          <th v-if="filters.re || noFilters" @click="sortBy('re_logged')">RE Logged</th>
          <th v-if="filters.re || noFilters" @click="sortBy('re_correct')">RE Correct</th>
          <th v-if="filters.dm || noFilters" @click="sortBy('dm_logged')">DM Logged</th>
          <th v-if="filters.dm || noFilters" @click="sortBy('dm_correct')">DM Correct</th>
          <th v-if="filters.qa || noFilters" @click="sortBy('qa_logged')">QA Logged</th>
          <th v-if="filters.qa || noFilters" @click="sortBy('qa_correct')">QA Correct</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="issue in issuesWithoutEstimations" :key="issue.id">
          <td><a :href="`https://jira.playtech.com/browse/${issue.id}`" target="_blank">{{ issue.id }}</a></td>
          <td>{{ issue.title }}</td>
          <td>{{ issue.priority }}</td>
          <td>
            <a v-if="issue.epic" :href="`https://jira.playtech.com/browse/${issue.epic.key}`" target="_blank">
              {{ issue.epic.name }}
            </a>
          </td>
          <td>{{ issue.team }}</td>
          <td>{{ issue.issueType }}</td>
          <td>{{ issue.status }}</td>
          <td v-if="filters.evt || noFilters">{{ formatTime(issue.evt_logged) }}</td>
          <td v-if="filters.evt || noFilters">{{
              calculateCorrectEstimation(issue.evt_logged, issue.status, 'evt')
            }}
          </td>
          <td v-if="filters.se || noFilters">{{ formatTime(issue.se_logged) }}</td>
          <td v-if="filters.se || noFilters">{{ calculateCorrectEstimation(issue.se_logged, issue.status, 'se') }}</td>
          <td v-if="filters.re || noFilters">{{ formatTime(issue.re_logged) }}</td>
          <td v-if="filters.re || noFilters">{{ calculateCorrectEstimation(issue.re_logged, issue.status, 're') }}</td>
          <td v-if="filters.dm || noFilters">{{ formatTime(issue.dm_logged) }}</td>
          <td v-if="filters.dm || noFilters">{{ calculateCorrectEstimation(issue.dm_logged, issue.status, 'dm') }}</td>
          <td v-if="filters.qa || noFilters">{{ formatTime(issue.qa_logged) }}</td>
          <td v-if="filters.qa || noFilters">{{ calculateCorrectEstimation(issue.qa_logged, issue.status, 'qa') }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import * as XLSX from 'xlsx';

export default {
  data() {
    return {
      issues: [],
      sprints: [],
      selectedSprintId: null,
      filters: {
        evt: false,
        se: false,
        re: false,
        dm: false,
        qa: false
      },
      sortKey: '',
      sortOrder: 1
    };
  },
  created() {
    this.fetchSprints();
  },
  methods: {
    getEstimatedCompletedString(total, completed) {
      if (total > 0) {
        const percentage = Math.round((completed * 100) / total);
        return `${total} / ${completed} (${percentage}%)`;
      }
      return `${total} / ${completed} (0%)`;
    },
    downloadTableAsExcel() {
      const workbook = XLSX.utils.book_new();

      const teams = ['evt', 'se', 're', 'dm', 'qa'];
      teams.forEach(team => {
        const tableData = this.generateTableDataForTeam(team);
        const worksheet = XLSX.utils.json_to_sheet(tableData);
        XLSX.utils.book_append_sheet(workbook, worksheet, `${team} sprint`);

        const tableDataNonSprint = this.generateExcelTableWithoutEstimationsForTeam(team);
        const worksheetNonSprint = XLSX.utils.json_to_sheet(tableDataNonSprint);
        XLSX.utils.book_append_sheet(workbook, worksheetNonSprint, `${team} other`);
      });

      XLSX.writeFile(workbook, `Sprint_Issues_${this.sprints[this.selectedSprintId - 1].name}.xlsx`);
    },
    generateTableDataForTeam(team) {
      return this.initialIssuesWithEstimationsByFilter({
        evt: team === 'evt',
        se: team === 'se',
        re: team === 're',
        dm: team === 'dm',
        qa: team === 'qa'
      }).map(issue => ({
        ID: 'https://jira.playtech.com/browse/' + issue.id,
        Title: issue.title,
        Priority: issue.priority,
        Epic: issue.epic ? issue.epic.name : '',
        Team: issue.team,
        IssueType: issue.issueType,
        Status: issue.status,
        [`${team.toUpperCase()}_Estimated`]: this.getEstimated(issue[`${team}_estimated`], issue[`${team}_late`]) || '',
        [`${team.toUpperCase()}_Logged`]: this.formatTime(issue[`${team}_logged`]) || '',
        [`${team.toUpperCase()}_Completed`]: this.calculateCompleted(issue[`${team}_estimated`], issue[`${team}_estimated_new`], issue.status, team) || '',
        [`${team.toUpperCase()}_Correct`]: this.calculateCorrect(issue[`${team}_logged`], issue.status, team) || ''
      }));
    },
    generateExcelTableWithoutEstimationsForTeam(team) {
      return this.issuesWithoutEstimationsByFilter({
        evt: team === 'evt',
        se: team === 'se',
        re: team === 're',
        dm: team === 'dm',
        qa: team === 'qa'
      }).map(issue => ({
        ID: 'https://jira.playtech.com/browse/' + issue.id,
        Title: issue.title,
        Priority: issue.priority,
        Epic: issue.epic ? issue.epic.name : '',
        Team: issue.team,
        IssueType: issue.issueType,
        Status: issue.status,
        [`${team.toUpperCase()}_Logged`]: this.formatTime(issue[`${team}_logged`]) || '',
        [`${team.toUpperCase()}_Correct`]: this.calculateCorrectEstimation(issue[`${team}_logged`], issue.status, team) || ''
      }));
    },
    async fetchSprints() {
      try {
        const response = await axios.get('http://localhost:8000/sprints');
        this.sprints = response.data;
        if (this.sprints.length > 0) {
          this.selectedSprintId = this.sprints[this.sprints.length - 1].id;
          await this.fetchIssues();
        }
      } catch (error) {
        console.error('Error fetching sprints:', error);
      }
    },
    async fetchIssues() {
      try {
        const response = await axios.get(`http://localhost:8000/jira-issues?sprintId=${this.selectedSprintId}`);
        this.issues = response.data;
      } catch (error) {
        console.error('Error fetching issues:', error);
      }
    },
    sortBy(key) {
      if (this.sortKey === key) {
        this.sortOrder *= -1;
      } else {
        this.sortKey = key;
        this.sortOrder = 1;
      }
    },
    formatTime(seconds) {
      if (seconds === null) return null;
      //const days = Math.floor(seconds / (8 * 3600));
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      let result = '';
      //if (days > 0) result += `${days}d `;
      if (hours > 0) result += `${hours}h `;
      result += `${minutes}m`;
      return result.trim();
    },
    getEstimated(estimated, late) {
      return late ? `${estimated} LATE` : estimated;
    },
    calculateCompleted(estimated, estimatedNew, status, team) {
      const qaStatuses = ['Test Completed', 'Completed', 'Resolved', 'Closed', 'Cancelled'];
      const defaultStatuses = ['Test Completed', 'Completed', 'In Testing', 'Ready for Test', 'Resolved', 'Closed', 'Cancelled'];

      const statuses = team === 'qa' ? qaStatuses : defaultStatuses;

      if (statuses.includes(status)) {
        return estimated;
      }
      if (estimated !== null) {
        if (estimatedNew === null) {
          return estimated;
        }
        return Math.max(0, estimated - estimatedNew);
      }
      return 0;
    },
    calculateCorrectEstimationForSprint(loggedTime, status, completed, team) {
      const correct = this.calculateCorrect(loggedTime, status, team);

      // Find the closest story point to the correct value
      const storyPoints = [0, 1, 2, 3, 5, 8, 13, 21];
      const closestStoryPoint = storyPoints.reduce((prev, curr) => {
        if (Math.abs(curr - correct) < Math.abs(prev - correct)) {
          return curr;
        } else if (Math.abs(curr - correct) === Math.abs(prev - correct)) {
          return Math.abs(curr - completed) < Math.abs(prev - completed) ? curr : prev;
        } else {
          return prev;
        }
      });

      // Determine the difference in steps between correct and estimated
      const correctIndex = storyPoints.indexOf(closestStoryPoint);
      const completedIndex = storyPoints.indexOf(completed);
      const stepDifference = Math.abs(correctIndex - completedIndex);

      // Determine the color based on the step difference
      let color = 'red';
      if (stepDifference === 0) color = 'green';
      else if (stepDifference === 1) color = '#bdad08';

      return `
    <span>
      ${correct}
      <span style="color: ${color}">
        (${correct - completed})
      </span>
    </span>
  `;
    },
    calculateCorrect(loggedTime, status, team) {
      if (loggedTime === null || loggedTime === 0) return 0;
      if (status === 'Cancelled' && team === null) return 0;

      return Math.ceil(loggedTime / 14400); // 4 hours = 14400 seconds
    },
    calculateCorrectEstimation(loggedTime, status, team) {
      const qaStatuses = ['Test Completed', 'Completed', 'Resolved', 'Closed', 'Cancelled'];
      const defaultStatuses = ['Test Completed', 'Completed', 'In Testing', 'Ready for Test', 'Resolved', 'Closed', 'Cancelled'];

      const statuses = team === 'qa' ? qaStatuses : defaultStatuses;

      if (statuses.includes(status)) {
        return Math.ceil(loggedTime / 14400); // 4 hours = 14400 seconds
      }
      return null;
    },
    isCompletedStatus(status) {
      const qaStatuses = ['Test Completed', 'Completed', 'Resolved', 'Closed', 'Cancelled'];
      const defaultStatuses = ['Test Completed', 'Completed', 'In Testing', 'Ready for Test', 'Resolved', 'Closed', 'Cancelled'];

      const statuses = this.filters.qa || this.noFilters ? qaStatuses : defaultStatuses;

      return statuses.includes(status);
    },
    initialIssuesWithEstimationsByFilter(filters) {
      let filtered = this.issues.filter(issue => {
        return issue.addedLater !== true &&
          (issue.evt_estimated !== null ||
            issue.se_estimated !== null ||
            issue.re_estimated !== null ||
            issue.dm_estimated !== null ||
            issue.qa_estimated !== null) &&
          (this.noFiltersByFilters(filters) ||
            (filters.evt && issue.evt_estimated !== null) ||
            (filters.se && issue.se_estimated !== null) ||
            (filters.re && issue.re_estimated !== null) ||
            (filters.dm && issue.dm_estimated !== null) ||
            (filters.qa && issue.qa_estimated !== null));
      });

      if (this.sortKey) {
        filtered.sort((a, b) => {
          let aValue = a[this.sortKey];
          let bValue = b[this.sortKey];

          if (this.sortKey.includes('completed')) {
            aValue = this.calculateCompleted(a[`${this.sortKey.split('_')[0]}_estimated`], a[`${this.sortKey.split('_')[0]}_estimated_new`], a.status, this.sortKey.split('_')[0]);
            bValue = this.calculateCompleted(b[`${this.sortKey.split('_')[0]}_estimated`], b[`${this.sortKey.split('_')[0]}_estimated_new`], b.status, this.sortKey.split('_')[0]);
          }

          if (this.sortKey.includes('correct')) {
            aValue = this.calculateCorrectEstimationForSprint(a[`${this.sortKey.split('_')[0]}_logged`], a.status, a[`${this.sortKey.split('_')[0]}_estimated`], this.sortKey.split('_')[0]);
            bValue = this.calculateCorrectEstimationForSprint(b[`${this.sortKey.split('_')[0]}_logged`], b.status, b[`${this.sortKey.split('_')[0]}_estimated`], this.sortKey.split('_')[0]);
          }

          let result = 0;
          if (aValue === null) result = -1;
          else if (bValue === null) result = 1;
          else if (aValue > bValue) result = 1;
          else if (aValue < bValue) result = -1;
          return result * this.sortOrder;
        });
      }

      return filtered;
    },
    issuesWithoutEstimationsByFilter(filters) {
      let filtered = this.issues.filter(issue => {
        return issue.addedLater === true &&
          (this.noFiltersByFilters(filters) &&
            (issue.evt_logged !== null || issue.se_logged !== null || issue.re_logged !== null || issue.dm_logged !== null || issue.qa_logged !== null) ||
            (filters.evt && issue.evt_logged !== null) ||
            (filters.se && issue.se_logged !== null) ||
            (filters.re && issue.re_logged !== null) ||
            (filters.dm && issue.dm_logged !== null) ||
            (filters.qa && issue.qa_logged !== null))
          ||
          ((issue.evt_estimated === null &&
              issue.se_estimated === null &&
              issue.re_estimated === null &&
              issue.dm_estimated === null &&
              issue.qa_estimated === null) &&
            (this.noFiltersByFilters(filters) &&
              (issue.evt_logged !== null || issue.se_logged !== null || issue.re_logged !== null || issue.dm_logged !== null || issue.qa_logged !== null) ||
              (filters.evt && issue.evt_logged !== null) ||
              (filters.se && issue.se_logged !== null) ||
              (filters.re && issue.re_logged !== null) ||
              (filters.dm && issue.dm_logged !== null) ||
              (filters.qa && issue.qa_logged !== null)));
      });

      if (this.sortKey) {
        filtered.sort((a, b) => {
          let aValue = a[this.sortKey];
          let bValue = b[this.sortKey];

          if (this.sortKey.includes('completed')) {
            aValue = this.calculateCompleted(a[`${this.sortKey.split('_')[0]}_estimated`], a[`${this.sortKey.split('_')[0]}_estimated_new`], a.status, this.sortKey.split('_')[0]);
            bValue = this.calculateCompleted(b[`${this.sortKey.split('_')[0]}_estimated`], b[`${this.sortKey.split('_')[0]}_estimated_new`], b.status, this.sortKey.split('_')[0]);
          }

          if (this.sortKey.includes('correct')) {
            aValue = this.calculateCorrectEstimation(a[`${this.sortKey.split('_')[0]}_logged`], a.status, this.sortKey.split('_')[0]);
            bValue = this.calculateCorrectEstimation(b[`${this.sortKey.split('_')[0]}_logged`], b.status, this.sortKey.split('_')[0]);
          }

          let result = 0;
          if (aValue === null) result = -1;
          else if (bValue === null) result = 1;
          else if (aValue > bValue) result = 1;
          else if (aValue < bValue) result = -1;
          return result * this.sortOrder;
        });
      }

      return filtered;
    },
    noFiltersByFilters(filters) {
      return !filters.evt && !filters.se && !filters.re && !filters.dm && !filters.qa;
    }
  },
  computed: {
    noFilters() {
      return this.noFiltersByFilters(this.filters)
    },
    totalEstimations() {
      return this.issues.reduce((totals, issue) => {
        if (issue.evt_estimated !== null && !issue.evt_late && !issue.addedLater) totals.evt += issue.evt_estimated;
        if (issue.se_estimated !== null && !issue.se_late && !issue.addedLater) totals.se += issue.se_estimated;
        if (issue.re_estimated !== null && !issue.re_late && !issue.addedLater) totals.re += issue.re_estimated;
        if (issue.dm_estimated !== null && !issue.dm_late && !issue.addedLater) totals.dm += issue.dm_estimated;
        if (issue.qa_estimated !== null && !issue.qa_late && !issue.addedLater) totals.qa += issue.qa_estimated;
        return totals;
      }, {evt: 0, se: 0, re: 0, dm: 0, qa: 0});
    },
    sprintCompleted() {
      return this.issues.reduce((totals, issue) => {
        if (issue.evt_estimated !== null && !issue.evt_late && !issue.addedLater) totals.evt += this.calculateCompleted(issue.evt_estimated, issue.evt_estimated_new, issue.status, 'evt');
        if (issue.se_estimated !== null && !issue.se_late && !issue.addedLater) totals.se += this.calculateCompleted(issue.se_estimated, issue.se_estimated_new, issue.status, 'se');
        if (issue.re_estimated !== null && !issue.re_late && !issue.addedLater) totals.re += this.calculateCompleted(issue.re_estimated, issue.re_estimated_new, issue.status, 're');
        if (issue.dm_estimated !== null && !issue.dm_late && !issue.addedLater) totals.dm += this.calculateCompleted(issue.dm_estimated, issue.dm_estimated_new, issue.status, 'dm');
        if (issue.qa_estimated !== null && !issue.qa_late && !issue.addedLater) totals.qa += this.calculateCompleted(issue.qa_estimated, issue.qa_estimated_new, issue.status, 'qa');
        return totals;
      }, {evt: 0, se: 0, re: 0, dm: 0, qa: 0});
    },
    completedOutsideSprint() {
      return this.issuesWithoutEstimations.reduce((totals, issue) => {
        if (issue.evt_logged !== null) totals.evt += this.calculateCorrectEstimation(issue.evt_logged, issue.status, 'evt');
        if (issue.se_logged !== null) totals.se += this.calculateCorrectEstimation(issue.se_logged, issue.status, 'se');
        if (issue.re_logged !== null) totals.re += this.calculateCorrectEstimation(issue.re_logged, issue.status, 're');
        if (issue.dm_logged !== null) totals.dm += this.calculateCorrectEstimation(issue.dm_logged, issue.status, 'dm');
        if (issue.qa_logged !== null) totals.qa += this.calculateCorrectEstimation(issue.qa_logged, issue.status, 'qa');
        return totals;
      }, {evt: 0, se: 0, re: 0, dm: 0, qa: 0});
    },
    totalCompleted() {
      return {
        evt: this.sprintCompleted.evt + this.completedOutsideSprint.evt + this.lateCompleted.evt,
        se: this.sprintCompleted.se + this.completedOutsideSprint.se + this.lateCompleted.se,
        re: this.sprintCompleted.re + this.completedOutsideSprint.re + this.lateCompleted.re,
        dm: this.sprintCompleted.dm + this.completedOutsideSprint.dm + this.lateCompleted.dm,
        qa: this.sprintCompleted.qa + this.completedOutsideSprint.qa + this.lateCompleted.qa
      };
    },
    lateEstimations() {
      return this.issues.reduce((total, issue) => {
        if (issue.evt_late && issue.evt_estimated !== null) {
          total.evt += issue.evt_estimated;
        }
        if (issue.se_late && issue.se_estimated !== null) {
          total.se += issue.se_estimated;
        }
        if (issue.re_late && issue.re_estimated !== null) {
          total.re += issue.re_estimated;
        }
        if (issue.dm_late && issue.dm_estimated !== null) {
          total.dm += issue.dm_estimated;
        }
        if (issue.qa_late && issue.qa_estimated !== null) {
          total.qa += issue.qa_estimated;
        }
        return total;
      }, {evt: 0, se: 0, re: 0, dm: 0, qa: 0});
    },
    lateCompleted() {
      return this.issues.reduce((total, issue) => {
        if (issue.evt_late && issue.evt_estimated !== null) {
          total.evt += this.calculateCompleted(issue.evt_estimated, issue.evt_estimated_new, issue.status, 'evt');
        }
        if (issue.se_late && issue.se_estimated !== null) {
          total.se += this.calculateCompleted(issue.se_estimated, issue.se_estimated_new, issue.status, 'se');
        }
        if (issue.re_late && issue.re_estimated !== null) {
          total.re += this.calculateCompleted(issue.re_estimated, issue.re_estimated_new, issue.status, 're');
        }
        if (issue.dm_late && issue.dm_estimated !== null) {
          total.dm += this.calculateCompleted(issue.dm_estimated, issue.dm_estimated_new, issue.status, 'dm');
        }
        if (issue.qa_late && issue.qa_estimated !== null) {
          total.qa += this.calculateCompleted(issue.qa_estimated, issue.qa_estimated_new, issue.status, 'qa');
        }
        return total;
      }, {evt: 0, se: 0, re: 0, dm: 0, qa: 0});
    },
    qaEstimations() {
      return this.issues.reduce((totals, issue) => {
        if (issue.evt_estimated !== null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.evt += issue.qa_estimated;
        if (issue.se_estimated !== null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.se += issue.qa_estimated;
        if (issue.re_estimated !== null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.re += issue.qa_estimated;
        if (issue.dm_estimated !== null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.dm += issue.qa_estimated;
        if (issue.evt_estimated === null && issue.se_estimated === null && issue.re_estimated === null && issue.dm_estimated === null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.qa += issue.qa_estimated;
        return totals;
      }, {evt: 0, se: 0, re: 0, dm: 0, qa: 0});
    },
    qaCompleted() {
      return this.issues.reduce((totals, issue) => {
        if (issue.evt_estimated !== null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.evt += this.calculateCompleted(issue.qa_estimated, issue.qa_estimated_new, issue.status, 'qa');
        if (issue.se_estimated !== null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.se += this.calculateCompleted(issue.qa_estimated, issue.qa_estimated_new, issue.status, 'qa');
        if (issue.re_estimated !== null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.re += this.calculateCompleted(issue.qa_estimated, issue.qa_estimated_new, issue.status, 'qa');
        if (issue.dm_estimated !== null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.dm += this.calculateCompleted(issue.qa_estimated, issue.qa_estimated_new, issue.status, 'qa');
        if (issue.evt_estimated === null && issue.se_estimated === null && issue.re_estimated === null && issue.dm_estimated === null && !issue.qa_late && !issue.addedLater && issue.qa_estimated !== null) totals.qa += this.calculateCompleted(issue.qa_estimated, issue.qa_estimated_new, issue.status, 'qa');
        return totals;
      }, {evt: 0, se: 0, re: 0, dm: 0, qa: 0});
    },
    totalTimeLoggedSprint() {
      return this.initialIssuesWithEstimations.reduce((totals, issue) => {
        if (issue.evt_logged !== null) totals.evt += issue.evt_logged;
        if (issue.se_logged !== null) totals.se += issue.se_logged;
        if (issue.re_logged !== null) totals.re += issue.re_logged;
        if (issue.dm_logged !== null) totals.dm += issue.dm_logged;
        if (issue.qa_logged !== null) totals.qa += issue.qa_logged;
        return totals;
      }, {evt: 0, se: 0, re: 0, dm: 0, qa: 0});
    },
    totalTimeLoggedNonSprint() {
      return this.issuesWithoutEstimations.reduce((totals, issue) => {
        if (issue.evt_logged !== null) totals.evt += issue.evt_logged;
        if (issue.se_logged !== null) totals.se += issue.se_logged;
        if (issue.re_logged !== null) totals.re += issue.re_logged;
        if (issue.dm_logged !== null) totals.dm += issue.dm_logged;
        if (issue.qa_logged !== null) totals.qa += issue.qa_logged;
        return totals;
      }, {evt: 0, se: 0, re: 0, dm: 0, qa: 0});
    },
    initialIssuesWithEstimations() {
      return this.initialIssuesWithEstimationsByFilter(this.filters)
    },
    issuesWithoutEstimations() {
      return this.issuesWithoutEstimationsByFilter(this.filters)
    }
  }
};
</script>

<style scoped>
h2 {
  color: #2E346F;
}

h3 {
  color: #2E346F;
}

.button-container {
  position: relative;
  width: 100%;
}

.download-button {
  position: absolute;
  top: 0;
  right: 0;
  padding: 5px 10px;
  margin: 30px;
  font-size: 14px;
  color: white;
  background-color: #2E346F;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.download-button:hover {
  background-color: #1d234f;
}

.cards {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin: 16px;
  justify-content: center;
}

.card {
  border: 3px solid #ddd;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card h3 {
  margin-top: 0;
}

.table-container {
  overflow-x: auto;
  margin: 16px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
}

th {
  background-color: #2E346F;
  color: white;
  cursor: pointer;
}

.completed-status {
  background-color: #d4edda;
}

.custom-select {
  width: 100%;
  max-width: 180px;
  padding: 5px;
  margin: 30px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #fff;
  color: #333;
  outline: none;
  cursor: pointer;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.custom-select:hover {
  border-color: #888;
}

.custom-select:focus {
  border-color: #2E346F;
  box-shadow: 0 0 5px rgba(46, 52, 111, 0.5);
}
</style>
