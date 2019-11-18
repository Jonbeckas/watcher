With watcher you can track processes and running a commandline command if there are running or not runnung.


## Install
1. Download jar from [Releases](https://github.com/Jonbeckas/watcher/releases)
1. Make sure you have installed java 8 or above
1. Add watcher.jar to planned jobs
    1. __Linux:__
       1. Open crontab with ```chrontab -e```
       1. Paste ```* * * * * nohup java -jar /PATH/TO/WATCHER/watcher.jar```
   2. __Windows__
       1. Run ```schtasks.exe -create -SC MINUTE -TR YourDrive:\PATH\TO\java.exe -jar  YourDrive:\PATH\TO\WATCHER\watcher.jar -TN Watcher```
  
## Configurate
 1. Run watcher one times manualy
 1. Watcher creates a `config.json` in the home directory or in the directory where the watcher.jar is
 1. Open config.json in a text editor 
 1. For Example:
 ```json
 [{
     "procname": "process Name like in tasklist.exe or ps -e",
     "if": "command if process run",
     "else": "command if process is not running "
  }, {"linkconf": "PATH_TO_SECOND_CONF"}
  ]
  ```
