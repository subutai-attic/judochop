package org.safehaus.chop.webapp.dao.model;

import org.safehaus.chop.api.*;

import java.util.Iterator;

public class BasicRun implements Run {

    private Version version;
    private Summary summary;

    public BasicRun(Version version, Summary summary) {
        this.version = version;
        this.summary = summary;
    }

    @Override
    public Version getCommitVersion() {
        return version;
    }

    @Override
    public Summary getSummary() {
        return summary;
    }

    @Override
    public Iterator<TestResult> getResults() {
        return null;
    }
}

