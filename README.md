# Reusable Spring Service Template

## Getting Started

### Customization
After creating your project from this template, you'll need to follow these steps:

1. create a new repository using this one as a template
2. update the [swagger.yml](./swagger.yaml) with your api contents
3. modify the project name in...
   - [settings.gradle](./settings.gradle)
   - [skaffold.yaml](./skaffold.yaml)
   - [Chart.yaml](./helm/application/Chart.yaml)
   - [values.local.yaml](./helm/application/values.local.yaml)
   - [_helpers.tpl](./helm/application/templates/_helpers.tpl)
   - [deployment.yaml](./helm/application/templates/deployment.yaml)
   - [service.yaml](./helm/application/templates/service.yaml)
4. update the package information in [build.gradle](./build.gradle)
5. update default service name in [cloudbuild.yaml](./cloudbuild.yaml)
6. update access control policies in [cerbos-policies](./helm/application/cerbos-policies) directory

### Documentation
 - [tools and frameworks](./docs/tools.md)

## Run Locally
1. Setup and configure minikube (using [This setup](https://github.com/Nuvalence/dsgov-local-environment))
2. run this command: `skaffold run`
2. [view docs](http://api.dsgov.test/demo/swagger-ui/index.html)
