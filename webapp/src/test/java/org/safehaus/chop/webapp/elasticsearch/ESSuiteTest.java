package org.safehaus.chop.webapp.elasticsearch;


import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.ProviderParams;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.CommitDaoTest;
import org.safehaus.chop.webapp.dao.ModuleDao;
import org.safehaus.chop.webapp.dao.ModuleDaoTest;
import org.safehaus.chop.webapp.dao.NoteDao;
import org.safehaus.chop.webapp.dao.NoteDaoTest;
import org.safehaus.chop.webapp.dao.ProviderParamsDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.RunDaoTest;
import org.safehaus.chop.webapp.dao.RunResultDao;
import org.safehaus.chop.webapp.dao.RunResultDaoTest;
import org.safehaus.chop.webapp.dao.RunnerDaoTest;
import org.safehaus.chop.webapp.dao.UserDao;
import org.safehaus.chop.webapp.dao.model.BasicCommit;
import org.safehaus.chop.webapp.dao.model.BasicModule;
import org.safehaus.chop.webapp.dao.model.BasicProviderParams;
import org.safehaus.chop.webapp.dao.model.BasicRun;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;
import org.safehaus.chop.webapp.dao.model.Note;
import org.safehaus.chop.webapp.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith( Suite.class )
@Suite.SuiteClasses(
{
        ModuleDaoTest.class, CommitDaoTest.class, NoteDaoTest.class, RunDaoTest.class, RunnerDaoTest.class,
        RunResultDaoTest.class
} )
public class ESSuiteTest {

    private static Logger LOG = LoggerFactory.getLogger( ESSuiteTest.class );

    public static final String MODULE_ID_1 = "778087981";
    public static final String MODULE_ID_2 = "2067867";
    public static final String MODULE_GROUPID = "org.safehaus.chop";
    public static final String COMMIT_ID_1 = "cc471b502aca2791c3a068f93d15b79ff6b7b827";
    public static final String COMMIT_ID_2 = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
    public static final String COMMIT_ID_3 = "e29074efad5e0e1c7c2b63128ff9284f9b47ceb3";
    public static final String NOTE = "This is a note!";
    public static final String IMAGE_ID = "ami-213213214";
    public static final String RUNNER_ID_1 = "35231";
    public static final String RUNNER_ID_2 = "23412";
    public static final String TEST_NAME = "org.safehaus.chop.example.DigitalWatchTest";
    public static final String USER_1 = "testuser";
    public static final String USER_2 = "user-2";


    @ClassRule
    public static ElasticSearchResource esClient = new ElasticSearchResource();

    public static ModuleDao moduleDao;
    public static CommitDao commitDao;
    public static NoteDao noteDao;
    public static ProviderParamsDao ppDao;
    public static RunDao runDao;
    public static RunResultDao runResultDao;
    public static UserDao userDao;

