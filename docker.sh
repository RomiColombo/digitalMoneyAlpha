## Build images from Dockerfile and push to Dockerhub
## Usage: ./docker.sh

# Build Eureka image

echo $'\n ============= Eureka Service ============= \n'

echo $'\nš ļø  Building eureka image'
docker build -t eureka:latest eureka

echo $'\nš  Tagging eureka image'
docker tag eureka:latest romicolombo/eureka:latest

echo $'\nš  Pushing eureka image to Dockerhub'
docker push romicolombo/eureka:latest

# Build gateway image

echo $'\nšļø ============= Gateway ============= š·š·āāļø\n'

echo $'\nš ļø  Building gateway image'
docker build -t ms-gateway:latest gateway

echo $'\nš  Tagging gateway image'
docker tag ms-gateway:latest romicolombo/ms-gateway:latest

echo $'\nš  Pushing gateway image to Dockerhub'
docker push romicolombo/ms-gateway:latest

# Build accounts image

echo $'\nš§¦ ============= Accounts ============= š\n'

echo $'\nš ļø  Building accounts image'
docker build -t ms-accounts:latest accounts

echo $'\nš  Tagging accounts image'
docker tag ms-accounts:latest romicolombo/ms-accounts:latest

echo $'\nš  Pushing accounts image to Dockerhub'
docker push romicolombo/ms-accounts:latest

# Build users image

echo $'\nšļø ============= Users ============= š·š·āāļø\n'

echo $'\nš ļø  Building users image'
docker build -t ms-users:latest users

echo $'\nš  Tagging users image'
docker tag ms-users:latest romicolombo/ms-users:latest

echo $'\nš  Pushing users image to Dockerhub'
docker push romicolombo/ms-users:latest

# Build cards image

echo $'\nšļø ============= Cards ============= š·š·āāļø\n'

echo $'\nš ļø  Building cards image'
docker build -t ms-cards:latest cards

echo $'\nš  Tagging cards image'
docker tag ms-cards:latest romicolombo/ms-cards:latest

echo $'\nš  Pushing cards image to Dockerhub'
docker push romicolombo/ms-cards:latest