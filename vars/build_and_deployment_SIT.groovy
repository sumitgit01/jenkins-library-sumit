def call() {
    pipeline {
        agent any
        stages {
            stage('Build_Provisioning'){
                steps { 
                    script {
                        cleanWs()
                        echo 'Building the Provisioning'
                        sh 'echo "Building the Provisioning"'
                        sh """
                            git clone https://github.com/minaxijoshi3101/seh-students.git
                            cd seh-students
                            mvn clean package
                            ls -lart target/
                        """
                   }
                }
            }
        }
    }
}