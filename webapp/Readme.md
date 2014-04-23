# What is Wagon Plugin?

"The Maven Wagon Plugin, as the name implies, allows you to use various functions of a maven wagon.
It allows you to upload resources from your build to a remote location using wagon.
It allows you to download resources from a repository using wagon.
It allows to list a content of a repository using wagon.
Finally, by combining the upload and download capabilities, it can merge a Maven repository to another in a generic way." [1]

# How to use Wagon Plugin

First of all, you may need to run the following command to get the necessary libraries
for wagon plugin : "mvn wagon:update-maven-3"
Then, you need to set the following properties inside settings.xml (e.g. /etc/maven/settings.xml) file
which may look like as follows:

~~~~~~

    <server>
        <id>ec2-coordinator-instance</id>
        <username>ubuntu</username>
        <privateKey>/path/to/key-file.pem</privateKey>
    </server>

    <profile>
        <id>deploy-chop-webapp</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <chop.coordinator.url>ec2-xxx.amazonaws.com</chop.coordinator.url>
        </properties
    </profile>

~~~~~~

Then, you may run the following goals for the following operations inside webapp module:
*wagon:upload-single -> Uploads the required jar file for webapp to the specified machine.
*wagon:sshexec       -> Runs the necessary commands for the newly uploaded webapp to take effect.

That's it. Ready to go. You just started the web application with the new one!

# References
[1] http://mojo.codehaus.org/wagon-maven-plugin/usage.html