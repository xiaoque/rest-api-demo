pipeline {
    agent any
    tools {
        // Use the Maven installation configured in Jenkins
        maven 'Maven'
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn clean package -DskipTests' 
            }
        }
    }
}