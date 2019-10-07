package org.zowe.zowecatalog.util;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zowe.zowecatalog.github.Content;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
@Slf4j
public class FileUtil {

    private static final String FORMAT_CATALOG_FILE = "%s/src/main/resources/statics/catalogs/zowe-catalog-#%s-version.json";

    public static List<Content> readAllContent(String resourceFolder) {
        List<Content> contents = new ArrayList<>();
        File catalogFolder = getFileFromResources(resourceFolder);

        for (File file: catalogFolder.listFiles()) {
            try {
                Content content = read(file);
                contents.add(content);
            } catch (Exception e) {
                log.error("Exception when %s file is reading", file.getAbsolutePath());
                e.printStackTrace();
            }
        }

        return contents;
    }

    public static Content read(File file) {
        try(InputStream inputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {

            Content content = (Content) objectInputStream.readObject();
            log.info("Object has been deserialized successfully {}", content);

            return content;
        } catch (Exception e) {
            log.error("Serialize error {}", e);
            throw new RuntimeException("Deserialized exception");//Should be replaced custom runtime exception
        }
    }


    // get file from classpath, resources folder
    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = FileUtil.class.getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
    }

}
