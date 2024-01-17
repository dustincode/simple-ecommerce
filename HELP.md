### Docker

> Simple start dev environment

```
docker-compose up -d
```
| Service | Description                    | Access                                        |
|---------|--------------------------------|-----------------------------------------------|
| backend | Backend server running on java | localhost:8080                                |
| db | Mysql server                   | jdbc:mysql://admin:admin123@localhost:6606    |
| swagger_ui | Swagger api documentation      | http://localhost:8080/swagger-ui/index.html#/ |

## Admin account

```sybase
"email": "sparkminds@mailinator.com",
"password": "Admin@123456",
```

> MFA Secret:
```sybase
OHVE6XYJUMKRLJQ6HV3TXENDJZAWKAW3
```
> MFA QR:
```sybase
https://api.qrserver.com/v1/create-qr-code/?data=otpauth%3A%2F%2Ftotp%2Flocalhost%3Asparkminds%40mailinator.com%3Fsecret%3DOHVE6XYJUMKRLJQ6HV3TXENDJZAWKAW3%26issuer%3Dlocalhost%26algorithm%3DSHA1%26digits%3D6%26period%3D30&size=200x200&ecc=M&margin=0
```
