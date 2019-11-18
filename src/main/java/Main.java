import org.json.JSONArray;
import org.json.JSONObject;
import toolbox.os.OSnotSupportedException;


import java.io.*;

import static net.tetraowl.watcher.toolbox.JavaTools.javaMajorRelease;
import static toolbox.process.ProcessTools.isRunning;


public class Main {
    public static void main(String[] args) {
        new Main((args));
    }
    public Main(String[] args) {
        if (javaMajorRelease()<8) {
            System.out.println("Your Java Version is "+javaMajorRelease()+" you need Java Version 8 or above to use this programm. ❌");
            System.exit(1);
        }
        File file;
        if (args.length!=0 &&!args[0].equals("")) {
            file = new File(args[0]);
        } else {
            file = new File("config.json");
        }
        if (!checkConfig(file)) {
            if (args.length!=0&&!args[0].equals("")) {
                System.out.println("Config file not found!\n Created new config file at "+args[0]+"❌");
            }
            System.out.println("Config file not found!\n Created new config file config.json ❌");
            System.exit(1);
        }
        runCommands(file);

    }
    private boolean checkConfig(File file) {
        if(!file.exists() || file.isDirectory()) {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write("[]");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    private void runCommands(File file) {
        try {
            String json = getContentFromFile(file);
            JSONArray jsonarray = new JSONArray(json);
            System.out.println("Config loadet ✔");
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                if (jsonobject.has("linkconf") && jsonobject.getString("linkconf")!=null) {
                    File exConf = new File(jsonobject.getString("linkconf"));
                    if (!checkConfig(exConf)) {
                        System.out.println("Config file not found!\n Created new config file at "+jsonobject.getString("linkconf")+"❌");
                    } else {
                        runCommands(exConf);
                    }
                } else {
                    String proc = jsonobject.getString("procname");
                    String ifu = jsonobject.getString("if");
                    String elseu = jsonobject.getString("else");
                    boolean isRunning = isRunning(proc);
                    Runtime rt = Runtime.getRuntime();
                    if (isRunning&&!ifu.equals("")) {
                        Process pr = rt.exec(ifu);
                        System.out.println(proc+" is running\n started "+ifu+"\uD83D\uDE80");
                    } else if (!isRunning&&!elseu.equals("")){
                        System.out.println(proc+" is not running\n started "+elseu+"\uD83D\uDE80");
                        Process pr = rt.exec(elseu);
                    }
                }
            }
            System.out.println("DONE");
        } catch (OSnotSupportedException e) {
            System.out.println("No supported OS found!\n Program stopped! ❌");
            System.exit(1);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private String getContentFromFile(File file) throws FileNotFoundException,IOException {
        StringBuilder builder = new StringBuilder();
        FileInputStream inputStream = new FileInputStream(file.getPath());
        InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
        int character;

        while ((character = reader.read()) != -1) {
            builder.append((char) character);
        }
        reader.close();
        return builder.toString();
    }





}
