# Developer Portal

---

## Introduction

The [Payment Service Directive 2 (PSD2)](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN)
instructs banks to provide a fully productive Access-to-Account (XS2A)
interface to Third Party Providers (TPPs) until September 2019. XS2A
itself consists of banking services to initiate payments (PIS), request
account data (AIS) and get the confirmation of the availability of funds
(PIIS). In order to guarantee the compliance of this deadline due to
adaptions and bugs, PSD2 claims the banks to provide a functional
sandbox offering the XS2A services in a non-productive environment until
March 2019.

Central component of the PSD2 Accelerator is the XS2A interface which
meets the requirements of the [Berlin Group](https://www.berlin-group.org/)
(Version 1.3) and is based on test data. Besides the actual interface,
PSD2 instructs banks to offer a technical documentation free of charge containing
amongst others, information about supported payment products and payment
services.

Usually, before accessing the XS2A services a TPP would need to register
at its National Competent Authority (NCA) and request an
[eIDAS](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN)
certificate at an appropriate Trust Service Provider (TSP). Issuing a
real certificate just for testing purposes would be too much effort,
which is why the PSD2 Accelerator is additionally simulating a fictional
TSP issuing Qualified Website Authentication Certificates (QWAC). A QWAC
is part of eIDAS and might be better known as [X.509](https://www.ietf.org/rfc/rfc3739.txt)
certificate. For PSD2-purposes the certificate gets extended by the QcStatement
containing appropriate values such as the role(s) of the PSP (see
[ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf)).

After embedding the QWAC in the actual XS2A request, the role and the
signature get validated at a central reverse proxy before it gets
finally passed to the interface where the banking logic happens. You can
visit our documentation here [XS2A Swagger UI](/swagger-ui.html).

The described components with their connection to each other are
displayed in Figure 1.1:

![PSD2 Accelerator](assets/accelerator.svg 'Figure 1.1: Components of the PSD2 Accelerator')
_Figure 1.1: Components of the PSD2 Accelerator_

### Active XS2A Configuration (Bank Profile)

- SCA-Approach: Redirect
- Payment-Types
  - Single (sepa-credit-transfers)
  - Future-Dated (sepa-credit-transfers)
  - Periodic (sepa-credit-transfers)
- Confirmation of Funds: Yes
- Redirect-URLs
  - PIS Redirect-URL: _https://sandbox-api.dev.adorsys.de/v1/online-banking/init/pis/:redirect-id_
  - PIS Cancellation Redirect-URL: _https://sandbox-api.dev.adorsys.de/v1/online-banking/cancel/pis/:redirect-id_
  - AIS Redirect-URL: _https://sandbox-api.dev.adorsys.de/v1/online-banking/init/ais/:redirect-id_
- Supported Consents
  - Dedicated: Yes
  - Bank-Offered: Yes
  - Global: No
  - Available Accounts: No

Disabled features:

- Signing Basket
- Bulk Payments
- Delta-Reports
- Multi-Level SCA

Technical configuration of the XS2A API:

```yaml
---
setting:
  frequencyPerDay: 5
  combinedServiceIndicator: false
  scaApproaches:
    - REDIRECT
  tppSignatureRequired: false
  bankOfferedConsentSupport: true
  pisRedirectUrlToAspsp: http://localhost:8080/v1/online-banking/init/pis/{redirect-id}
  pisPaymentCancellationRedirectUrlToAspsp: http://localhost:8080/v1/online-banking/cancel/pis/{redirect-id}
  aisRedirectUrlToAspsp: http://localhost:8080/v1/online-banking/init/ais/{redirect-id}
  multicurrencyAccountLevel: SUBACCOUNT
  availableBookingStatuses:
    - BOOKED
    - PENDING
  supportedAccountReferenceFields:
    - IBAN
  consentLifetime: 0
  transactionLifetime: 0
  allPsd2Support: false
  transactionsWithoutBalancesSupported: false
  signingBasketSupported: false
  paymentCancellationAuthorizationMandated: true
  piisConsentSupported: false
  deltaReportSupported: false
  redirectUrlExpirationTimeMs: 600000
  notConfirmedConsentExpirationPeriodMs: 86400000
  notConfirmedPaymentExpirationPeriodMs: 86400000
  supportedPaymentTypeAndProductMatrix:
    SINGLE:
      - sepa-credit-transfers
    PERIODIC:
      - sepa-credit-transfers
  paymentCancellationRedirectUrlExpirationTimeMs: 600000
  availableAccountsConsentSupported: false
  scaByOneTimeAvailableAccountsConsentRequired: true
  psuInInitialRequestMandated: false
```

## Getting started

In order to test the services of XS2A you need to execute the following
steps:

- To generate a QWAC Certificate follow this link [Certificate Service UI](./certificate-service) and
  embed the .pem and .key files in your requests.
- The most popular use cases are described in the following section.

---

## Use Cases with PSU Data

To execute services of XS2A using the EMBEDDED SCA approach, you have to
use these global PSU credentials:

- Password: 12345
- TAN: 54321
  The REST API is available here: [https://sandbox-api.dev.adorsys.de](https://sandbox-api.dev.adorsys.de).

Each use case contains a table with the fitting PSU-IDs, the Iban and
the expected status. Therefore, you can check if your outcome is
correct. Furthermore, some use cases contain an example request and its
response.

### Simulation of SCA

SCA is mandated for the initiation and cancellation of payments as well
as for the creation of consents. To simulate the different SCA
responses, PSU-IDs and corresponding Ibans are defined. A successful SCA
can therefore only be achieved when performing the requests with the
Iban "DE11760365688833114935" and the corresponding PSU-ID
"PSU-Successful". The same goes for all other combinations of PSUs.
After sending your payment or consent request, you receive a scaRedirect
url. Insert this url in your browser and attach your PSU-ID as
Query-Parameter to the url. And example would be:`?psu-id=PSU-Successful`.
"PSU-Successful" can be replaced with other PSUs. When the Iban within
the request does not match the PSU-ID, the SCA does not have an effect on
the transaction or consent status. Not passing any PSU-ID will lead to a
Format Error.

### Payment Initiation

In order to execute a payment, replace the Debtor-Iban in your request
with the one of your favored PSU. To legitimate the payment, use the SCA
Redirect described in the previous section.

`POST https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers`

The following code snippet is an example cURL command which initiates a
single payment for PSU "PSU-Successful":

```sh
curl -v "https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers" \
  -H "accept: application/json" \
  -H "X-Request-ID: 99391c7e-ad88-49ec-a2ad-99ddcb1f7721" \
  -H "PSU-IP-Address: 192.168.8.78" \
  -H "Content-Type: application/json" \
  -H "tpp-redirect-uri: https://adorsys.de/" \
  --cert certificate.pem \
  --key private.key \
  -d '{
    "endToEndIdentification": "WBG-123456789",
    "debtorAccount": {
      "currency": "EUR",
      "iban": "DE11760365688833114935"
    },
    "instructedAmount": {
      "currency": "EUR",
      "amount": "520.00"
    },
    "creditorAccount":{
      "currency": "EUR",
      "iban": "DE15500105172295759744"
    },
    "creditorName": "WBG",
    "creditorAddress": {
      "buildingNumber": "56",
      "city": "Nürnberg",
      "country": "DE",
      "postalCode": "90543",
      "street": "WBG Straße"
    },
    "remittanceInformationUnstructured": "Ref. Number WBG-1222"
  }'
```

The following code snippet is an example response for a successful
payment:

```json
{
    "transactionStatus": "RCVD",
    "paymentId": "FHQ0W-JVRLEuMwDXAYnRaRiEY5gFzU333uIo9CrgAxU6bEHR4m6hs_rkUaqcWwJqfPlpOwr468RhuFoTl0Y5Kg==_=_bS6p6XvTWI",
    "transactionFees": null,
    "transactionFeeIndicator": false,
    "scaMethods": null,
    "chosenScaMethod": null,
    "challengeData": null,
    "_links": {
        "scaRedirect": "https://sandbox-api.dev.adorsys.de/v1/online-banking/init/pis/65458d7e-2181-4e28-83cc-2a1900d1f727",
        "self": "https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers/FHQ0W-JVRLEuMwDXAYnRaRiEY5gFzU333uIo9CrgAxU6bEHR4m6hs_rkUaqcWwJqfPlpOwr468RhuFoTl0Y5Kg==_=_bS6p6XvTWI",
        "status": "https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers/FHQ0W-JVRLEuMwDXAYnRaRiEY5gFzU333uIo9CrgAxU6bEHR4m6hs_rkUaqcWwJqfPlpOwr468RhuFoTl0Y5Kg==_=_bS6p6XvTWI/status",
        "scaStatus": "https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers/FHQ0W-JVRLEuMwDXAYnRaRiEY5gFzU333uIo9CrgAxU6bEHR4m6hs_rkUaqcWwJqfPlpOwr468RhuFoTl0Y5Kg==_=_bS6p6XvTWI/authorisations/65458d7e-2181-4e28-83cc-2a1900d1f727"
    },
    "psuMessage": null,
    "tppMessages": null
}
```

| PSU-ID            | Iban                   | SCA Status                  | Transaction Status                  |
| :---------------- | :--------------------- | :-------------------------- | :---------------------------------- |
| PSU-Successful    | DE11760365688833114935 | finalised                   | ACTC/ACSC\*                         |
| PSU-Rejected\*\*  | DE06760365689827461249 | failed                      | RJCT                                |
| PSU-Blocked       | DE13760365681209386222 | _(no SCA Status available)_ | _(no Transaction Status available)_ |
| PSU-InternalLimit | DE91760365683491763002 | finalised                   | RJCT                                |

(\*) The Status is depending on the payment-type. A single payment will
get "executed" by our mocked backend. A future-dated payment will get
executed when the "requestedExecutionDate" is reached. A similar
behaviour is implemented for periodic-payments which is depending on
the "endDate".

(\*\*) Same behavior is implemented, when PSU does not match iban or the PSU-ID is not known.

### Payment Cancellation

In order to cancel a payment, insert your Payment-Id in the Delete Payment
Endpoint. Payment Cancellation requires an _explicit start of the
authorisation process_, which means you need to follow the `startAuthorisation`
link in the response (see the [XS2A Swagger UI](/swagger-ui.html)). From then on
you can legitimate the payment cancellation by using the [SCA Redirect described
in the previous section](developer-portal#simulation-of-sca).

`DELETE https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers/paymentId`

| PSU-ID                    | Iban                   | SCA Status | Transaction Status |
| :------------------------ | :--------------------- | :--------- | :----------------- |
| PSU-Successful            | DE11760365688833114935 | finalised  | CANC\*             |
| PSU-Cancellation-Rejected | DE68760365687914626923 | failed     | RJCT               |

(\*) It is only possible to cancel payments which are not yet executed.
Since our mocked backend "executes" single payments directly, only
future-dated- and periodic-payments can be deleted.

### Get Payment

In order to get the Payment Data, insert your Payment-Id in the GET
Payment Data Endpoint.

`GET https://sandbox-api.dev.adorsys.de/v1/payments/paymentId`

In order to get the Payment Status, insert your paymentId in the GET
Payment Status Endpoint.

`GET https://sandbox-api.dev.adorsys.de/v1/payments/paymentId/status`

| PSU-ID         | Iban                   | SCA Status | Transaction Status |
| :------------- | :--------------------- | :--------- | :----------------- |
| PSU-Successful | DE11760365688833114935 | finalised  | ACTC/ACSC          |

### Consent Creation

In order to create a consent, replace the Iban in your request with the
one of your favored PSU. To legitimate the consent creation, use the SCA
Redirect described in the previous section.

`POST https://sandbox-api.dev.adorsys.de/v1/consents`

The following code snippet is an example cURL command which creates a
consent for PSU "PSU-Successful":

```sh
curl -v "https://sandbox-api.dev.adorsys.de/v1/consents" \
  -H "accept: application/json" \
  -H "X-Request-ID: 99391c7e-ad88-49ec-a2ad-99ddcb1f7721" \
  -H "Content-Type: application/json" \
  -H "tpp-redirect-uri: https://adorsys.de/" \
  --cert certificate.pem \
  --key private.key \
  -d '{
  "access": {
    "accounts": [
      {
        "iban": "DE11760365688833114935",
        "currency": "EUR"
     }
    ],
    "balances": [
     {
        "iban": "DE11760365688833114935",
        "currency": "EUR"
     }
    ],
    "transactions": [
      {
        "iban": "DE11760365688833114935",
        "currency": "EUR"
    }
    ]
},
  "recurringIndicator": true,
  "validUntil": "2020-12-31",
  "frequencyPerDay": 4,
  "combinedServiceIndicator": true
}'
```

| PSU-ID                  | Iban                   | SCA Status                  | Consent Status                  |
| :---------------------- | :--------------------- | :-------------------------- | :------------------------------ |
| PSU-Successful          | DE11760365688833114935 | finalised                   | valid                           |
| PSU-Rejected            | DE06760365689827461249 | failed                      | rejected                        |
| PSU-Blocked             | DE13760365681209386222 | _(no SCA Status available)_ | _(no Consent Status available)_ |
| PSU-ConsentExpired      | DE12760365687895439876 | finalised                   | expired                         |
| PSU-ConsentRevokedByPsu | DE89760365681729983660 | finalised                   | revokedByPsu                    |

### Consent Deletion

In order to delete a consent, insert your consentId in the Delete
Consent Endpoint.

`DELETE https://sandbox-api.dev.adorsys.de/v1/consents/consentId`

| PSU-ID            | Iban                   | Consent Status                  |
| :---------------- | :--------------------- | :------------------------------ |
| PSU-Successful    | DE11760365688833114935 | terminatedByTpp                 |
| PSU-Rejected      | DE06760365689827461249 | rejected                        |
| PSU-Blocked       | DE13760365681209386222 | _(no Consent Status available)_ |
| PSU-InternalLimit | DE91760365683491763002 | terminatedByTpp                 |

### Get Account Data

In order to get the Account Data, replace the Iban in your request with
the one of your favored PSU.

`GET https://sandbox-api.dev.adorsys.de/v1/accounts`

The following code snippet is an example cURL command which gets all the
account data from PSU "PSU-Successful":

```sh
curl -v "https://sandbox-api.dev.adorsys.de/v1/accounts" \
  -H "accept: application/json" \
  -H "X-Request-ID: 99391c7e-ad88-49ec-a2ad-99ddcb1f7721" \
  -H "Content-Type: application/json" \
  -H "consent-id: I58hV2nWPJVJEvuw0dzl8qBkGcz40Qo_BCd_CjnTf_vsx7DeU-pL5sFaqwNUzbAThuXzrcFlVLs6eEVHdoFgKQ==_=_bS6p6XvTWI" \
  -H "tpp-redirect-uri: https://adorsys.de/" \
  --cert certificate.pem \
  --key private.key \
```

The following code snippet is an example response for successful GET
Account Data:

```json
{
  "accounts": [
    {
      "resourceId": "8660d175-2c79-4b68-a175-93b1866dc7e3",
      "iban": "DE11760365688833114935",
      "bban": "",
      "msisdn": "",
      "currency": "EUR",
      "name": "",
      "product": "Current Account",
      "cashAccountType": "CACC",
      "status": null,
      "bic": "",
      "linkedAccounts": "",
      "usage": null,
      "details": "",
      "balances": [
        {
          "balanceAmount": {
            "currency": "EUR",
            "amount": "1500"
          },
          "balanceType": null,
          "lastChangeDateTime": null,
          "referenceDate": null,
          "lastCommittedTransaction": null
        }
      ],
      "_links": {
        "viewTransactions": "https://sandbox-api.dev.adorsys.de/v1/accounts/8660d175-2c79-4b68-a175-93b1866dc7e3/transactions"
      }
    }
  ]
}
```

| PSU-ID                  | Iban                     | Consent Status |
| :---------------------- | :----------------------- | :------------- |
| PSU-Successful          | DE11760365688833114935\* | valid          |
| PSU-ConsentExpired      | DE12760365687895439876   | expired        |
| PSU-ConsentRevokedByPsu | DE89760365681729983660   | revokedByPsu   |

(\*) PSU-Successful has further accounts to test the behaviour where

- an account has a negative accounting and available balance
  (DE89760365681134661389)
- an account has no transactions and the available balance is 0
  (DE07760365680034562391)
- an account has a lower available balance than accounting balance
  (DE71760365681257681381)
- the currency of an account is USD (DE56760365681650680255)
