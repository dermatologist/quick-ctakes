## Create db container

```
FROM mysql:5

COPY ./sno_rx_16ab_db/ /docker-entrypoint-initdb.d/
```