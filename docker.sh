## Build images from Dockerfile and push to Dockerhub
## Usage: ./docker.sh

# Build Eureka image

echo $'\n ============= Eureka Service ============= \n'

echo $'\n🛠️  Building eureka image'
docker build -t eureka:latest eureka

echo $'\n📄  Tagging eureka image'
docker tag eureka:latest romicolombo/eureka:latest

echo $'\n💌  Pushing eureka image to Dockerhub'
docker push romicolombo/eureka:latest

# Build gateway image

echo $'\n🏗️ ============= Gateway ============= 👷👷‍♀️\n'

echo $'\n🛠️  Building gateway image'
docker build -t ms-gateway:latest gateway

echo $'\n📄  Tagging gateway image'
docker tag ms-gateway:latest romicolombo/ms-gateway:latest

echo $'\n💌  Pushing gateway image to Dockerhub'
docker push romicolombo/ms-gateway:latest

# Build accounts image

echo $'\n🧦 ============= Accounts ============= 👕\n'

echo $'\n🛠️  Building accounts image'
docker build -t ms-accounts:latest accounts

echo $'\n📄  Tagging accounts image'
docker tag ms-accounts:latest romicolombo/ms-accounts:latest

echo $'\n💌  Pushing accounts image to Dockerhub'
docker push romicolombo/ms-accounts:latest

# Build users image

echo $'\n🏗️ ============= Users ============= 👷👷‍♀️\n'

echo $'\n🛠️  Building users image'
docker build -t ms-users:latest users

echo $'\n📄  Tagging users image'
docker tag ms-users:latest romicolombo/ms-users:latest

echo $'\n💌  Pushing users image to Dockerhub'
docker push romicolombo/ms-users:latest

# Build cards image

echo $'\n🏗️ ============= Cards ============= 👷👷‍♀️\n'

echo $'\n🛠️  Building cards image'
docker build -t ms-cards:latest cards

echo $'\n📄  Tagging cards image'
docker tag ms-cards:latest romicolombo/ms-cards:latest

echo $'\n💌  Pushing cards image to Dockerhub'
docker push romicolombo/ms-cards:latest