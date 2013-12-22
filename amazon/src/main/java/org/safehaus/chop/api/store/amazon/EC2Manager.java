package org.safehaus.chop.api.store.amazon;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;


public class EC2Manager {

    private static final Logger LOG = LoggerFactory.getLogger( EC2Manager.class );

    private static final long SLEEP_LENGTH = 3000;

    private int defaultTimeout = 75000;

    private String accessKey;

    private String secretKey;

    private String amiId;

    private String securityGroup;

    private String keyName;

    private String runnerName;

    private InstanceType defaultType = InstanceType.M1Medium;

    private AmazonEC2Client client;


    public int getDefaultTimeout() {
        return defaultTimeout;
    }


    public void setDefaultTimeout( final int defaultTimeout ) {
        this.defaultTimeout = defaultTimeout;
    }


    public InstanceType getDefaultType() {
        return defaultType;
    }


    public void setDefaultType( final InstanceType defaultType ) {
        this.defaultType = defaultType;
    }


    public EC2Manager( String accessKey, String secretKey, String amiId, String securityGroup, String keyName,
                     String runnerName ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.amiId = amiId;
        this.securityGroup = securityGroup;
        this.keyName = keyName;
        this.runnerName = runnerName;

        client = getEC2Client( accessKey, secretKey );
    }


