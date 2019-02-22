package io.flexio.maven;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public interface WithCoordinates {
    static List<WithCoordinates> fromDependencies(List<Dependency> dependencies) {
        return dependencies.stream().map(dependency -> from(dependency)).collect(Collectors.toList());
    }

    static List<WithCoordinates> fromPlugins(List<Plugin> plugins) {
        return plugins.stream().map(plugin -> from(plugin)).collect(Collectors.toList());
    }

    String groupId();
    String artifactId();
    String version();

    default boolean isSnapshot() {
        return this.version().endsWith("-SNAPSHOT");
    }



    static WithCoordinates from(Dependency dependency) {
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

    static WithCoordinates from(Plugin plugin) {
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
}
