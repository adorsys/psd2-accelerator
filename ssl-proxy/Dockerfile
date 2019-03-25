FROM adorsys/nginx
LABEL maintainer "https://github.com/adorsys/psd2-accelerator"

ADD root /

USER 0
# Allow entrypoint to create a temp file
RUN chmod -R g=u /opt/app-root && \
    # move default server with healthchecks to 8090 and remove "default_server;" option
    sed -i -e '/listen/!b' -e '/8080 default_server;/!b' -e 's/8080 default_server;/8090;/' /etc/opt/rh/rh-nginx${NGINX_SHORT_VER}/nginx/nginx.conf


USER default

ENTRYPOINT ["/sandbox-entrypoint.sh"]

EXPOSE 8443

CMD ["/usr/libexec/s2i/run"]
