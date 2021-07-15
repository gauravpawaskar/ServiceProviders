# OAuth / SAML testing

## Docker build

If there is no code change then directly jump to minikube setup. Latest images are already built and uploaded to Docker hub.

Else follow following sections as per code changes.

```
./build.sh
```

## Minikube Setup

Tested in ubuntu VM on VMWare Player

```sh
cd k8s_infa
minikube start --vm-driver=none --addons ingress
kubectl get pods -n ingress-nginx // To verify ingress installation
minikube ip // record the ip
```

edit /etc/hosts file and add following line

```sh
{minikube ip} flagprotectors.com
```

After updating hosts file continue following commands

```sh
kubectl create secret generic linkedin --from-literal CLIENT_ID={client_id} --from-literal CLIENT_SECRET={client_secret}
kubectl create secret generic mongo --from-literal MONGO_USER={mongo_user} --from-literal MONGO_PASSWORD={mongo_secret}
kubectl apply -f .
```
