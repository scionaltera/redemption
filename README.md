# Redemption
![AWS CodeBuild](https://codebuild.us-west-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoibTY4T2JxQ2IvQ0YybURlS0VDNXZ5YXFyU2ozZVhhMldJZ0FEd1V5dzhXalUrdVhhK2h3eHh5UW1SeXo0MG9OVy9HQmpNYnlUY09CSStOSHQyYTlzc0lBPSIsIml2UGFyYW1ldGVyU3BlYyI6IlU3WG5BMTdEQi95cVlvcjciLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=scionaltera/redemption)](https://dependabot.com)
[![Website](https://img.shields.io/website/https/redemption.bonevm.com.svg?label=website "Website")](https://redemption.bonevm.com)  
## Introduction
Redemption is a platform for the folks at [1UpOnCancer](http://www.1uponcancer.org/) to use to manage the competitions and giveaways they do. They have to manage a lot of Steam keys and other prizes, participants and events and needed a database to help with that. I have decided to make it an open source project in case there are other organizations that need a similar way to organize their contest assets.

## Running
### Locally
The easiest way to get Redemption running is to use the [Docker container](https://hub.docker.com/r/scionaltera/redemption/). To run Redemption on your local machine, make sure you have Docker installed and simply type:
```
docker-compose up
```

### Deployed
To deploy a production version of Redemption you will need a database. Redemption is only tested using MySQL because that is what our production deployment uses, but other databases should work if they are compatible with Spring Data JPA. You may need to modify the SQL scripts under `src/main/resources/db/migration` because they may contain features specific to MySQL that don't work on your database.

You will need to change the values in `redemption.env` to point at the database you have set up, and you will either need to deploy the Redemption Docker container in a service like ECS or Kubernetes, or run the executable JAR on your server. It has Jetty embedded in it, so you shouldn't need to install anything else except Java 8.

### Configuration Properties
Redemption needs the properties from `redemption.env` in order to start up successfully, no matter how it's deployed. You can provide them in any of the various ways that Spring Boot supports. This is not a [comprehensive list](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html), but should cover most use cases.

1. Just edit `redemption.env` and supply it to `docker-compose`.
1. Modify `src/main/resources/application.yaml` and recompile with `./gradlew clean buildDocker`.
1. Supply the properties as environment variables.
1. JSON in the `SPRING_APPLICATION_JSON` environment variable: `{ "redemption.datasource.username": "admin" }`

### First Run
The first time Redemption starts up it will create a default user with the username and password `admin`. When you log in, there will be a banner across the top warning you that your copy of Redemption is not secure. **The very first thing you should do is to create a new user**, give that user all the permissions, and delete the `admin` user. Log out and log back in as your new user, and the banner will go away.

## Support
The best way to get support for Redemption is to file a GitHub issue. We do not have a large development team and this work is being done on a volunteer basis, so support is on a best-effort basis only.

There currently is no documentation, but we are going to start building out a wiki in the near future. Contributions are welcome.
