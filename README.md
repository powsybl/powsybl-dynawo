# powsybl-dynawo
Dynawo integration in powsybl

# run dynawo from main
It's mandatory to include a config package in the classpath.
The expected list of arguments are:
 - a network file (mandatory argument)
 - a JSON parameters file (optional argument)

```
$> java -cp "./target/powsybl-dynawo-1.0.0-SNAPSHOT.jar;$USERPROFILE/.m2/repository/com/powsybl/powsybl-config-classic/3.3.0/powsybl-config-classic-3.3.0.jar" -Dpowsybl.config.dirs="./config" com.powsybl.dynawo.Main ./ieee57cdf.xiidm  
```
