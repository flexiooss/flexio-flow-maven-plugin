package io.flexio.maven;

import io.flexio.maven.report.Report;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class WithCoordinates {
    static public List<WithCoordinates> fromDependencies(List<Dependency> dependencies) {
        return dependencies.stream().map(dependency -> from(dependency)).collect(Collectors.toList());
    }

    static public List<WithCoordinates> fromPlugins(List<Plugin> plugins) {
        return plugins.stream().map(plugin -> from(plugin)).collect(Collectors.toList());
    }

    static public List<WithCoordinates> fromArtifacts(List<Artifact> artifacts) {
        return artifacts.stream().map(plugin -> from(plugin)).collect(Collectors.toList());
    }

    public abstract String groupId();
    public abstract String artifactId();
    public abstract String version();

    public boolean isSnapshot() {
        return this.version() != null && this.version().endsWith("-SNAPSHOT");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! (o instanceof WithCoordinates)) return false;
        WithCoordinates other = (WithCoordinates) o;
        return Objects.equals(this.groupId(), other.groupId()) &&
                Objects.equals(this.artifactId(), other.artifactId()) &&
                Objects.equals(this.version(), other.version());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.groupId(), this.artifactId(), this.version());
    }

    @Override
    public String toString() {
        return "WithCoordinates{" + this.groupId() + ":" + this.artifactId() + ":" + this.version() +"}";
    }

    static public WithCoordinates from(Dependency dependency) {
        return new WithCoordinates() {
            @Override
            public String groupId() {
                return dependency.getGroupId();
            }

            @Override
            public String artifactId() {
                return dependency.getArtifactId();
            }

            @Override
            public String version() {
                return dependency.getVersion();
            }

        };
    }

    static public WithCoordinates from(Plugin plugin) {
        return new WithCoordinates() {
            @Override
            public String groupId() {
                return plugin.getGroupId();
            }

            @Override
            public String artifactId() {
                return plugin.getArtifactId();
            }

            @Override
            public String version() {
                return plugin.getVersion();
            }
        };
    }

    static public WithCoordinates from(Artifact artifact) {
        return new WithCoordinates() {
            @Override
            public String groupId() {
                return artifact.getGroupId();
            }

            @Override
            public String artifactId() {
                return artifact.getArtifactId();
            }

            @Override
            public String version() {
                return artifact.getVersion();
            }
        };
    }

    static public WithCoordinates from(String spec) {
        String [] coords = spec.split(":");

        return new WithCoordinates() {
            @Override
            public String groupId() {
                return coords[0];
            }

            @Override
            public String artifactId() {
                return coords[1];
            }

            @Override
            public String version() {
                return coords[2];
            }
        };
    }

    static public Set<WithCoordinates> loadFromReport(File reportTo) throws IOException {
        HashSet<WithCoordinates> result = new HashSet<>();
        if(reportTo != null && reportTo.exists()) {
            try(BufferedReader reader = new BufferedReader(new FileReader(reportTo))) {
                for(String line = reader.readLine() ; line != null ; line = reader.readLine()) {
                    result.add(WithCoordinates.from(line));
                }
            }
        }
        return result;
    }

    public static boolean isComplete(WithCoordinates withCoordinates) {
        return withCoordinates.groupId() != null && withCoordinates.artifactId() != null && withCoordinates.version() != null;
    }
}
