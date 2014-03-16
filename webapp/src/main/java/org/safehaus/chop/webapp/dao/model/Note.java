package org.safehaus.chop.webapp.dao.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Note {

    private String id;
    private String commitId;
    private int runNumber;
    private String text;

    public Note(String commitId, int runNumber, String text) {
        this.id = createId(commitId, runNumber);
        this.commitId = commitId;
        this.runNumber = runNumber;
        this.text = text;
    }

    private static String createId(String commitId, int runNumber) {
        return "" + new HashCodeBuilder()
                .append(commitId)
                .append(runNumber)
                .toHashCode();
    }

    public String getId() {
        return id;
    }

    public String getCommitId() {
        return commitId;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("commitId", commitId)
                .append("runNumber", runNumber)
                .append("text", text)
                .toString();
    }
}
