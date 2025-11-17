def sonarTest() {
    withCredentials([string(credentialsId: 'sonarqube', variable: 'SONAR_AUTH_TOKEN')]) {
        sh """
            cd ${src_code_path} && \
            mvn sonar:sonar \
            -Dsonar.host.url=${SONAR_URL} \
            -Dsonar.login=${SONAR_AUTH_TOKEN}
        """
    }
}