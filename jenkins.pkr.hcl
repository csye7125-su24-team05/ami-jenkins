packer {
  required_plugins {
    amazon = {
      version = ">= 1.2.8"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "jenkins_username" {
  type    = string
  default = env("JENKINS_USERNAME")
}

variable "jenkins_password" {
  type      = string
  default   = env("JENKINS_PASSWORD")
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
  instance_type = "t2.micro"
  region        = "us-east-1"
  ssh_username  = "ubuntu"
}

build {
  sources = ["source.amazon-ebs.ubuntu-jenkins"]

  provisioner "file" {
    sources     = ["files/Caddyfile", "files/setup.groovy", "files/jenkins-config.yaml"]
    destination = "/tmp/"
  }

  provisioner "shell" {
    environment_vars = [
      "JENKINS_USERNAME=${var.jenkins_username}",
      "JENKINS_PASSWORD=${var.jenkins_password}",
    ]
    scripts = ["scripts/install.sh", "scripts/setup.sh", "scripts/cleanup.sh"]
  }
}
