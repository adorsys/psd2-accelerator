#!/bin/sh

envsubst '$PORTAL_INTERNAL_URL \
          $PORTAL_EXTERNAL_URL' \
 < /opt/app-root/etc/nginx.d/sandbox-portal-server.conf > /opt/app-root/etc/nginx.d/sandbox-portal-server.TMP

mv /opt/app-root/etc/nginx.d/sandbox-portal-server.TMP /opt/app-root/etc/nginx.d/sandbox-portal-server.conf

envsubst '$API_INTERNAL_URL \
          $API_EXTERNAL_URL' \
 < /opt/app-root/etc/nginx.d/sandbox-xs2a-server.conf > /opt/app-root/etc/nginx.d/sandbox-xs2a-server.TMP

mv /opt/app-root/etc/nginx.d/sandbox-xs2a-server.TMP /opt/app-root/etc/nginx.d/sandbox-xs2a-server.conf

# envsubst
exec /docker-entrypoint.sh "$@"
