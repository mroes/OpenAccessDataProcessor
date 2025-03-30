# Open Access Data Processor

## About

This software processes publication data from various sources (e.g., FIS/CRIS systems) to be used both for nationwide Open Access monitoring in Austria and to support negotiations with academic publishers.

This software was developed as part of the Austrian Transition to Open Access 2 [AT2OA²](https://at2oa.at/) (2021–2024) project, 
funded by the Austrian Federal Ministry of Education, Science and Research (BMBWF) under the funding program 
"Vorhaben zur digitalen und sozialen Transformation in der Hochschulbildung"

The AT2OA² project, led by the University of Vienna, aimed primarily to advance the transition from closed to Open Access (OA) 
in academic publishing.

## Overview

This project is a full-stack web application. It features a robust Java backend built with Spring Boot and a reactive, responsive frontend built with Vue.js and Quasar Framework using TypeScript.

The frontend is built and packaged into the WAR file.

## Build & Deployment

To build the full application including the frontend and package everything into a `.war` file:

`
mvn clean package
`

The resulting WAR file can be deployed to any compatible servlet container such as **Tomcat 10 or later**.

## Notice About Tests

> ⚠️ **Test Configuration Notice**

- Most of the existing tests are **integration tests**, not isolated unit tests.
- They are **not designed to be run automatically** in CI/CD pipelines.
- Running the tests requires a valid and accessible **database** with the correct structure.
- You must configure a working database connection in your `application.properties` (typically under `src/test/resources/`) for the tests to run successfully.
