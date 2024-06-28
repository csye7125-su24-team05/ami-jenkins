def repos = [
  [
    name: 'static-site',
    url: 'https://github.com/csye7125-su24-team05/static-site.git',
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'webapp-migration',
    url: 'https://github.com/csye7125-su24-team05/webapp-migration.git',
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'webapp-cve-processor',
    url: 'https://github.com/csye7125-su24-team05/webapp-cve-processor.git',
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-webapp-cve-processor',
    url: 'https://github.com/csye7125-su24-team05/helm-webapp-cve-processor.git',
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'csye7125-test-repo',
    url: 'https://github.com/csye7125-su24-team05/csye7125-test-repo.git',
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'webapp-cve-consumer',
    url: 'https://github.com/csye7125-su24-team05/webapp-cve-consumer.git',
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-webapp-cve-consumer',
    url: 'https://github.com/csye7125-su24-team05/helm-webapp-cve-consumer.git',
    credentialsId: 'github-jenkins-app',
  ]
]

repos.each { repo ->
  pipelineJob("${repo.name}/release") {
    definition {
      cpsScm {
        scm {
          git {
            remote {
              url repo.url
              credentials repo.credentialsId
            }
            branches '*/main'
            extensions {
              localBranch()
              cleanBeforeCheckout()
              gitSCMChecksExtension {
                verboseConsoleLog true
              }
              gitSCMStatusChecksExtension {
                name 'release'
                skip false
                skipProgressUpdates true
                suppressLogs false
                unstableBuildNeutral false 
              }
              userExclusion {
                excludedUsers 'semantic-release-bot'
              }
            }
          }
        }
        scriptPath ".jenkins/release"
      }
    }
    triggers {
      githubPush()
    }
  }
}