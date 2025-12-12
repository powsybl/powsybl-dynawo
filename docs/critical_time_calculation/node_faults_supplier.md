# Node Faults supplier
Node faults supplier creates the [node faults](../dynamic_simulation/event-models-configuration.md#node-fault) used in the critical time calculation.

## Implementation
Powsybl-Dynawo provides one supplier based on a JSON configuration file.
All node faults should be written in a `nodeFaults` array. Each node fault will create a separate scenario.

## Node Fault model
The node fault model has three attributes:
- `elementId`: the node fault will be applied on this element.
- `fault_rPu`: r pu variation (must be >= 0). The default value is `0.001`.
- `fault_xPu`: x pu variation (must be >= 0). The default value is `0.001`.


**Json configuration:**
```json
{
  "nodeFaults" : [
    {
      "elementId" : "GEN",
      "fault_rPu" : 0.001,
      "fault_xPu" : 0.001
    },
    {
      "elementId" : "GEN2"
    }
  ]
}
```

## Node Faults builder
Ultimately, the supplier calls the dedicated builder `NodeFaultsBuilder` that can be used directly by developers.
