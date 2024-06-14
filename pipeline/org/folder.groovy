def repos = [
    "static-site",
    "ami-jenkins",
    "infra-jenkins",
    "k8s-yaml-manifests",
    "webapp-migration",
    "webapp-cve-processor",
    "helm-webapp-cve-processor",
    "csye7125-test-repo"
]

repos.each { name ->
    folder(name) {
        description "Folder for the ${name} project" 
        displayName name
        icon {
            metadataActionFolderIcon()
        }
    }
}