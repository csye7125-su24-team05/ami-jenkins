sudo npm install -g semantic-release @semantic-release/commit-analyzer @semantic-release/release-notes-generator @semantic-release/changelog @semantic-release/github @semantic-release/git @commitlint/config-conventional @commitlint/cli

sudo mkdir -p /etc/jenkins
sudo tee -a /etc/jenkins/commitlint.config.js > /dev/null <<EOF
module.exports = {
    extends: ['@commitlint/config-conventional']
};
EOF