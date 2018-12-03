#!/bin/sh

envsubst '$XS2A_INTERNAL_URL
          $XS2A_EXTERNAL_URL' \
 < /opt/app-root/etc/nginx.default.d/sandbox-server.conf > /opt/app-root/etc/nginx.default.d/sandbox-server.TMP

mv /opt/app-root/etc/nginx.default.d/sandbox-server.TMP /opt/app-root/etc/nginx.default.d/sandbox-server.conf

# envsubst
exec /docker-entrypoint.sh "$@"
