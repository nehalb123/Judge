FROM openjdk:8-alpine

MAINTAINER Nehal Borole <nehal.borole@gmail.com>

WORKDIR /app

COPY build/libs/algocup-judge-*.jar /app/
COPY build/classes/kotlin/main/com/algocup/type/ /app/build/classes/kotlin/main/com/algocup/type/

EXPOSE 5008

CMD java $JAVA_OPTS -jar /app/algocup-judge-*.jar