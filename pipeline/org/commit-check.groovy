organizationFolder('commit-check') {
  description('Folder for the csye7125-su24-team05 organization')
  displayName('commit-check')
  organizations {
    github {
      repoOwner 'csye7125-su24-team05'
      apiUri 'https://api.github.com'
      credentialsId 'github-jenkins-app'
      enableAvatar true
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
          name 'commit-lint'
          suppressLogs false
          skipProgressUpdates false
        }
        multiBranchProjectDisplayNaming {
          displayNamingStrategy 'RAW_AND_OBJECT_DISPLAY_NAME'
        }
        pruneStaleBranch()
      }
    }
  }
  properties {
    organizationChildTriggersProperty {}
  }
  projectFactories {
    workflowMultiBranchProjectFactory {
      scriptPath(".jenkins/commit-lint")
    }
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