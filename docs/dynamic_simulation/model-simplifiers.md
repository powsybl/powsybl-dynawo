# Model simplifiers
Model simplifiers allow user to remove or substitute dynamic models based on various rules.
Simplifiers can be set in [Dynawo simulation parameters](configuration.md#optional-parameters).

## Energized simplifier
Filter energized equipment models, namely :
* Equipment terminals are all connected (except dangling sides)
* Each terminal buses have a voltage level on

Filter overload management systems :
* monitored branch is energized
* measuring point is energized

## Main connected component simplifier
Filter equipment models in the main connected component.
