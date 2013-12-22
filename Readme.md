![Judo Chop](main/judo-chop.jpeg)

# What is it?

Judo Chop is a simple distributed performance testing framework designed to
pound the heck out of anything you want to give a REALLY BAD DAY! Performance
testing has never been easier. Just annotate your JUnit tests with TimeChop or 
IterationChop annotations telling Judo Chop how to chop it up. Judo Chop 
uses your own projects tests as drivers to bombard your application, service,
or server.

# How does it work?  

Judo Chop has two kinds of annotations you apply to JUnit tests. Each 
annotation value and the annotations applying to tests are listed below:
 
     * time (TimeChop) - time in milliseconds to run the test
     * iterations (IterationChop) - iterations of the test to run per thread
     * threads (Both) - the number of threads to use per runner
     * runners (Both) - the number of distributed runners to use
     * delay (Both) - the number of milliseconds to delay between test runs
     * saturate (Both) - find and run tests at saturation

It's probably already clear how this thingy works. The Chop annotations tell 
Judo Chop how to run your JUnit or Jukito tests. Of course it's up to you to
make sure your chop tests actually pound on something else rather than running
locally. So you guessed it Judo Chop is for pounding on services, servers and
web applications.

Judo Chop's maven plugin will take your annotated tests and build a runner war
out of it. The plugin will deploy it to several virtual machines. The plugin
or just a curl start command against the rest API of one of the runners triggers
their synchronous bombardment of your application via your own tests. Reports
are generated and placed in a store where they can be later analyzed.

Each time you change your source and commit to your version control system, 
Judo Chop deploys your new source and associates it with your project and its
version. That way you have a history of performance metrics collected for 
your source associated with your VCS.

## Future Enhancements

* Inject yammer metrics and send the results to Graphite
* Build a results visualization console
* Support more VCS', more stores, and more virtual environments 
* Better chop synchronization and start stop boundry alignment
* Actually implement saturate which fires up tests in a chop and increases
  parameters of the chop to find out the point at which the throughput and
  performance of your target ceases to improve.

# How do I use it?

First add the Judo Chop maven plugin to your project like so:

~~~~~~

      <plugin>
        <groupId>org.safehaus.chop</groupId>
        <artifactId>chop-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
          <accessKey>${aws.s3.key}</accessKey>
          <secretKey>${aws.s3.secret}</secretKey>
          <bucketName>${aws.s3.bucket}</bucketName>
          <managerAppUsername>admin</managerAppUsername>
          <managerAppPassword>${manager.app.password}</managerAppPassword>
          <testPackageBase>org.safehaus.chop.example</testPackageBase>
          <runnerSSHKeyFile>${runner.ssh.key.file}</runnerSSHKeyFile>
          <failIfCommitNecessary>false</failIfCommitNecessary>
          <amiID>${ami.id}</amiID>
          <awsSecurityGroup>${security.group}</awsSecurityGroup>
          <runnerKeyPairName>${runner.keypair.name}</runnerKeyPairName>
          <minimumRunners>5</minimumRunners>
          <maximumRunners>8</maximumRunners>
          <securityGroupExceptions>
            <param>21.14.31.218/32</param>
          </securityGroupExceptions>
        </configuration>
      </plugin>

~~~~~~

Give yourself a chop on the back if you guessed that Judo Chop works in
the EC2 environment. Everything here is pretty self explanatory and if it
is not then well ping us about it on judo-chop AT safehaus.org. 

Oh and you'll also need to generate a <artifact>-tests.jar by using the
maven jar plugin. When you do a chop:deploy Judo Chop will take your tests
and bundle them into a war file and load them into an S3 buck prefixed with
your projects last commit. Right now note that only git and S3 and EC2 are
supported as the VCS, result store, and virtualization environment respectively.

The following REST endpoints are used to control peers:

 * POST /start    (optional *propagate* boolean parameter)
 * POST /stop     (optional *propagate* boolean parameter)
 * POST /reset    (optional *propagate* boolean parameter)
 * GET  /stats
 * GET  /status
 * POST /load     *project*, (optional *propagate* boolean parameter)
 * GET  /tests

The following ascii text shows the states of the framework which one can 
go through while issuing POSTs to the end points above:

~~~~~~~

            start           stop
    +-----+       +-------+      +-------+
--->+ready+------>+running+----->+stopped|
    +--+--+       +-------+      +---+---+
       ^                             |
       |_____________________________v
                    reset

~~~~~~~

A post to a single node using the optional boolean propagate option will issue
the same POST to all the nodes in the cluster. All the POST operations support
this property to control all the runner nodes in the cluster. Also note that 
the propagation occurs in parallel against all nodes in the cluster.

Happy Chopping!
The Judo Chop Team

