# Nuvalence Work Manager

## Prerequisites

Make sure you have the following installed:

1. Java 11+
2. Docker
3. [Camunda Modeler](Make sure you have done the following before you can deploy)
4. Setup and configure minikube (using [This setup](https://github.com/Nuvalence/dsgov-local-environment))

## Run Locally

1. To just spin up the service in `minikube`, run this command: `skaffold run`
2. [view docs](http://api.dsgov.test/wm/swagger-ui/index.html)

The app can be brought down via: `skaffold delete`

## Develop Locally

1. In a standalone terminal, run: `skaffold dev`
2. You should eventually have console output similar to this:
   ![Skaffold Dev 1](docs/assets/skaffold-dev-log-1.png)
   ![Skaffold Dev 2](docs/assets/skaffold-dev-log-2.png)
3. As you make code changes, Skaffold will rebuild the container image and deploy it to your local `minikube` cluster.
4. Once the new deployment is live, you can re-generate your Postman collection to test your new API changes!

To exit `skaffold dev`, in the terminal where you executed the command, hit `Ctrl + C`.

**NOTE: This will terminate your existing app deployment in minikube.**

## Deploying Camunda BPMN diagrams

1. Open the desired BPMN diagram in the Camunda Modeler application
2. Click on the 'Deploy Current Diagram' icon on the bottom-left corner of the window
3. Select a Deployment Name and provide `http://localhost:8080/engine-rest` as the REST Endpoint URL, click 'Deploy'
4. [Camunda Cockpit](http://localhost:8080/camunda/app/cockpit/default/)
5. [Camunda Tasklist](http://localhost:8080/camunda/app/tasklist/default/)
6. Username and Password for Cockpit and Tasklist are admin/admin

## Querying Postgres locally via IntelliJ

1. Open the database tab in the top right
2. Add new datasource `PostgresSQL`
3. Add host as `db.dsgov.test` and the port as `30201`
4. Add your database as `local-work-manager-db`
5. Add your user as `root` and password as `root`
6. Hit apply and save

### Documentation

- [tools and frameworks](./docs/tools.md)

## Contributors

The Nuvalence Work Manager was originally a private project with contributions from:

- [@JayStGelais](https://github.com/JayStGelais)
- [@gcusano](https://github.com/gcusano)
- [@apengu](https://github.com/apengu)
- [@bsambrook](https://github.com/bsambrook)
- [@katt-mim](https://github.com/katt-mim)
- [@dtsong](https://github.com/dtsong)
- [@franklincm](https://github.com/franklincm)
- [@Mark-The-Dev](https://github.com/Mark-The-Dev)
- [@gcastro12](https://github.com/gcastro12)
- [@LPMarin](https://github.com/LPMarin)
