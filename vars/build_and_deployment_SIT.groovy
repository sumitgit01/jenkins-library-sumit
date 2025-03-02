def call() {
    pipeline {
        agent any
        environment {
            NEXUS_VERSION = "nexus3"
            //NEXUS_PROTOCOL = "http"
            //NEXUS_URL = env.NexusUrl
            NEXUS_REPOSITORY = "seh_students"
            NEXUS_CREDENTIAL_ID = "nexusCredential"
        }
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
            stage('Push Artifacts to Nexus') {
                steps {
                    script {
                        dir('seh-students') {
                            print env.NexusUrl
                            pom = readMavenPom file: "pom.xml";
                            filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                            echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                            artifactPath = filesByGlob[0].path;
                            artifactExists = fileExists artifactPath;
                            if(artifactExists) {
                                echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
                                nexusArtifactUploader(
                                    nexusVersion: NEXUS_VERSION,
                                    nexusUrl: env.NexusUrl,
                                    groupId: pom.groupId,
                                    version: pom.version,
                                    repository: NEXUS_REPOSITORY,
                                    credentialsId: NEXUS_CREDENTIAL_ID,
                                    artifacts: [
                                        [artifactId: pom.artifactId,
                                        classifier: '',
                                        file: artifactPath,
                                        type: pom.packaging],
                                        [artifactId: pom.artifactId,
                                        classifier: '',
                                        file: "pom.xml",
                                        type: "pom"]
                                    ]
                                );
                            } else {
                                error "*** File: ${artifactPath}, could not be found"; 
                            }
                        }
                    }
                }
            }
        }
    }
}