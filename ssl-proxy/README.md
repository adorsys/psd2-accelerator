# Component _ssl-proxy_

## Create CA Certificate

Create the key for the CA

```sh
$ openssl genrsa -des3 -out ca.key 4096
```

With this key you can create the certificate for the CA. Leave **Common Name (CN)** empty, because it can cause errors. **Email** can be omitted.

```sh
$ openssl req -new -x509 -days 365 -key ca.key -out ca.crt
```

## Create the Client Certificate Signing Request (CSR)

Create the key for the client.
A number of questions will be asked; answer each one, including the Common Name (CN) and email address.
The CSR that’s created would be sent to the CA (an administrator, but in this case probably also yourself) to be signed.

```sh
$ openssl genrsa -des3 -out user.key 4096
```

With the key you can create the certificate signing request (CSR) file:

```sh
$ openssl req -new -key user.key -out user.csr
```

## Sign the Client Certificate Signing Request with the CA Certificate

```sh
openssl x509 -req -days 365 -in user.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out user.crt
```

## Export the PKCS File from the Client Cert

PKCS (PFX) is used to import the certificate in web browsers. So we have to export our **.crt** file to a **.pfx** file.

```sh
$ openssl pkcs12 -export -out user.pfx -inkey user.key -in user.crt -certfile ca.crt
```

You will be asked to supply an export password.

## Create a Self Signed SSL Certificate for nginx

NOTE: this is for testing - you can skip this step if you already have a proper SSL certificate

```sh
$ openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout nginx.key -out nginx.pem
```

## Create a Password File for Your SSL Certificate Key

The directive [ssl_password_file](http://nginx.org/en/docs/http/ngx_http_ssl_module.html#ssl_password_file) is used to
set the required SSL certificate key password. Create a file containing your passphrase and copy it to
`/opt/app-root/etc/global.pass`.

```sh
$ grep -i ssl_password_file /opt/app-root/etc/nginx.default.d/sandbox-server.conf
ssl_certificate /opt/app-root/etc/nginx.pem;
ssl_certificate_key /opt/app-root/etc/nginx.key;
ssl_password_file /opt/app-root/etc/global.pass;
[...]

$ cat /opt/app-root/etc/global.pass
supersecurepassword
```
