package com.bank.bdd.runner;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.bank.bdd"
        //value = "com.bank.bdd.hooks, com.bank.bdd.steps, com.bank.bdd.config"  // Use full package paths
)
public class CucumberTest {
}