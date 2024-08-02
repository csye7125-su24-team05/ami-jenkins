def repos = [
  [
    name: 'static-site',
    url: 'https://github.com/csye7125-su24-team05/static-site.git',
    scriptPath: ['release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'webapp-migration',
    url: 'https://github.com/csye7125-su24-team05/webapp-migration.git',
    scriptPath: ['release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'webapp-cve-processor',
    url: 'https://github.com/csye7125-su24-team05/webapp-cve-processor.git',
    scriptPath: ['release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-webapp-cve-processor',
    url: 'https://github.com/csye7125-su24-team05/helm-webapp-cve-processor.git',
    scriptPath: ['release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'csye7125-test-repo',
    url: 'https://github.com/csye7125-su24-team05/csye7125-test-repo.git',
    scriptPath: ['release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'webapp-cve-consumer',
    url: 'https://github.com/csye7125-su24-team05/webapp-cve-consumer.git',
    scriptPath: ['release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-webapp-cve-consumer',
    url: 'https://github.com/csye7125-su24-team05/helm-webapp-cve-consumer.git',
    scriptPath: ['release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-eks-autoscaler',
    url: 'https://github.com/csye7125-su24-team05/helm-eks-autoscaler.git',
    scriptPath: ['release', 'docker-release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'cve-operator',
    url: 'https://github.com/csye7125-su24-team05/cve-operator.git',
    scriptPath: ['docker-release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-cve-operator',
    url: 'https://github.com/csye7125-su24-team05/helm-cve-operator.git',
    scriptPath: ['helm-release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'kube-prometheus-stack',
    url: 'https://github.com/csye7125-su24-team05/kube-prometheus-stack.git',
    scriptPath: ['helm-release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-cloudwatch',
    url: 'https://github.com/csye7125-su24-team05/helm-cloudwatch.git',
    scriptPath: ['helm-release'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'infra-aws',
    url: 'https://github.com/csye7125-su24-team05/infra-aws.git',
    scriptPath: ['docker-release'],
    credentialsId: 'github-jenkins-app',
  ]
]

repos.each { repo ->
  repo.scriptPath.each { script ->
    pipelineJob("${repo.name}/${script}") {
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
                  name script
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
          scriptPath ".jenkins/${script}"
        }
      }
      triggers {
        githubPush()
      }
    }
  }
}