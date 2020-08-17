package com.designwright.core.loader;

import com.designwright.core.container.ApplicationStart;
import com.designwright.core.server.exception.InternalServiceException;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class ClassResource {

    private final String path;
    private final String domainLocation;
    private String name;
    private String simpleName;
    private String canonicalName;
    private boolean entryPoint;
    private Pattern entryPointPattern;

    public ClassResource(String path, String domainLocation) {
        this.path = path;
        this.domainLocation = domainLocation;
    }

    public void parseData() {
        String[] bits = path.split("/");
        this.name = bits[bits.length - 1];
        bits = this.name.split("\\.");
        this.simpleName = bits[0];
        String localPath = path.replace(domainLocation, "").replaceAll("(\\$\\d*)?\\.class$", "");
        this.canonicalName = pathToCanonical(localPath);
        applicationStartCheck(path);
    }

    String pathToCanonical(String path) {
        return Stream.of(path.split("/")).filter(s -> !s.isEmpty()).collect(Collectors.joining("."));
    }

    void applicationStartCheck(String path) {
        try {
            URLConnection connection = new URL("file:" + path).openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (input.available() > 0) {
                int length = input.read(buffer);
                out.write(buffer, 0, length);
            }
            String fileData = out.toString(StandardCharsets.UTF_8.name());
            entryPoint = hasEntryPointAnnotation(fileData);
        } catch (IOException e) {
            throw new InternalServiceException("Could not load class data", e);
        }
    }

    boolean hasEntryPointAnnotation(String data) {
        if (entryPointPattern == null) {
            String annotationPath = canonicalToPath(ApplicationStart.class.getCanonicalName());
            entryPointPattern = Pattern.compile("RuntimeVisibleAnnotations.{4}" + annotationPath);
        }

        return entryPointPattern.matcher(data).find();
    }

    String canonicalToPath(String canonical) {
        return canonical.replace(".", "/");
    }

    @Override
    public String toString() {
        return "ClassResource{" +
                "'" + canonicalName + '\'' +
                '}';
    }
}
