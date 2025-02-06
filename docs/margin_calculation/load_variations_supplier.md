# Load variations supplier
Loads variation supplier creates loads variations used for the margin calculation.

## Implementation
Powsybl-Dynawo handle provide one supplier using JSON configuration file.
All variations should be written in an `variations` array.

## Loads variation model
The variation model has two attributes:
- `loadsIds`: an array of load ids.
- `variationValue`: the variation to apply on loads.

**Json configuration:**
```json
{
  "variations": [
    {
      "loadsIds": ["LOAD", "LOAD2"],
      "variationValue": 20
    },
    {
      "loadsIds": ["LOAD3"],
      "variationValue": 10.2
    }
  ]
}
```

## Loads variation builder
Ultimately, the supplier call the dedicated builder `LoadsVariationBuilder` that can be used directly by developers.