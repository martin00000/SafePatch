package com.aland.agent.utils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUtils
 * <p>
 * providing utility methods for file operations.
 * <p>
 *
 * @author aland
 * @version 1.0
 * @since 2023/12/29
 */
public class FileUtils {

    public static URL[] listURL(File file){
        List<URL> lst = new ArrayList<>();
        if(file.isDirectory()) {
            File[] filesBoot = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".jar");
                }
            });

            try{
                for (File dirFile: filesBoot) {
                    lst.add(dirFile.toURI().toURL());
                }
            }catch (Exception ignored){

            }

        }
        return lst.toArray(new URL[lst.size()]);
    }

    public static List<File> listFile(File file){
        List<File> lst = new ArrayList<>();
        if(file.isDirectory()) {
            File[] filesBoot = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.getName().endsWith(".jar")) {
                        return true;
                    }
                    return false;
                }
            });

            try{
                for (File dirFile: filesBoot) {
                    lst.add(dirFile);
                }
            }catch (Exception ignored){

            }

        }
        return lst;
    }


    public static void writeByteArrayToFile(final File file, final byte[] data) throws IOException {
        writeByteArrayToFile(file, data, false);
    }

    public static void writeByteArrayToFile(final File file, final byte[] data, final boolean append)
            throws IOException {
        writeByteArrayToFile(file, data, 0, data.length, append);
    }

    public static void writeByteArrayToFile(final File file, final byte[] data, final int off, final int len,
                                            final boolean append) throws IOException {
        try (OutputStream out = openOutputStream(file, append)) {
            out.write(data, off, len);
        }
    }

    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            final File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }
}
