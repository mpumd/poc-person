package com.mpumd.poc.person.application;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com.mpumd.poc.person.application/feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.mpumd.poc.person.application.feature")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @disabled")
public class CucumberTest {
}
