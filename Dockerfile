FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER Peter Keeler <peter@bonevm.com>
EXPOSE 8080
COPY . /opt/build/
RUN mkdir -p /opt/app \
&& cd /opt/build \
&& apk update \
&& apk upgrade \
&& apk add --no-cache bash \
&& ./gradlew clean build -x check \
&& cp -v build/libs/redemption*.jar /opt/app/app.jar \
&& cd /opt/app \
&& rm -rf /tmp/* /var/cache/apk/* /opt/build ~/.m2 ~/.gradle
CMD ["/usr/bin/java", "-jar", "/opt/app/app.jar"]