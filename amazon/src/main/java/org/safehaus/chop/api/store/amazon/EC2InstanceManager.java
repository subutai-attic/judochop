package org.safehaus.chop.api.store.amazon;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.safehaus.chop.stack.ICoordinatedCluster;
import org.safehaus.chop.stack.ICoordinatedStack;
import org.safehaus.chop.stack.Instance;
import org.safehaus.chop.spi.InstanceManager;
import org.safehaus.chop.stack.InstanceState;
import org.safehaus.chop.spi.LaunchResult;
import org.safehaus.chop.stack.BasicInstanceSpec;
import org.safehaus.chop.stack.InstanceSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.google.inject.Inject;


/** Implements all InstanceManager functionality for AmazonAWS  */
public class EC2InstanceManager implements InstanceManager {

    private static Logger LOG = LoggerFactory.getLogger( EC2InstanceManager.class );

    private static final long SLEEP_LENGTH = 3000;

    @Inject
    private AmazonFig amazonFig;

    private AmazonEC2Client client;


    public EC2InstanceManager() {
        client = getEC2Client( amazonFig.getAwsAccessKey(), amazonFig.getAwsSecretKey() );

    }


    @Override
    public int getDefaultTimeout() {
        return 0;
    }


    @Override
    public void terminateInstances( final Collection<String> instancesIds ) {
        TerminateInstancesRequest request = ( new TerminateInstancesRequest() ).withInstanceIds( instancesIds );
        client.terminateInstances( request );

        // TODO should we wait until all terminated?
    }


    @Override
    public LaunchResult launchCluster( ICoordinatedStack stack, ICoordinatedCluster cluster, int timeout ) {

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

        runInstancesRequest.withImageId( cluster.getInstanceSpec().getImageId() )
                           .withInstanceType( cluster.getInstanceSpec().getType() )
                           .withMinCount( cluster.getSize() )
                           .withMaxCount( cluster.getSize() )
                           .withKeyName( cluster.getInstanceSpec().getKeyName() )
                           .withSecurityGroups( stack.getIpRuleSet().getName() );

        if( stack.getDataCenter() != null && ! stack.getDataCenter().isEmpty() ) {
            runInstancesRequest = runInstancesRequest.withPlacement( new Placement( stack.getDataCenter() ) );
        }

        RunInstancesResult runInstancesResult = client.runInstances( runInstancesRequest) ;

        LOG.info( "Created instances, setting the names now..." );

        List<String> instanceIds = new ArrayList<String>( cluster.getSize() );

        String instanceNames = getInstanceName( stack, cluster );

        int i = 0;
        for( com.amazonaws.services.ec2.model.Instance instance : runInstancesResult.getReservation().getInstances() ) {

            try {
                instanceIds.add( i, instance.getInstanceId() );
                LOG.debug( "Setting name of cluster instance with id: {}", instanceIds.get( i ) );

                List<Tag> tags = new ArrayList<Tag>();

                Tag t = new Tag();
                t.setKey( "Name" );
                t.setValue( instanceNames );
                tags.add( t );

                CreateTagsRequest ctr = new CreateTagsRequest();
                ctr.setTags( tags );
                ctr.withResources( instanceIds.get( i ) );
                client.createTags( ctr );
            }
            catch ( Exception e ) {
                LOG.warn( "Error while setting names", e );
            }
            i++;
        }

        LOG.info( "Names of the instances are set" );

        if ( timeout > SLEEP_LENGTH ) {
            LOG.info( "Waiting for maximum {} msec until all instances are running", timeout );
            boolean stateCheck = waitUntilRunning( instanceIds, timeout );

            if ( ! stateCheck ) {
                LOG.warn( "Waiting for instances to get into Running state has timed out" );
            }
        }

        // TODO should we run setup scripts here?

        return null; // TODO LaunchResult
    }


