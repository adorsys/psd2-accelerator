# Nginx SSL Proxy

## Getting started

### Create CA certificate

Create the key for the CA

`openssl genrsa -des3 -out ca.key 4096 ` 

With this key you can create the certificate for the CA. Leave **Common Name (CN)** empty, because it can cause errors. **Email** can be omitted.

`openssl req -new -x509 -days 365 -key ca.key -out ca.crt`


### Create the client certificate signing request
Create the key for the client.
A number of questions will be asked; answer each one, including the Common Name (CN) and email address.
The CSR that’s created would be sent to the CA (an administrator, but in this case probably also yourself) to be signed.

`openssl genrsa -des3 -out user.key 4096`

With the key you can create the certificate signing request (CSR) file:

`openssl req -new -key user.key -out user.csr`

### Sign the client certificate signing request with the CA cert

`openssl x509 -req -days 365 -in user.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out user.crt`

### Export the PKCS file from the client cert
PKCS (PFX) is used to import the certificate in web browsers. So we have to export our **.crt** file to a **.pfx** file.

`openssl pkcs12 -export -out user.pfx -inkey user.key -in user.crt -certfile ca.crt`

You will be asked to supply an export password.

### Create the nginx certificate

`openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout nginx.key -out nginx.pem`

### Create a password file for your nginx cert




