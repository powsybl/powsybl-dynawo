---
layout: default
---

# Curves DSL
The curves domain specific language allow a user to configure the curves Dynawo will export at the end of the simulation. This DSL defines the `curve` and the `curves` keywords.

The `curve` keyword create a single curve for a dynamic model. One identifies a dynamic model by its ID, the same as the one used in the [Dynamic Models DSL](dynamic-models-dsl). The variable to plot is identified by its name.
```groovy
curve {
    dynamicModelId load.id
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

If you want to plot several variables of the same dynamic model, you can use the `curves` keyword that permit limiting boilerplate code in the script.
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
curves {
    dynamicModelId load.id
    variables "load_PPu", "load_QPu"
}
```