    @Override
    public LaunchResult launchRunners( ICoordinatedStack stack, InstanceSpec spec, int count, int timeout ) {
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

        runInstancesRequest.withImageId( spec.getImageId() )
                           .withInstanceType( spec.getType() )
                           .withMinCount( count )
                           .withMaxCount( count )
                           .withKeyName( spec.getKeyName() )
                           .withSecurityGroups( stack.getIpRuleSet().getName() );

        if( stack.getDataCenter() != null && ! stack.getDataCenter().isEmpty() ) {
            runInstancesRequest = runInstancesRequest.withPlacement( new Placement( stack.getDataCenter() ) );
        }

        RunInstancesResult runInstancesResult = client.runInstances( runInstancesRequest) ;

        LOG.info( "Created instances, setting the names now..." );

        List<String> instanceIds = new ArrayList<String>( count );

        String runnerNames = getRunnerName( stack );

        int i = 0;
        for( com.amazonaws.services.ec2.model.Instance instance : runInstancesResult.getReservation().getInstances() ) {

            try {
                instanceIds.add( i, instance.getInstanceId() );
                LOG.debug( "Setting name of runner instance with id: {}", instanceIds.get( i ) );

                List<Tag> tags = new ArrayList<Tag>();

                Tag t = new Tag();
                t.setKey( "Name" );
                t.setValue( runnerNames );
                tags.add( t );

                CreateTagsRequest ctr = new CreateTagsRequest();
                ctr.setTags( tags );
                ctr.withResources( instanceIds.get( i ) );
                client.createTags( ctr );
            }
            catch ( Exception e ) {
                LOG.warn( "Error while setting names", e );
            }
            i++;
        }

        LOG.info( "Names of the instances are set" );

        if ( timeout > SLEEP_LENGTH ) {
            LOG.info( "Waiting for maximum {} msec until all instances are running", timeout );
            boolean stateCheck = waitUntilRunning( instanceIds, timeout );

            if ( ! stateCheck ) {
                LOG.warn( "Waiting for instances to get into Running state has timed out" );
            }
        }

        return null; // TODO LaunchResult
    }


    @Override
    public Collection<Instance> getClusterInstances( ICoordinatedStack stack, ICoordinatedCluster cluster ) {

        String name = getInstanceName( stack, cluster );

        return toInstances( getEC2Instances( name, null ) );

    }


    @Override
    public Collection<Instance> getRunnerInstances( ICoordinatedStack stack ) {

        String name = getRunnerName( stack );

        return toInstances( getEC2Instances( name, null ) );

    }


    /**
     * @param name Causes the method to return instances with given Name tag, give null if you want to get
     * instances with all names
     * @param state Causes the method to return instances with given state, give null if you want to get instances in
     * all states
     * @return Returns all instances that satisfy given parameters
     */
    protected Collection<com.amazonaws.services.ec2.model.Instance> getEC2Instances( String name,
                                                                               InstanceStateName state ) {

        Collection<com.amazonaws.services.ec2.model.Instance> instances =
                new LinkedList<com.amazonaws.services.ec2.model.Instance>();

        DescribeInstancesRequest request = new DescribeInstancesRequest();

        if ( name != null ) {

            List<String> valuesT1 = new ArrayList<String>();
            valuesT1.add( name );
            Filter filter = new Filter("tag:Name", valuesT1);
            request = request.withFilters( filter );

        }

        if ( state != null ) {

            List<String> valuesT1 = new ArrayList<String>();
            valuesT1.add( state.toString() );
            Filter filter = new Filter( "instance-state-name", valuesT1 );
            request = request.withFilters( filter );

        }

        DescribeInstancesResult result = client.describeInstances( request );

        for ( Reservation reservation : result.getReservations() ) {
            for ( com.amazonaws.services.ec2.model.Instance in : reservation.getInstances() ) {
                instances.add( in );
            }
        }

        return instances;
    }


    protected Collection<Instance> toInstances( Collection<com.amazonaws.services.ec2.model.Instance> ec2s ) {
        Collection<Instance> instances = new ArrayList<Instance>( ec2s.size() );

        for( com.amazonaws.services.ec2.model.Instance ec2 : ec2s ) {
            instances.add( toInstance( ec2 ) );
        }

        return instances;
    }


