package com.jesua.registration.builder;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class FileBuilder {

    public byte[] getBytesFromFile(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        return FileUtils.readFileToByteArray(new File(classLoader.getResource(fileName).getFile()));
    }
}
