#!/bin/bash

# jenkins
sudo apt-get install fontconfig openjdk-17-jre -y
sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt-get update
sudo apt-get install jenkins -y
sudo apt install -y docker.io

sudo usermod -aG docker jenkins
# start docker
sudo systemctl start docker
sudo systemctl enable docker

# start jenkins
sudo systemctl enable jenkins
sudo systemctl start jenkins

# caddy
# sudo apt install -y debian-keyring debian-archive-keyring apt-transport-https curl
# curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
# curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | sudo tee /etc/apt/sources.list.d/caddy-stable.list
# sudo apt-get install caddy -y
# sudo setcap cap_net_bind_service=+ep $(which caddy)


# # config caddy
# sudo cp /tmp/Caddyfile /etc/caddy
# sudo systemctl enable caddy


