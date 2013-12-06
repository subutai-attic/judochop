# A Simple Performance Testing Framework

This is a simple performance testing framework designed to pound the heck out 
of anything you want to give a real bad day. The runners run your Perftest 
implementations inside Tomcat containers. The runner is designed as a webapp 
with a REST API to control distributed tests. The runners coordinate each other
to bombard in unison.

For now it only works on Tomcat because we use the manager web application to 
reload modified versions of the perftest.war overlaid with your project's
Perftest implementations and Modules. Also it only works in an Amazon EC2 
environment. It uses S3 to store runner configuration files to coordinate 
the cluster and to store test results.

The perftest-maven-plugin is used to generate your modified perftest.war file
with your test and module as well as the needed dependencies. All you have to
do is configure the plugin in your Maven project.

The framework simply executes a number of calls which you specify using a 
Perftest implementation class. This class specifies all the parameters to
run your test and encasulates it so you can write anything you like 
since the test interface is a single method. Guice is used to load your 
Module that instantiates your Perftest implementation. So you also have 
to write a short Guice Module for your Perftest. This sounds uber trivial and
it is but it allows you to hook in anything keeping things simple. If you
wanted to you could even programmatically run JMeter inside.

The following REST endpoints are used to control peers:

 * POST /start    (optional propagate boolean parameter)
 * POST /stop     (optional propagate boolean parameter)
 * POST /reset    (optional propagate boolean parameter)
 * GET  /stats
 * GET  /status
 * POST /load     perftest, (optional propagate boolean parameter)
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

## Dependencies

It uses the following libraries to do what it does:

* Jersey - for REST services
* AWS Java SDK
* Jackson - for JSON <--> Java marshalling
* Guice - for the DI container
* Archaius - for dynamic properties
* Blitz4j - for asynchronous logging
* Slf4j - API for binding to Blitz4j

