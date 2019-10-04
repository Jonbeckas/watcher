import org.json.JSONArray;
import org.json.JSONObject;
import toolbox.process.OSnotDetectedException;
import toolbox.process.OSnotsupportedException;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        new Main();
    }
    public Main() {
        if (javaMajorRelease()<8) {
            System.out.println("Your Java Version is "+javaMajorRelease()+" you need Java version 8 or above to use this programm. ❌");
            System.exit(1);
        }
        File file = new File("config.json");
        if(!file.exists() || file.isDirectory()) {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write("[]");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Config file not found!\n Created new config.json ❌");
            return;
        }
        try {
            String json = getContentFromFile(file);
            JSONArray jsonarray = new JSONArray(json);
            System.out.println("Config loadet ✔");
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
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
            System.out.println("DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean isRunning(String prn) {
        ArrayList<String> procs = null;
        try {
            procs = getRunningProcesses();
        } catch (OSnotDetectedException e) {
            System.out.println("No supported OS found!\n Program stopped! ❌");
            System.exit(1);
        }
        for (String onep: procs) {
            if (onep.contains(prn)) {
                return true;
            }
        }
        return false;
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

    private ArrayList<String> getRunningProcesses() throws OSnotDetectedException {
        ArrayList<String> processes = new ArrayList<>();
        try {
            String process;
            Process p = null;
            if (getOS() == OS_LINUX) {
                p = Runtime.getRuntime().exec("ps -few");
            } else if(getOS() ==OS_WINDOWS) {
                 p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
            } else if (getOS() == OS_MAC) {
                p = Runtime.getRuntime().exec("ps -e");
            } else {
                throw new OSnotsupportedException();
            }

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((process = input.readLine()) != null) {
                processes.add(process);
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return processes;
    }
    public static final int OS_WINDOWS = 0;
    public static final int  OS_LINUX = 1;
    public static final int OS_MAC = 2;
    private int getOS() throws OSnotDetectedException {
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            return OS_WINDOWS;

        } else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ) {
            return OS_LINUX;
        } else if (OS.contains("mac")) {
            return OS_MAC;
        } else {
            throw new OSnotDetectedException();
        }
    }
    private int javaMajorRelease() {
        String[] javaVersionElements = System.getProperty("java.runtime.version").split("\\.|_|-b");
        int major = Integer.parseInt(javaVersionElements[0]);
        if (major>1) {
            return major;
        } else {
            return  Integer.parseInt(javaVersionElements[2]);
        }
    }

}
