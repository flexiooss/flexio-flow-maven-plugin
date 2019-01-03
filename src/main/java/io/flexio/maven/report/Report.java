package io.flexio.maven.report;

import org.apache.maven.plugin.logging.Log;

import java.util.LinkedList;
import java.util.List;

public class Report {

    public interface LogToLevel {

        LogToLevel INFO = (line, log) -> log.info(line);
        LogToLevel ERROR = (line, log) -> log.error(line);

        void logWith(String line, Log log);
    }


    private boolean failed = false;
    private final List<String> lines = new LinkedList<>();

    public Report append(String line) {
        this.lines.add(line);
        return this;
    }

    public Report hasFailed(boolean failure) {
        this.failed = failure;
        return this;
    }

    public boolean isFailure() {
        return this.failed;
    }

    public void log(Log log, LogToLevel to) {
        for (String line : this.lines) {
            to.logWith(line, log);
        }
    }

    @Override
    public String toString() {
        return "Report{" +
                "failed=" + failed +
                ", lines=" + lines +
                '}';
    }
}