    /** Populate elastic search for all tests */
    @BeforeClass
    public static void setUpData() throws Exception {

        LOG.warn( "Setup called!!!" );

        LOG.warn( "Is esClient null? : {}", ( esClient == null )  );

        LOG.warn( "Is esClient.getClient() null? : {}", ( esClient.getClient() == null )  );


        /** Save 2 different modules */
        moduleDao = new ModuleDao( esClient );
        Module module = new BasicModule( // ID is 778087981
                MODULE_GROUPID, // groupId
                "chop-maven-plugin", // artifactId
                "1.0-SNAPSHOT", // version
                "https://stash.safehaus.org/scm/chop/main.git", // vcsRepoUrl
                MODULE_GROUPID // testPackageBase
        );
        moduleDao.save( module );

        module = new BasicModule( // ID is -975269068
                MODULE_GROUPID, // groupId
                "chop-client", // artifactId
                "1.0-SNAPSHOT", // version
                "https://stash.safehaus.org/scm/chop/main.git", // vcsRepoUrl
                MODULE_GROUPID // testPackageBase
        );
        moduleDao.save( module );

        LOG.warn( "after module save" );

        /** Save 3 commits, 2 under same module, 1 different */
        commitDao = new CommitDao( esClient );
        Commit commit = new BasicCommit(
                COMMIT_ID_1, // commitId
                MODULE_ID_1, // moduleId
                "742e2a76a6ba161f9efb87ce58a9187e", // warMD5
                new Date() // createDate
        );
        commitDao.save( commit );

        commit = new BasicCommit(
                COMMIT_ID_2, // commitId
                MODULE_ID_2, // moduleId
                "395cfdfc3b77242a6f957d6d92da8958", // warMD5
                new Date() // createDate
        );
        commitDao.save( commit );

        commit = new BasicCommit(
                COMMIT_ID_3, // commitId
                MODULE_ID_2, // moduleId
                "b9860ffa5e39b6f7123ed8c72c4b7046", // warMD5
                new Date() // createDate
        );
        commitDao.save( commit );

        LOG.warn( "after commit save" );

        /** Save a Note */
        noteDao = new NoteDao( esClient );
        Note note = new Note( COMMIT_ID_1, 1, NOTE );
        noteDao.save( note );

        LOG.warn( "after note save" );

        /** Save 2 provider params */
        ppDao = new ProviderParamsDao( esClient );
        ProviderParams pp = new BasicProviderParams(
                USER_1,
                "m1.large",
                "es-east",
                "1230d4353459da23ec21a259a",
                "ad911213ab21ef23ab4e0e",
                IMAGE_ID,
                "chop-security",
                "Ec2KeyPair",
                "chop-runner"
        );
        ppDao.save( pp );

        pp = new BasicProviderParams(
                "testuser2",
                "t1.micro",
                "es-west",
                "1230d4353459da23ec21a259a",
                "ad911213ab21ef23ab4e0e",
                "ami-2143224",
                "chop-security-2",
                "ChopKeyPair",
                "chop-runner"
        );
        ppDao.save( pp );

        LOG.warn( "after pparams save" );


        /** Save 2 runs for one commit, 1 run for another */
        String[] runIds = new String[ 3 ];

        runDao = new RunDao( esClient );
        BasicRun run = new BasicRun(
                COMMIT_ID_2, // commitId
                RUNNER_ID_2, // runner
                1, // runNumber
                TEST_NAME // testName
        );
        runDao.save( run );
        runIds[ 0 ] = run.getId();

        run = new BasicRun(
                COMMIT_ID_2, // commitId
                RUNNER_ID_2, // runner
                2, // runNumber
                TEST_NAME // testName
        );
        runDao.save( run );
        runIds[ 1 ] = run.getId();

        run = new BasicRun(
                COMMIT_ID_3, // commitId
                RUNNER_ID_1, // runner
                1, // runNumber
                TEST_NAME // testName
        );
        runDao.save( run );
        runIds[ 2 ] = run.getId();

        LOG.warn( "after run save" );

        /** Save 3 run results, one for each run */
        runResultDao = new RunResultDao( esClient );
        BasicRunResult runResult = new BasicRunResult( runIds[ 0 ], 5, 1000, 0, 1 );
        runResultDao.save( runResult );

        runResult = new BasicRunResult( runIds[ 1 ], 5, 1200, 1, 0 );
        runResultDao.save( runResult );

        runResult = new BasicRunResult( runIds[ 2 ], 17, 15789, 2, 2 );
        runResultDao.save( runResult );

        LOG.warn( "after runresult save" );

        /** Save 2 users */
        userDao = new UserDao( esClient );
        User user = new User( USER_1 , "password" );
        userDao.save( user );

        user = new User( USER_2 , "sosecretsuchcryptowow" );
        userDao.save( user );

        LOG.warn( "after user save" );

    }

    @AfterClass
    public static void tearDownData() {
        LOG.warn( "ESSuiteTest teardown called" );
    }


}
