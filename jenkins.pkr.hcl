packer {
  required_plugins {
    amazon = {
      version = ">= 1.2.8"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "env" {
  type = map(string)
  default = {
    jenkins_username   = env("JENKINS_USERNAME")
    jenkins_password   = env("JENKINS_PASSWORD")
    github_username    = env("GITHUB_USERNAME")
    github_token       = env("GITHUB_ACCESS_TOKEN")
    docker_username    = env("DOCKER_USERNAME")
    docker_token       = env("DOCKER_TOKEN")
    gh_app_id          = env("GH_APP_ID")
    gh_app_private_key = env("GH_APP_PRIVATE_KEY")
    tld                = env("TLD")
    lets_encrypt_url   = env("LETS_ENCRYPT_URL")
  }
  sensitive = true
}

source "amazon-ebs" "ubuntu-jenkins" {
  ami_name        = "jenkins-ami-{{timestamp}}"
  ami_description = "Jenkins AMI with caddy reverse proxy and let's encrypt certificate"

  ami_virtualization_type = "hvm"
  tags = {
    Name = "jenkins-ami"
  }
  ami_block_device_mappings {
    device_name           = "/dev/sda1"
    volume_size           = 20
    volume_type           = "gp3"
    delete_on_termination = true
  }
  source_ami_filter {
    filters = {
      name                = "ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-amd64-server-*"
      root-device-type    = "ebs"
      virtualization-type = "hvm"
    }
    most_recent = true
    owners      = ["099720109477"]
  }
  instance_type = "t2.small"
  region        = "us-east-1"
  ssh_username  = "ubuntu"
}

build {
  sources = ["source.amazon-ebs.ubuntu-jenkins"]

  provisioner "file" {
    sources     = ["files/", "pipeline"]
    destination = "/tmp"
  }

  provisioner "shell" {
    environment_vars = [
      "JENKINS_USERNAME=${var.env.jenkins_username}",
      "JENKINS_PASSWORD=${var.env.jenkins_password}",
      "GITHUB_ACCESS_TOKEN=${var.env.github_token}",
      "GITHUB_USERNAME=${var.env.github_username}",
      "DOCKER_USERNAME=${var.env.docker_username}",
      "DOCKER_TOKEN=${var.env.docker_token}",
      "GH_APP_ID=${var.env.gh_app_id}",
      "GH_APP_PRIVATE_KEY=${var.env.gh_app_private_key}",
      "TLD=${var.env.tld}",
      "LETS_ENCRYPT_URL=${var.env.lets_encrypt_url}"
    ]
    scripts = ["scripts/install.sh", "scripts/setup.sh", "scripts/node.sh", "scripts/cleanup.sh"]
  }
}
