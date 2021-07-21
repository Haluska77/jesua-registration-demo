package com.jesua.registration.builder;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FileBuilder {

    public byte[] getBytesFromFile(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(fileName);
//        return IOUtils.toByteArray(resourceAsStream);

        return FileUtils.readFileToByteArray(new File(classLoader.getResource(fileName).getFile()));
    }
}
