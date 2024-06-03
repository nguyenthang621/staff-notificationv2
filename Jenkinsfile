pipeline {
    agent any
    stages {
    // --------------------pipeline inbound-----------------------
    stage('Check docker mysql') {
    steps {
        script {
            echo 'Checking docker ...'
            def serviceStatus = sh(script: 'service docker status', returnStatus: true)
            if (serviceStatus == 0) {
                echo 'Service docker start success.'
            } else {
                error 'Service docker start fail.'
            }
            echo 'Checking mysql in docker ...'
            
 
            def redisStatus = sh(script: 'docker ps --filter "name=mysql" --format "{{.Status}}"', returnStdout: true).trim()
            if (redisStatus.contains("Up")) {
                echo 'Mysql container is running.'
            } else {
                echo 'Mysql container is not running.'
                    }
                }
            }
        }
        stage('Check service staff-notification-v2 is running') {
            steps {
                echo 'Checking service staff-notification-v2...'
                script {
                    sh 'systemctl stop staff-notification-v2'
                    def serviceStatus = sh(script: 'service staff-notification-v2 status', returnStatus: true)
                    if (serviceStatus == 0) {
                        error 'Service staff-notification-v2 is running.'
                    } else {
                        echo 'Service staff-notification-v2 is stopped'
                    }
                }
            }
        }
        stage('Test staff-notification-v2') {
            steps {
                echo 'Running tests staff-notification-v2...'
                sh 'cd /var/lib/jenkins/workspace/staff-notification-v2_main/ && /opt/apache-maven-3.9.6/bin/mvn test'
            }
        }
        stage('Build staff-notification-v2') {
            steps {
                script {
                    echo 'Starting the build process...'
                    sh 'cd /var/lib/jenkins/workspace/staff-notification-v2_main/ && /opt/apache-maven-3.9.6/bin/mvn clean install'
                }
            }
        }
        stage('Deploy staff-notification-v2') {
            steps {
                echo 'Deploying the application...'
                script {
                    sh 'whoami'
                    sh 'cp /var/lib/jenkins/workspace/staff-notification-v2_main/target/staff-notification-v2-0.0.1-SNAPSHOT.jar /home/rnd/'
                    sh 'sudo systemctl restart staff-notification-v2'
                    // check status service
                    def serviceStatus = sh(script: 'service staff-notification-v2 status', returnStatus: true)
                    if (serviceStatus == 0) {
                        echo 'Service inbound start success.'
                    } else {
                        error 'Service inbound start fail.'
                    }
                }
            }
        }

    }
    // post {
    //     always {
    //         echo 'Checking service staff-notification-v2...'
    //         script {
    //             def serviceStatus = sh(script: 'service staff-notification-v2 status', returnStatus: true)
    //             if (serviceStatus == 0) {
    //                 echo 'Service staff-notification-v2 is running.'
    //             } else {
    //                 echo 'Service staff-notification-v2 is not running.'
    //             }
    //         }
 
    //     }
    // }
}
