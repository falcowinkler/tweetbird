FROM java:8-alpine
RUN mkdir -p /app /app/resources
WORKDIR /app
COPY target/uberjar/*-standalone.jar .
COPY resources/public resources/public
CMD java -jar tweetbird-deploy-standalone.jar
EXPOSE 8080