    protected Instance toInstance( com.amazonaws.services.ec2.model.Instance ec2 ) {
        Instance instance;
        BasicInstanceSpec spec;

        spec = new BasicInstanceSpec();
        spec.setImageId( ec2.getImageId() );
        spec.setKeyName( ec2.getKeyName() );
        spec.setType( ec2.getInstanceType() );

        instance = new AmazonInstance(
                        ec2.getInstanceId(),
                        spec,
                        InstanceState.fromValue( ec2.getState().getName() ),
                        ec2.getPrivateDnsName(),
                        ec2.getPublicDnsName(),
                        ec2.getPrivateIpAddress(),
                        ec2.getPublicIpAddress()
                );

        return instance;
    }


    /**
     * Checks the state of all given instances in SLEEP_LENGTH intervals, returns when all instances are in running
     * state or state check times out
     * @param instanceIds List of instance IDs whose states are going to be checked
     * @param timeout Timeout length in milliseconds
     * @return Returns true if all instances are in running state, false if timeout occured
     */
    protected boolean waitUntilRunning ( Collection<String> instanceIds, int timeout ) {

        List<String> instanceIdCopy = new ArrayList<String>( instanceIds );
        Calendar cal = Calendar.getInstance();
        cal.setTime( new Date() );
        long startTime = cal.getTimeInMillis();
        long timePassed;

        do {
            DescribeInstancesRequest dis = ( new DescribeInstancesRequest() ).withInstanceIds( instanceIdCopy );
            DescribeInstancesResult disresult = client.describeInstances( dis );
            // Since the request is filtered with instance IDs, there is always only one Reservation object
            Reservation reservation  = disresult.getReservations().iterator().next();
            for ( com.amazonaws.services.ec2.model.Instance in : reservation.getInstances() ) {
                LOG.info( "{} is {}", in.getInstanceId(), in.getState().getName() );
                if ( in.getState().getName().equals( InstanceStateName.Running.toString() ) ) {
                    instanceIdCopy.remove( in.getInstanceId() );
                    LOG.info( "Instance started running with ID: {}", in.getInstanceId() );
                }
            }
            cal.setTime( new Date() );
            timePassed = cal.getTimeInMillis() - startTime;
            try {
                Thread.sleep( SLEEP_LENGTH );
            }
            catch ( InterruptedException e ) {
                LOG.warn( "Thread interrupted while sleeping", e );
            }
        }
        while ( timePassed < timeout && instanceIdCopy.size() > 0 );

        return ( timePassed < timeout );
    }


    /**
     * @param accessKey
     * @param secretKey
     * @return
     */
    protected AmazonEC2Client getEC2Client( String accessKey, String secretKey ) {
        AWSCredentialsProvider provider;
        if ( accessKey != null && secretKey != null ) {
            AWSCredentials credentials = new BasicAWSCredentials( accessKey, secretKey );
            provider = new StaticCredentialsProvider( credentials );
        }
        else {
            provider = new DefaultAWSCredentialsProviderChain();
        }

        AmazonEC2Client client = new AmazonEC2Client( provider );

        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProtocol( Protocol.HTTPS );
        client.setConfiguration( configuration );
        return client;
    }


    protected static String getLongName( ICoordinatedStack stack ) {

        StringBuilder sb = new StringBuilder();
        sb.append( stack.getUser().getUsername() )
                .append( "-" ).append( stack.getModule().getGroupId() )
                .append( "-" ).append( stack.getModule().getArtifactId() )
                .append( "-" ).append( stack.getModule().getVersion() )
                .append( "-" ).append( stack.getCommit().getId() )
                .append( "-" ).append( stack.getName() );

        return sb.toString();
    }


    protected static String getInstanceName( ICoordinatedStack stack, ICoordinatedCluster cluster ) {
        StringBuilder sb = new StringBuilder();
        sb.append( getLongName( stack ).hashCode() ).append( "-" ).append( cluster.getName() );
        return sb.toString();
    }


    protected static String getRunnerName( ICoordinatedStack stack ) {
        StringBuilder sb = new StringBuilder();
        sb.append( getLongName( stack ).hashCode() ).append( "-runner" );
        return sb.toString();

    }
}
