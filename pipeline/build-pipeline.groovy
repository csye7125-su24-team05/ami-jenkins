def builder_repos = [
  [
    folderName: 'static-site',
    jobName: 'docker-build-and-push',
    repoUrl: 'https://github.com/csye7125-su24-team05/static-site',
    credentialsId: 'github-credentials',
    branch: '*/main',
    jenkinsFile: ['docker-build'],
  ]
]

builder_repos.each { builder_repo ->
  builder_repo.jenkinsFile.each { fileName ->
    pipelineJob("${builder_repo.folderName}/${builder_repo.jobName}") {
      definition {
        cpsScm {
          scm {
            git {
              remote {
                url builder_repo.repoUrl
                credentials builder_repo.credentialsId
              }
              branches builder_repo.branch
              extensions {
                cleanBeforeCheckout()
                gitSCMChecksExtension {
                  verboseConsoleLog true
                }
                gitSCMStatusChecksExtension {
                  name fileName
                  skip false
                  skipProgressUpdates true
                  suppressLogs false
                  unstableBuildNeutral false 
                }
              }
            }
          }
          scriptPath ".jenkins/"+fileName // Path to your Jenkinsfile in the repo
        }
      }
      triggers {
        githubPush()
      }
    }
  }
}
