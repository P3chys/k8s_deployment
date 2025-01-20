#!/bin/bash

# Helper function to print status messages
print_status() {
    echo "----------------------------------------"
    echo "🚀 $1"
    echo "----------------------------------------"
}

# Helper function to check if a command succeeded
check_status() {
    if [ $? -eq 0 ]; then
        echo "✅ $1 succeeded"
    else
        echo "❌ $1 failed"
        exit 1
    fi
}

# Start the redeployment process
print_status "Starting redeployment process"

# Build the backend Docker image
print_status "Building backend Docker image"
cd backend/java_backend
docker build -t k8s-backend:latest .
check_status "Backend Docker build"

# Build the frontend Docker image
print_status "Building frontend Docker image"
cd ../../frontend
docker build -t k8s-frontend:latest .
check_status "Frontend Docker build"

# Return to root directory
cd ..

# Delete existing ConfigMap
print_status "Removing existing router ConfigMap"
kubectl delete configmap router-config
check_status "ConfigMap deletion"

# Apply new configurations
print_status "Applying new Kubernetes configurations"
cd k8s
kubectl apply -f proxy-config.yaml
check_status "Router config application"
cd backend
kubectl apply -f backend-config.yaml
check_status "Backend config application"
cd ../frontend
kubectl apply -f service.yaml
check_status "Service frotend config application"
kubectl apply -f deployment.yaml
check_status "Service frotend deployment"

# Restart deployments
print_status "Restarting deployments"

# Restart router
kubectl rollout restart deployment router
check_status "Router deployment restart"

# Restart backends
kubectl rollout restart deployment backend-physical
check_status "Physical backend deployment restart"
kubectl rollout restart deployment backend-machine
check_status "Machine backend deployment restart"

# Restart frontend
kubectl rollout restart deployment frontend
check_status "Frontend deployment restart"

# Wait for rollouts to complete
print_status "Waiting for rollouts to complete"

kubectl rollout status deployment router
kubectl rollout status deployment backend-physical
kubectl rollout status deployment backend-machine
kubectl rollout status deployment frontend

# Verify the state of the system
print_status "Verifying system state"

echo "Current pods:"
kubectl get pods
echo ""

echo "Current services:"
kubectl get services
echo ""

#kubectl port-forward service/backend-machine 3001:80
#kubectl port-forward service/backend-physical 3000:80
#kubectl port-forward service/frontend 8082:80
#kubectl port-forward service/router-service 3000:80

print_status "Redeployment complete! You can now test your endpoints."

read -p "Press Enter to continue..."