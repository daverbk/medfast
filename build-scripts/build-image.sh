# Building the app
cd ..

echo "Building JAR files"
./gradlew clean build -x test

echo "Building Docker image"
docker build -f ./build-scripts/Dockerfile -t medfast:latest .
