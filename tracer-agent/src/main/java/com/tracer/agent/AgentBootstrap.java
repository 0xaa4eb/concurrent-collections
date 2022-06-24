package com.tracer.agent;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.jar.JarFile;

import com.tracer.agent.util.StreamDrainer;
import com.tracer.agent.util.Version;

public class AgentBootstrap {

    private static final String CORE_MODULE_NAME = "tracer-agent-core";
    private static final String AGENT_CLASSES_JAR_INTERNAL_RESOURCE_NAME = CORE_MODULE_NAME + ".jarr";
    private static final String TRACER_TMP_DIR_PROPERTY = "tracer.tmp-dir";
    private static final Class<?> thisClass = AgentBootstrap.class;

    public static void premain(String args, Instrumentation instrumentation) {
        try {

            instrumentation.appendToBootstrapClassLoaderSearch(
                new JarFile(copyJarToTmp())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Agent.start(args, instrumentation);
    }

    private static File copyJarToTmp() {

        File tmpJarFile;
        try {
            String tmpDir = System.getProperty(TRACER_TMP_DIR_PROPERTY);
            String fileName = CORE_MODULE_NAME + "-" + Version.get() + "-" + Version.getBuildTimeEpochMilli() + ".jar";
            if (tmpDir != null) {
                tmpJarFile = Paths.get(tmpDir, fileName).toFile();
            } else {
                tmpJarFile = Paths.get(System.getProperty("java.io.tmpdir"), fileName).toFile();
            }

            if (tmpJarFile.exists() && tmpJarFile.length() == 0L) {
                if (!tmpJarFile.delete()) {
                    if (tmpDir != null) {
                        tmpJarFile = Files.createTempFile(Paths.get(tmpDir), CORE_MODULE_NAME, ".jar").toFile();
                    } else {
                        tmpJarFile = Files.createTempFile(CORE_MODULE_NAME, ".jar").toFile();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error ocurred while bootstrapping agent", e);
        }

        if (tmpJarFile.exists() && tmpJarFile.length() > 0) {
            return tmpJarFile;
        }

        try (
            InputStream inputStream = thisClass.getClassLoader().getResourceAsStream(AGENT_CLASSES_JAR_INTERNAL_RESOURCE_NAME);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tmpJarFile, false))
        ) {
            outputStream.write(new StreamDrainer().drain(inputStream));
            File classesJar = tmpJarFile;
            System.out.println("Unpacking tracer-agent-core jar file to " + classesJar);
            classesJar.deleteOnExit();
            return classesJar;
        } catch (IOException e) {
            throw new RuntimeException("Could not copy classpath resource " + AGENT_CLASSES_JAR_INTERNAL_RESOURCE_NAME, e);
        }
    }
}

