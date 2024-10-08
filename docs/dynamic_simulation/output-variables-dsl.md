# Output Variables DSL
The output variables domain specific language allow a user to configure the curves or final state values Dynawo will export at the end of the simulation.
This DSL defines the `curve` and `fsv`keywords.

The `curve` keyword combined with the `variable` field create a single curve for a dynamic model. One identifies a dynamic model by its ID, the same as the one used in the [Dynamic Models DSL](dynamic-models-dsl). The variable to plot is identified by its name.
```groovy
curve {
    dynamicModelId "dynamicId"
    variable "load_PPu"
}
```

If the only information needed is the variable final value, the `curve` keyword can be replaced by `fsv` keyword:
```groovy
fsv {
    dynamicModelId "dynamicId"
    variable "load_PPu"
}
```

If you want to plot a static variable, the `dynamicModelId` parameter has to be replaced by the `staticId` keyword and refers to an ID present in the static network.
```groovy
curve {
    staticId bus.id
    variable "Upu_value"
}
```

If you want to plot several variables of the same dynamic model, you can use the `variables` field that permit limiting boilerplate code in the script.
```
// This:
curve {
    dynamicModelId load.id
    variable "load_PPu"
}
curve {
    dynamicModelId load.id
    variable "load_QPu"
}

// is equivalent to:
curve {
    dynamicModelId load.id
    variables "load_PPu", "load_QPu"
}
```
