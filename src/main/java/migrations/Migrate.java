package migrations;

import java.io.File;

import java.net.URI;
import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

class Migrate {

    private static final String GEM_PATH = "jruby/1.8/gems";

    public void migrate(String version) throws Exception {
        String migrations = getMigrationsPath();
        List<String> gemPaths = getGemPaths();

        ScriptingContainer container = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
        container.setLoadPaths(gemPaths);

        String rubyVersion = "nil";
        if (version != null && version.length() > 0)
            rubyVersion = version;

        String script =
          "require 'rubygems'\n" +
          "require 'active_record'\n" +
          "ActiveRecord::Base.establish_connection(" + getDbConnection() + ")\n" +
          "ActiveRecord::Migrator.migrate('" + migrations + "', " + rubyVersion + ")";

        container.runScriptlet(script);
    }

    public String getMigrationsPath() {
        return getClass().getClassLoader().getResource("db/migrate").toString().replace("jar:", "");
    }

    private String getDbConnection() {
        StringBuilder builder = new StringBuilder("{");

        String adapter = System.getProperty("migrations.db.adapter", "jdbcmysql");
        builder.append(":adapter => '").append(adapter).append("', ");

        String jndi = System.getProperty("migrations.db.jndi");

        if (jndi != null && jndi.length() > 0) {
            builder.append(":jndi => '").append(jndi).append("'");
        } else {
            String host = System.getProperty("migrations.db.host", "localhost");
            builder.append(":host => '").append(host).append("', ");

            String database = System.getProperty("migrations.db.name");
            builder.append(":database => '").append(database).append("', ");

            String user = System.getProperty("migrations.db.user", "root");
            builder.append(":username => '").append(user).append("', ");

            String password = System.getProperty("migrations.db.password", "");
            builder.append(":password => '").append(password).append("'");
        }

        return builder.append("}").toString();
    }

    public List<String> getGemPaths() throws Exception {
        List<String> gemPaths = new ArrayList<String>();
        String resource = getClass().getClassLoader().getResource(GEM_PATH).toString().replace("jar:", "");

        String jarName = resource.substring(5, resource.indexOf("!")); // remove "file:"

        JarFile jar = new JarFile(jarName);
        for(Enumeration<JarEntry> eje = jar.entries(); eje.hasMoreElements(); ) {
            String entryName = eje.nextElement().getName();
            if (entryName.startsWith(GEM_PATH) && entryName.matches(GEM_PATH + "/.+/lib/")) {
                gemPaths.add("file:" + jarName + "!" + entryName);
            }
        }
        return gemPaths;
    }

    public static void main(String... args) {
        try{
            String version = null;
            if (args.length > 0)
                version = args[0];

            new Migrate().migrate(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
