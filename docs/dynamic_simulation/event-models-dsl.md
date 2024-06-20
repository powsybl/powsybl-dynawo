---
layout: default
---

# Event Models DSL

The Event Models DSL is a domain specific language written in groovy for the simulation of events that occurs during the simulation.

## Event Model
- All the models supported are `BlackBoxModel`. This kind of event model have three attributes:
- `lib` refers to the event model library used in Dynawo.
- `staticId` identifies the equipment affected by the event
- `startTime` defines when the event starts.

## Disconnect
Use this event to disconnect a branch, injection or an HVDC line.

**Example**
```groovy
Disconnect {
    staticId "GEN"
    startTime 1
}
```

### TODO add remaining events
