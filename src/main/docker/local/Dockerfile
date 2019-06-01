#
# This is a "development" Dockerfile that creates a Docker image with a full JDK as well as the Redemption
# JAR. It exposes the debug port (5005) so that you can attach a debugger. Docker images created using this
# file are not recommended for production use.
#

FROM openjdk:8-jre-alpine
MAINTAINER Peter Keeler <peter@bonevm.com>
EXPOSE 8080
COPY redemption-*.jar /opt/app/app.jar
CMD ["/usr/bin/java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005","-jar","/opt/app/app.jar"]