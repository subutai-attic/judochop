package org.apache.usergrid.chop.stack;


import java.util.Collection;

import org.apache.usergrid.chop.api.Commit;
import org.apache.usergrid.chop.api.Module;
import org.apache.usergrid.chop.api.Runner;


public interface ICoordinatedStack extends Stack {


    Commit getCommit();


    Module getModule();


    User getUser();


    StackState getState();


    Collection<Runner> getRunners();


    Collection<Instance> getRunnerInstances();
}
