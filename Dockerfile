FROM eclipse-temurin:21-alpine as jdk

COPY ./build/libs/work-adapter.jar /work-adapter.jar

RUN jar -xvf work-adapter.jar && jlink --add-modules $(jdeps --recursive --multi-release 21 --ignore-missing-deps --print-module-deps -cp 'BOOT-INF/lib/*' work-adapter.jar) --output /java

FROM alpine

LABEL org.opencontainers.image.source https://github.com/attocash/work-adapter

ENV JAVA_HOME=/java
ENV PATH "${JAVA_HOME}/bin:${PATH}"

RUN adduser -D atto
USER atto

COPY ./build/libs/work-adapter.jar /home/atto/work-adapter.jar

COPY --from=jdk /java /java

ENTRYPOINT ["java","-XX:+UseZGC","-jar","/home/atto/work-adapter.jar"]