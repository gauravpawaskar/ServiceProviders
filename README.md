# OAuth / SAML testing

## Frontend Docker build

```sh
cd frontend
docker build -t flagprotectors/frontend:latest .
docker push flagprotectors/frontend:latest
```

## Services Docker build

```sh
cd linkedin
docker build -t flagprotectors/linkedin:latest .
docker push flagprotectors/linkedin:latest
```

## Minikube Setup

Tested in ubuntu VM on VMWare Player

```sh
minikube start --vm-driver=none
minikube addons enable ingress
kubectl get pods -n ingress-nginx // To verify ingress installation
minikube ip // record the ip
```

edit /etc/hosts file and add following line

```sh
{minikube ip} flagprotectors.com
kubectl create secret generic linkedin --from-literal CLIENT_ID={client_id} --from-literal CLIENT_SECRET={client_secret}
kubectl apply -f frontend.yaml
kubectl apply -f linkedin.yaml
```
