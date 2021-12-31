# lisa-lambda

## API

### POST /login
### GET /classes

### Deploy
```shell
$ aws s3 cp target/lisa-api-lambda-1.0-SNAPSHOT.jar s3://lisa-api-lambda-deployment/
$ aws lambda update-function-code --function-name lisa-login --s3-bucket lisa-api-lambda-deployment --s3-key lisa-api-lambda-1.0-SNAPSHOT.jar
$ aws lambda update-function-code --function-name lisa-get-classes --s3-bucket lisa-api-lambda-deployment --s3-key lisa-api-lambda-1.0-SNAPSHOT.jar
```
