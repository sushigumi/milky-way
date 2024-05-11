package dev.sushigumi.milkyway.kubernetes.api.model;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("sushigumi.dev")
@Version("v1alpha1")
public class TestGroup extends CustomResource<TestGroupSpec, Void> implements Namespaced {}
