## mysql
```shell
docker run -p 3306:3306 --name my-mysql -e MYSQL_ROOT_PASSWORD=123456 -d mysql:latest
```


## postgreSQL
```shell
docker run -p 5432:5432 --name my-postgres -e POSTGRES_PASSWORD=123456 -d postgres:latest
```