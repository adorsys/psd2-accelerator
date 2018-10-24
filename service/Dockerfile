FROM adorsys/java:8

LABEL maintainer "https://git.adorsys.de/psd2/sandbox"

EXPOSE 8080

ENV JAVA_OPTS -Xmx512m

COPY ./target/certificate-server-*.jar .

CMD exec java $JAVA_OPTS -jar certificate-server-*.jar