    public void close() {
        if ( client != null ) {
            client.shutdown();
            client = null;
        }
    }


    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        }
        finally {
            super.finalize();
        }
    }


    public boolean ensureRunningInstancesExact( int count ) {
        return ensureRunningInstances( count, count );
    }


    public boolean ensureRunningInstancesMin( int minimumCount ) {
        return ensureRunningInstances( minimumCount, Integer.MAX_VALUE );
    }


    public boolean ensureRunningInstancesMax( int maximumCount ) {
        return ensureRunningInstances( 1, maximumCount );
    }


    /**
     * @param minCount Must be positive, inclusive
     * @param maxCount Must be bigger than or equal to minCount, inclusive
     * @return true if running instance count is already within limits or successfully
     */
    public boolean ensureRunningInstances( int minCount, int maxCount ) {
        if ( minCount <= 0 ) {
            throw new IllegalArgumentException( "Count should be positive" );
        }
        if ( maxCount < minCount ) {
            throw new IllegalArgumentException( "Maximum count can't be negative or smaller than minimum" );
        }

        Collection<Instance> activeInstances = getInstances( runnerName, InstanceStateName.Running );
        if ( activeInstances.size() >= minCount && activeInstances.size() <= maxCount ) {
            return true;
        }

        if ( activeInstances.size() < minCount ) {
            int increaseCount = minCount - activeInstances.size();
            LOG.info( "Adding {} more instance(s)", increaseCount );
            launchEC2Instances( defaultType, increaseCount );
        }

        if ( activeInstances.size() > maxCount ) {
            int decreaseCount = activeInstances.size() - maxCount;
            LOG.info( "Terminating {} instance(s)", decreaseCount );
            Collection<String> instances = new ArrayList<String>();
            int i = 1;
            for ( Instance instance : activeInstances ) {
                instances.add( instance.getInstanceId() );
                if ( ++i > decreaseCount ) {
                    break;
                }
            }
            terminateEC2Instances( instances );
        }

        activeInstances = getInstances( runnerName, InstanceStateName.Running );
        return ( activeInstances.size() >= minCount && activeInstances.size() <= maxCount );
    }


    /**
     * @param instanceIds List of instance IDs to terminate
     * @return
     */
    public TerminateInstancesResult terminateEC2Instances ( Collection<String> instanceIds ) {
        TerminateInstancesRequest request = ( new TerminateInstancesRequest() ).withInstanceIds( instanceIds );
        return client.terminateInstances( request );
    }


    public RunInstancesResult launchEC2Instances( InstanceType instanceType, int count ) {
        return launchEC2Instances( instanceType, count, defaultTimeout );
    }


    /**
     * @param instanceType Be careful to provide a type compatible with the used AMI
     * @param count Number of instances to launch
     * @param timeout In milliseconds. If set to a value bigger than SLEEP_LENGTH, method waits for all launched
     * instances to get into running state or times out.
     * @return
     */
    public RunInstancesResult launchEC2Instances( InstanceType instanceType, int count, int timeout ) {

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        runInstancesRequest.withImageId( amiId ).withInstanceType( instanceType ).withMinCount( count )
                           .withMaxCount( count ).withKeyName( keyName ).withSecurityGroups( securityGroup );

        RunInstancesResult runInstancesResult = client.runInstances( runInstancesRequest) ;

        LOG.info( "Created instances, setting the names now..." );

        List<String> instanceIds = new ArrayList<String>( count );

        int i = 0;
        for ( Instance instance : runInstancesResult.getReservation().getInstances() ) {

            try {
                instanceIds.add( i, instance.getInstanceId() );
                LOG.debug( "Setting name of instance with id: {}", instanceIds.get( i ) );

                List<Tag> tags = new ArrayList<Tag>();

                Tag t = new Tag();
                t.setKey( "Name" );
                t.setValue( runnerName );
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

        return runInstancesResult;
    }


    public Collection<Instance> getInstances() {
        return getInstances( null, null );
    }


    public Collection<Instance> getInstances( InstanceStateName state ) {
        return getInstances( null, state );
    }


    public Collection<Instance> getInstances( String runnerName ) {
        return getInstances( runnerName, null );
    }


    /**
     * @param runnerName Causes the method to return instances with given Name tag, give null if you want to get
     * instances with all names
     * @param state Causes the method to return instances with given state, give null if you want to get instances in
     * all states
     * @return Returns all instances that satisfy given parameters
     */
    public Collection<Instance> getInstances( String runnerName, InstanceStateName state ) {
        Collection<Instance> instances = new LinkedList<Instance>();

        DescribeInstancesRequest request = new DescribeInstancesRequest();
        if ( runnerName != null ) {
            List<String> valuesT1 = new ArrayList<String>();
            valuesT1.add( runnerName );
            Filter filter = new Filter("tag:Name", valuesT1);
            request = request.withFilters( filter );
        }
        if ( state != null ) {
            List<String> valuesT1 = new ArrayList<String>();
            valuesT1.add( state.toString() );
            Filter filter = new Filter("instance-state-name", valuesT1);
            request = request.withFilters( filter );
        }
        DescribeInstancesResult result = client.describeInstances( request );

        for ( Reservation reservation : result.getReservations() ) {
            for ( Instance in : reservation.getInstances() ) {
                instances.add( in );
            }
        }

        return instances;
    }


    public Collection<String> getInstanceIDs( String runnerName, InstanceStateName state ) {
        Collection<Instance> instances = getInstances( runnerName, state );
        Collection<String> instanceIDs = new ArrayList<String>();
        for ( Instance i : instances ) {
            instanceIDs.add( i.getInstanceId() );
        }
        return instanceIDs;
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
            for ( Instance in : reservation.getInstances() ) {
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


    public void updateSecurityGroupRecords( Collection<Integer> ports, boolean clearAllRecords ) {
        Collection<IpPermission> prevPermissions = getSecurityGroupRecords();
        if ( clearAllRecords ) {
            deleteSecurityGroupRecord( prevPermissions );
        }
        else {
            for ( int port : ports) {
                for ( IpPermission permission : prevPermissions ) {
                    if ( permission.getFromPort() == port ) {
                        deleteSecurityGroupRecord( permission );
                    }
                }
            }
        }
        Collection<Instance> instances = getInstances( runnerName, InstanceStateName.Running );
        Collection<String> ipRanges = new ArrayList<String>( instances.size() );
        for ( Instance i : instances ) {
            ipRanges.add( i.getPrivateIpAddress() + "/32" );
        }
        for ( int port : ports ) {
            addRecordToSecurityGroup( ipRanges, "tcp", port );
        }
    }


    /**
     *
     * @param ipRanges
     * @param protocol
     * @param port
     */
    public void addRecordToSecurityGroup( Collection<String> ipRanges, String protocol, int port ) {
        IpPermission ipPermission = new IpPermission();

        ipPermission.withIpRanges( ipRanges )
                    .withIpProtocol( protocol )
                    .withFromPort( port )
                    .withToPort( port );

        AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest();
        request = request.withGroupName( securityGroup ).withIpPermissions( ipPermission );
        client.authorizeSecurityGroupIngress( request );
    }


    /**
     * @param ipRanges
     * @param protocol
     * @param port
     */
    public void deleteSecurityGroupRecord( Collection<String> ipRanges, String protocol, int port ) {
        RevokeSecurityGroupIngressRequest request = new RevokeSecurityGroupIngressRequest();
        IpPermission permission = new IpPermission();
        permission = permission.withIpProtocol( protocol ).withFromPort( port ).withToPort( port ).
                withIpRanges( ipRanges );
        request = request.withGroupName( securityGroup ).withIpPermissions( permission );

        client.revokeSecurityGroupIngress( request );
    }


    /**
     * @param ipRanges
     */
    public void deleteSecurityGroupRecord(Collection<IpPermission> ipRanges) {
        RevokeSecurityGroupIngressRequest request = new RevokeSecurityGroupIngressRequest();
        request = request.withGroupName( securityGroup ).withIpPermissions( ipRanges );
        client.revokeSecurityGroupIngress( request );
    }


    /**
     * @param ipRanges
     */
    public void deleteSecurityGroupRecord(IpPermission... ipRanges) {
        RevokeSecurityGroupIngressRequest request = new RevokeSecurityGroupIngressRequest();
        request = request.withGroupName( securityGroup ).withIpPermissions( ipRanges );
        client.revokeSecurityGroupIngress( request );
    }


    /**
     *
     * @return
     */
    public Collection<IpPermission> getSecurityGroupRecords() {
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
        List<String> valuesT1 = new ArrayList<String>();
        valuesT1.add( securityGroup );
        Filter filter = new Filter("group-name", valuesT1);
        request = request.withFilters( filter );
        DescribeSecurityGroupsResult result = client.describeSecurityGroups( request );
        if ( result == null || result.getSecurityGroups().size() == 0 ) {
            return null;
        }
        return result.getSecurityGroups().iterator().next().getIpPermissions();
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

        return new AmazonEC2Client( provider );
    }

}
