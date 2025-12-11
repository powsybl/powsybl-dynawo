# Event models configuration
Event models configuration allows the creation of events that occurs during the simulation.

## Implementation
Powsybl-Dynawo handles two methods of configuration:
- **Event Models DSL**: a domain specific language written in groovy (used in iTools)
- **Dynawo Event Models**: a JSON configuration file. 

**Note**: For Json configuration, all models should be written in an `events` array.
```json
{
  "events":[...]
}
```
In the following examples the array will be omitted.

## Event Model
- All the models share three attributes:
- `modelName`: refers to the event model library used in Dynawo (used as a keyword in Groovy script).
- `staticId`: identifies the equipment affected by the event.
- `startTime`: defines when the event starts.

### Disconnect
Disconnects a bus, a branch, an injection or an HVDC line.
With specific attributes:
- `disconnectOnly`: optional attribute that specifies the side to disconnect for Branch or HVDC (by default both sides are disconnected)

**Groovy script:**
```groovy
import com.powsybl.iidm.network.TwoSides

Disconnect {
    staticId "LINE"
    startTime 1
    disconnectOnly TwoSides.TWO
}
```
**Json configuration:**
```json
{
  "model":"Disconnect",
  "properties":[
    {
      "name":"staticId",
      "value":"GEN",
      "type":"STRING"
    },
    {
      "name":"startTime",
      "value":"1",
      "type":"DOUBLE"
    },
    {
      "name": "disconnectOnly",
      "value": "TWO",
      "type": "TWO_SIDES"
    }
  ]
}
```

### Active Power Variation
Active power variation on generator or load.  
With specific attribute:
- `deltaP`: active power variation.

**Groovy script:**
```groovy
ActivePowerVariation {
    staticId "LOAD"
    startTime 2
    deltaP 0.2
}
```
**Json configuration:**
```json
{
  "model":"ActivePowerVariation",
  "properties":[
    {
      "name":"staticId",
      "value":"LOAD",
      "type":"STRING"
    },
    {
      "name":"startTime",
      "value":"2",
      "type":"DOUBLE"
    },
    {
      "name":"deltaP",
      "value":"0.2",
      "type":"DOUBLE"
    }
  ]
}
```

### Reactive Power Variation
Reactive power variation on load and generator without dynamic model.  
With specific attribute:
- `deltaQ`: reactive power variation.

**Groovy script:**
```groovy
ReactivePowerVariation {
    staticId "LOAD"
    startTime 2
    deltaQ 0.2
}
```
**Json configuration:**
```json
{
  "model":"ReactivePowerVariation",
  "properties":[
    {
      "name":"staticId",
      "value":"LOAD",
      "type":"STRING"
    },
    {
      "name":"startTime",
      "value":"2",
      "type":"DOUBLE"
    },
    {
      "name":"deltaQ",
      "value":"0.2",
      "type":"DOUBLE"
    }
  ]
}
```

### Reference Voltage Variation
Reference voltage variation on synchronized/synchronous generator  
With specific attribute:
- `deltaU`: reference voltage variation.

**Groovy script:**
```groovy
ReferenceVoltageVariation {
    staticId "GEN"
    startTime 2
    deltaU 0.2
}
```
**Json configuration:**
```json
{
  "model":"ReferenceVoltageVariation",
  "properties":[
    {
      "name":"staticId",
      "value":"GEN",
      "type":"STRING"
    },
    {
      "name":"startTime",
      "value":"2",
      "type":"DOUBLE"
    },
    {
      "name":"deltaU",
      "value":"0.2",
      "type":"DOUBLE"
    }
  ]
}
```

### Node Fault
Node fault with configurable resistance, reactance and duration.  
With specific attributes:
- `faultTime`: delta with `startTime` at which the event ends (must be > 0).
- `rPu`: r pu variation (must be >= 0).
- `xPu`: x pu variation (must be >= 0).

**Groovy script:**
```groovy
NodeFault {
    staticId "NGEN"
    startTime 1
    faultTime 0.1
    rPu 0
    xPu 0.01
}
```
**Json configuration:**
```json
{
  "model":"NodeFault",
  "properties":[
    {
      "name":"staticId",
      "value":"NGEN",
      "type":"STRING"
    },
    {
      "name":"startTime",
      "value":"1",
      "type":"DOUBLE"
    },
    {
      "name":"faultTime",
      "value":"0.1",
      "type":"DOUBLE"
    },
    {
      "name":"rPu",
      "value":"0",
      "type":"DOUBLE"
    },
    {
      "name":"xPu",
      "value":"0.01",
      "type":"DOUBLE"
    }
  ]
}
```

## Event model builder list
Ultimately, all groovy scripts or Json configuration file call the dedicated builders that can be used directly by developers in order to create a custom `EventModelsSupplier`:
- EventDisconnectionBuilder
- EventActivePowerVariationBuilder
- EventReactivePowerVariationBuilder
- EventReferenceVoltageVariationBuilder
- NodeFaultEventBuilder