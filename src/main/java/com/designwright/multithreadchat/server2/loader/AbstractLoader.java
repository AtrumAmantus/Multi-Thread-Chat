package com.designwright.multithreadchat.server2.loader;

import lombok.Getter;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class AbstractLoader {

    @Getter
    private final String domainLocation;

    public AbstractLoader() {
        try {
            ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URI location = codeSource != null ? codeSource.getLocation().toURI() : null;
            domainLocation = location != null ? location.getSchemeSpecificPart() : null;
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to determine source path", e);
        }
    }

    protected File loadClassDirectory() {
        if (domainLocation == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        } else {
            File root = new File(domainLocation);
            if (!root.exists()) {
                throw new IllegalStateException("Unable to determine code source archive from " + root);
            } else {
                return root;
            }
        }
    }

    protected List<File> directoryScan(File dir) {
        if (dir != null) {
            List<File> files = new ArrayList<>();
            File[] dirFiles = dir.listFiles();

            for (File file : dirFiles) {
                if (file.exists()) {
                    if (file.isDirectory()) {
                        files.addAll(directoryScan(file));
                    } else {
                        files.add(file);
                    }
                }
            }

            return files;
        }

        throw new NullPointerException("Argument can not be null");
    }

}
