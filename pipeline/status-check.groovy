def repos = [
  [
    name: 'static-site',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/static-site.git',
    scriptPath: ['docker-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'ami-jenkins',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/ami-jenkins.git',
    scriptPath: ['packer-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'infra-jenkins',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/infra-jenkins.git',
    scriptPath: ['terraform-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'k8s-yaml-manifests',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/k8s-yaml-manifests.git',
    scriptPath: ['yaml-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-webapp-cve-processor',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/helm-webapp-cve-processor.git',
    scriptPath: ['helm-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'webapp-cve-processor',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/webapp-cve-processor.git',
    scriptPath: ['docker-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'webapp-migration',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/webapp-migration.git',
    scriptPath: ['docker-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'csye7125-test-repo',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/csye7125-test-repo.git',
    scriptPath: ['test-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'infra-aws',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/infra-aws.git',
    scriptPath: ['terraform-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'webapp-cve-consumer',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com:csye7125-su24-team05/webapp-cve-consumer.git',
    scriptPath: ['docker-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-webapp-cve-consumer',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/helm-webapp-cve-consumer.git',
    scriptPath: ['helm-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-eks-autoscaler',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/helm-eks-autoscaler.git',
    scriptPath: ['helm-validate', 'docker-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'cve-operator',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/cve-operator.git',
    scriptPath: ['docker-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-cve-operator',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/helm-cve-operator.git',
    scriptPath: ['helm-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'kube-prometheus-stack',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/kube-prometheus-stack.git',
    scriptPath: ['helm-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'helm-cloudwatch',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/helm-cloudwatch.git',
    scriptPath: ['helm-validate'],
    credentialsId: 'github-jenkins-app',
  ],
  [
    name: 'infra-aws',
    owner: 'csye7125-su24-team05',
    url: 'https://github.com/csye7125-su24-team05/infra-aws.git',
    scriptPath: ['docker-validate'],
    credentialsId: 'github-jenkins-app',
  ]
  
]

repos.each { repo ->
  repo.scriptPath.each { jobName ->
    multibranchPipelineJob("${repo.name}/${jobName}") {
      branchSources {
        branchSource {
          source {
            github {
              id UUID.randomUUID().toString()
              apiUri 'https://api.github.com'
              repoOwner repo.owner
              repository repo.name
              repositoryUrl repo.url
              credentialsId repo.credentialsId
              configuredByUrl true
              traits {
                gitHubForkDiscovery {
                  strategyId 2
                  trust {
                    gitHubTrustPermissions()
                  }
                }
                gitHubPullRequestDiscovery {
                  strategyId 2
                }
                gitHubSourceChecks {
                  verboseConsoleLog true
                }
                gitHubStatusChecks {
                  skip false
                  skipNotifications true
                  unstableBuildNeutral false
                  name jobName
                  suppressLogs false
                  skipProgressUpdates false
                }
                multiBranchProjectDisplayNaming {
                  displayNamingStrategy 'RAW_AND_OBJECT_DISPLAY_NAME'
                }
              }
            }
          }
        }
      }
      factory {
        workflowBranchProjectFactory {
          scriptPath ".jenkins/"+jobName
        }
      }
      icon {
        metadataActionFolderIcon()
      }
      orphanedItemStrategy {
        defaultOrphanedItemStrategy {
          pruneDeadBranches true
          daysToKeepStr "15"
          numToKeepStr "15"
        }
        discardOldItems {
          numToKeep 15
          daysToKeep 15
        }
      }
    }
  }
}