# DSR Sprint Tracker - Sprint Tracking Tool

SprintTracker is a Spring Boot-based application designed to automate the generation of sprint summaries and provide detailed feedback on story point estimation accuracy within a team. It integrates with Jira to track sprint progress, log hours, and generate insights into sprint performance metrics.

## Features
- **Automated Sprint Summary**: Automatically fetches active sprints and tracks progress using Jira APIs.
- **Story Point Estimation Tracking**: Monitors estimated vs. completed story points across all sprints.
- **Time Tracking**: Collects and compares logged time against original estimates for each issue.
- **Periodic Updates**: Configurable interval-based updates of sprint and issue data.
- **Database Storage**: Stores sprint and issue data in a relational database for analysis and future reference.
- **Insights for Sprint Planning**: Provides insights into planning efficiency and estimation accuracy.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Technologies](#technologies)
- [License](#license)

## Prerequisites
- Java 21+
- Maven 3.6+
- Jira API Token for authentication.
- A relational database (PostgreSQL) for storing sprint and issue data.

## Installation
1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/SprintPulse.git
    cd SprintPulse
    ```

2. Build the project:
    ```bash
    mvn clean install
    ```

3. Run the backend application:
    ```bash
    Run the pplication in  SprinttrackerApplication.java class
    ```

4. Run the frontend application:
    ```bash
    cd sprinttracker_frontend
    npm install
    npm run serve
    ```