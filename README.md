# Books API
**API for books services.**

The application provides opportunities to create 
and save data about books and their categories.

## Running locally
### Cloning the repository and moving to the working directory
```shell
git clone https://github.com/allitov/books-api.git
cd books-api
```

### Run the application in default mode
```shell
docker-compose --file ./docker/docker-compose-default.yaml up -d
```

#### Stop the application
```shell
docker-compose --project-name="books-api" down
```

### Run the application in demonstration mode (Contains entries in the database)
```shell
docker-compose --file ./docker/docker-compose-demo.yaml up -d
```

#### Stop the application
```shell
docker-compose --project-name="books-api-demo" down
```

### Run application environment only
```shell
docker-compose --file ./docker/docker-compose-env.yaml up -d
```

#### Stop the environment
```shell
docker-compose --project-name="books-api-env" down
```

## Documentation
To familiarize yourself with the application's API and see example queries, 
you can refer to the [interactive Swagger documentation](http://localhost:8080/swagger-ui/index.html) 
(available only after launching the application).