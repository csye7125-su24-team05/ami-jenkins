#!/bin/bash

# config jenkins
sudo mkdir /var/lib/jenkins/init.groovy.d
sudo cp /tmp/setup.groovy /var/lib/jenkins/init.groovy.d
# sudo cp -r /tmp/jenkins.yaml /var/lib/jenkins
sudo chown -R jenkins:jenkins /var/lib/jenkins/init.groovy.d
sudo cp /tmp/jenkins-config.yaml /var/lib/jenkins/jenkins.yaml
sudo chown -R jenkins:jenkins /var/lib/jenkins/jenkins.yaml

# config pipeline
sudo cp -r /tmp/pipeline /var/lib/jenkins
sudo chown -R jenkins:jenkins /var/lib/jenkins/pipeline

# sudo mkdir /var/lib/jenkins/pipeline
# sudo chown -R jenkins:jenkins /var/lib/jenkins/pipeline

# Install Jenkins CLI
wget http://localhost:8080/jnlpJars/jenkins-cli.jar -P /tmp

# Get the admin password
INITIAL_ADMIN_PASSWORD=$(sudo cat /var/lib/jenkins/secrets/initialAdminPassword)

# List of recommended plugins
RECOMMENDED_PLUGINS=(
  "ant:latest"
  "antisamy-markup-formatter:latest"
  "authorize-project:latest"
  "build-timeout:latest"
  "cloudbees-folder:latest"
  "configuration-as-code:latest"
  "credentials-binding:latest"
  "conventional-commits:latest"
  "docker-workflow:latest"
  "email-ext:latest"
  "git:latest"
  "github:latest"
  "github-branch-source:latest"
  "github-checks:latest"
  "gradle:latest"
  "job-dsl:latest"
  "ldap:latest"
  "mailer:latest"
  "matrix-auth:latest"
  "pam-auth:latest"
  "pipeline-github-lib:latest"
  "pipeline-stage-view:latest"
  "ssh-slaves:latest"
  "timestamper:latest"
  "workflow-aggregator:latest"
  "ws-cleanup:latest"
)

# Loop through the list of plugins and install each one
for plugin in "${RECOMMENDED_PLUGINS[@]}"; do
  java -jar /tmp/jenkins-cli.jar -s http://localhost:8080/ -auth admin:$INITIAL_ADMIN_PASSWORD install-plugin $plugin
done

java -jar /tmp/jenkins-cli.jar -s http://localhost:8080/ -auth admin:$INITIAL_ADMIN_PASSWORD restart

SECRETS_FILE=/var/lib/jenkins/secrets/secrets.properties 

sudo tee /etc/caddy/.env > /dev/null <<EOF
CADDY_TLSALPN01_DISABLED=true
TLD=$TLD
LETS_ENCRYPT_URL=$LETS_ENCRYPT_URL
EOF

sudo sed -i '/\[Service\]/a\EnvironmentFile=\/etc\/caddy\/.env' /usr/lib/systemd/system/caddy.service

sudo sed -i '/\[Service\]/a\Environment="SECRETS_FILE=/var/lib/jenkins/secrets/secrets.properties"' /usr/lib/systemd/system/jenkins.service
sudo sed -i 's/JAVA_OPTS=-Djava.awt.headless=true/JAVA_OPTS=-Djava.awt.headless=true -Djenkins.config=\/var\/lib\/jenkins\/jenkins.yaml/g' /usr/lib/systemd/system/jenkins.service

sudo tee -a $SECRETS_FILE > /dev/null <<EOF
USERNAME=$JENKINS_USERNAME
PASSWORD=$JENKINS_PASSWORD
GITHUB_ACCESS_TOKEN=$GITHUB_ACCESS_TOKEN
GITHUB_USERNAME=$GITHUB_USERNAME
DOCKER_USERNAME=$DOCKER_USERNAME
DOCKER_TOKEN=$DOCKER_TOKEN
GH_APP_ID=$GH_APP_ID
GH_APP_PRIVATE_KEY=$GH_APP_PRIVATE_KEY
EOF

sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

sudo systemctl daemon-reload
sudo systemctl restart jenkins
