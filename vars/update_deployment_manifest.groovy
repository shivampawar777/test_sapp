def updateDeploymentManifest(){
    withCredentials([
        string(credentialsId: "gitHubEmail", variable: "GITHUB_EMAIL"),
        string(credentialsId: "gitHubToken", variable: "GITHUB_TOKEN"),
        usernamePassword(credentialsId: "dockerHub",
                        usernameVariable: "DOCKER_USER",
                        passwordVariable: "DOCKER_TOKEN")
    ]) {
            sh """
                git config user.email "${GITHUB_EMAIL}"
                git config user.name "${env.GITHUB_USER}"

                # update the deployment file: replace image tag
                sed -i "s|${DOCKER_USER}/streamlit-app:.*|${DOCKER_USER}/streamlit-app:${env.IMAGE_TAG}|g" \
                ${env.deployment_file_path}
                        
                git add Deployment-Manifest/deployment.yml
                git commit -m "Updated deployment image to version ${env.IMAGE_TAG}"
                git push https://${GITHUB_TOKEN}@github.com/${GITHUB_USER}/${env.GITHUB_REPO} HEAD:main
            """        
    }
}
