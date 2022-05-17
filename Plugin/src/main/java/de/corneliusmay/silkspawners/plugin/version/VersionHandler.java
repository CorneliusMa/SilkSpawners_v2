package de.corneliusmay.silkspawners.plugin.version;

import de.corneliusmay.silkspawners.api.NMS;
import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class VersionHandler {

    @Getter
    private final String version;

    @Getter
    private NMS nmsHandler;

    public VersionHandler() {
        this.version = getServerVersion();
    }

    public boolean load() {
        try {
            this.nmsHandler = getNMS(this.version);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            SilkSpawners.getInstance().getLog().error("The detected Server Version (" + this.version + ") is not supported by the currently installed version of SilkSpawners");

            SilkSpawners.getInstance().getLog().info("Currently supported Versions are: " + Arrays.toString(getSupportedVersions()));
            SilkSpawners.getInstance().getLog().info("You can check for updates at https://www.spigotmc.org/resources/silkspawners-with-api.60063/");


            SilkSpawners.getInstance().getLog().warn("Disabling plugin due to version incompatibility");
            SilkSpawners.getInstance().getPluginLoader().disablePlugin(SilkSpawners.getInstance());
            return false;
        }

        SilkSpawners.getInstance().getLog().info("Loading support for NMS-Version " + this.version);
        return true;
    }

    private String getServerVersion() {
        String packageName = SilkSpawners.getInstance().getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }


    private NMS getNMS(String version) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Class<?> clazz = Class.forName("de.corneliusmay.silkspawners.nms." + version + ".NMSHandler");
        if(NMS.class.isAssignableFrom(clazz)) {
            return (NMS) clazz.getConstructor().newInstance();
        }

        return null;
    }

    private String[] getSupportedVersions() {
        ArrayList<String> versions = new ArrayList<>();
        try {
            CodeSource src = SilkSpawners.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                while (true) {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null)
                        break;
                    String name = e.getName();
                    if (name.startsWith("de/corneliusmay/silkspawners/nms/v") && !name.contains(".class")) {
                        String[] nameSplit = name.split("/");
                        versions.add(nameSplit[nameSplit.length - 1]);
                    }
                }
            } else {
                return new String[]{"Cannot get supported versions"};
            }
        } catch (IOException ex) {
            return new String[]{"Cannot get supported versions: " + ex.getMessage()};
        }

        return versions.toArray(new String[0]);
    }
}