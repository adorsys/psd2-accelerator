FROM adorsys/java:8

LABEL maintainer "https://github.com/adorsys/psd2-accelerator"

EXPOSE 8080

ENV JAVA_OPTS -Xmx512m

# this path is documented in the arc42 documentation and part of the public API
# don't change this without proper communication to our users
COPY ./target/sandbox-*.jar ./target/sandbox-*.jar.sha1 /opt/app-root/src/

CMD exec java -Dloader.path="lib" $JAVA_OPTS -jar sandbox-*.jar
