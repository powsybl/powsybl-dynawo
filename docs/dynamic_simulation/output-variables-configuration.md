# Output variable configuration
The output variable domain specific language allows a user to configure the curves or final state values Dynawo will export at the end of the simulation.
This DSL defines the `curve` and `fsv`keywords.

The `curve` keyword combined with the `variable` field create a single curve for a dynamic model. The dynamic model is identified by its ID, the same as the one used in the [Dynamic Models DSL](dynamic-models-configuration). The variable to plot is identified by its name.
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

If you want to plot several variables of the same model, you can use the `variables` field that limits boilerplate code in the script.
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

## Output variables builder
Ultimately, all groovy scripts call the dedicated builder `DynawoOutputVariablesBuilder` that can be used directly by developers.