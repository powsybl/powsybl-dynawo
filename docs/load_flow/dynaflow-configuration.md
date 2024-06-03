---
layout: default
---

## DynaFlow configuration

You need to tell powsybl where to find DynaFlow, by adding this into you configuration file:
```yaml
dynaflow:
    homeDir: /path/to/dynaflow  # Directory obtained by unzipping the package, should contain "bin"
    debug: false
```

To use DynaFlow as a default for all power flow computations, you may configure the `load-flow`
module in your configuration file:
```yaml
load-flow:
    default-impl-name: "DynaFlow"
```