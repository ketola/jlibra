# Releasing JLibra to Maven Central

1. Set the release version number for the project
  * `mvn versions:set -DnewVersion=<version number>`
2. Deploy to Maven Central
  * `mvn clean deploy -Prelease,ossrh -Dgpg.passphrase=*******`
  * Check from https://repo1.maven.org/maven2/dev/jlibra/jlibra-core/ that the artifact has appeared there, it can take longer for the artifacts to show up in the search
3. Give a new snapshot version number for the project 
  * `mvn versions:set -DnewVersion=<version number>-SNAPSHOT`
4. Commit and push the changes to git

For more information read: https://central.sonatype.org/pages/apache-maven.html
