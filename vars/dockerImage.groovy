def dockerImage() {
    withCredentials([
        usernamePassword(credentialsId: "dockerHubCred",
                        usernameVariable: "DOCKER_USER",
                        passwordVariable: "DOCKER_TOKEN")
    ]) {
            sh """
                cd ${env.DockerFile_path}
                docker build -t ${DOCKER_USER}/streamlit_application:${env.IMAGE_TAG} .
                docker login -u ${DOCKER_USER} -p ${DOCKER_TOKEN}
                docker push ${DOCKER_USER}/streamlit_application:${env.IMAGE_TAG}
            """
    }
}