FROM adorsys/java:8

LABEL maintainer "https://git.adorsys.de/psd2/sandbox"

EXPOSE 8080

ENV JAVA_OPTS -Xmx512m

COPY ./target/sandbox-*.jar /opt/app-root/src

CMD exec java -Dloader.path="lib" $JAVA_OPTS -jar sandbox-*.jar
