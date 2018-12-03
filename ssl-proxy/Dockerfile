FROM adorsys/nginx
LABEL maintainer "https://git.adorsys.de/psd2/sandbox"

ADD root /

USER 0
# Allow entrypoint to create a temp file
RUN chmod -R g+w /opt/app-root/etc/nginx.default.d/ && \
    chown -R default /opt/app-root

USER default

ENTRYPOINT ["/sandbox-entrypoint.sh"]

EXPOSE 8443

CMD ["/usr/libexec/s2i/run"]
