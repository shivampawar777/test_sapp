# Jenkins Pipeline for Python based application using Docker, SonarQube, Argo CD, Helm and Kubernetes

Prerequisites:

   -  Python application code hosted on a Git repository
   -  Jenkins server
   -  Kubernetes cluster
   -  Helm package manager
   -  Argo CD

Run this project on local environment then go ahed for CICD.

### Install AWS CLI v2
``` shell
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
sudo apt install unzip
unzip awscliv2.zip
sudo ./aws/install -i /usr/local/aws-cli -b /usr/local/bin --update
```

### Install kubectl
``` shell
curl -o kubectl https://amazon-eks.s3.us-west-2.amazonaws.com/1.19.6/2021-01-05/bin/linux/amd64/kubectl
chmod +x ./kubectl
sudo mv ./kubectl /usr/local/bin
kubectl version --short --client
```

### Install eksctl
``` shell
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
eksctl version
```

### Create cluster
``` shell
eksctl create cluster \
--name three-tier-cluster \
--region us-east-1 \
--node-type t2.medium \
--nodes-min 2 \
--nodes-max 2
```

### Update kubeconfig to point kubectl
``` shell
aws eks update-kubeconfig \
--region us-east-1 \
--name three-tier-cluster
kubectl get nodes
```

### Create IAM policy
``` shell
curl -O https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.5.4/docs/install/iam_policy.json
aws iam create-policy \
--policy-name AWSLoadBalancerControllerIAMPolicy \
--policy-document file://iam_policy.json
```

### Open-ID connect provider
``` shell
eksctl utils associate-iam-oidc-provider \
--region=us-east-1 \
--cluster=three-tier-cluster \
--approve
```

### Create service account
``` shell
eksctl create iamserviceaccount \
--cluster=three-tier-cluster \
--namespace=kube-system \
--name=aws-load-balancer-controller \
--role-name AmazonEKSLoadBalancerControllerRole \
--attach-policy-arn=arn:aws:iam::<aws_account_id>:policy/AWSLoadBalancerControllerIAMPolicy \
--approve \
--region=us-east-1
```

### Deploy AWS load balancer controller
``` shell
sudo snap install helm --classic

helm repo add eks https://aws.github.io/eks-charts
helm repo update eks

helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
-n kube-system \
--set clusterName=my-cluster \
--set serviceAccount.create=false \
--set serviceAccount.name=aws-load-balancer-controller
```