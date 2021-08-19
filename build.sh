echo "**** Building Database service ****"
cd database
docker build -t flagprotectors/database:latest .
docker push flagprotectors/database:latest
cd ..
echo "**** Building Frontend service ****"
cd frontend
docker build -t flagprotectors/frontend:latest .
docker push flagprotectors/frontend:latest
cd ..
echo "**** Building Other services from services dir ****"
cd services
for d in *; do
    cd $d
    if [ $d = "auth0saml" ]
    then
        ./gradlew clean build -x test
    fi
    docker build -t flagprotectors/$d:latest .
    docker push flagprotectors/$d:latest
    cd ..
done
