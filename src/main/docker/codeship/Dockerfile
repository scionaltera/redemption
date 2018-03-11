#
# This is the "production" Dockerfile. It creates a very lean image with a JRE and the Redemption JAR, and
# not a whole lot else.
#
FROM frolvlad/alpine-oraclejdk8:slim as build
MAINTAINER Peter Keeler <peter@bonevm.com>
WORKDIR /opt/build
COPY . /opt/build/
RUN cd /opt/build \
&& apk update \
&& apk upgrade \
&& apk add --no-cache bash \
&& apk add libstdc++ \
&& ./gradlew clean build

FROM frolvlad/alpine-oraclejre8:slim as run
MAINTAINER Peter Keeler <peter@bonevm.com>
EXPOSE 8080
COPY --from=build /opt/build/build/libs/redemption-*.jar /opt/app/app.jar
CMD ["/usr/bin/java", "-jar","/opt/app/app.jar"]
