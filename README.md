testng-webdriver
===============

Library of resources for writing tests using Selenium WebDriver in TestNG.

Features
---------------

testng-webdriver currently has the following features.

* Provides a WebDriver to a TestNG test class
* Option to build a Driver before a class or before a method and destroy it afterwards automatically
* Driver is configured using TypeSafe config library
* Configuration can be overridden at the class level by providing a configuration file under src/test/resources/{package}/{class}.conf
* Configuration can be overridden using namespacing under a profile name, which is triggered using the webtest.profile system property.
* WebDrivers provided is thread local and thread safe, permitting parameterized and parallelized testing.
* Browsers provided can be local or remote, through sauce labs.

Usage
---------------

To use testng-webdriver, you should add the dependency and repository to your project, add a
config file, add a WebDriverFor____.class as the parent of your test class, and add SauceTestListener.class as your test listener.

To add the dependency to your project, use the following in your pom.xml:

    <project>
    ...
        <repositories>
            <repository>
                <id>cloudbees</id>
                <url>https://repository-dynacrongroup.forge.cloudbees.com/release/</url>
            </repository>
        </repositories>
        <dependencies>
            <dependency>
                <groupId>com.dynacrongroup</groupId>
                <artifactId>testng-webdriver</artifactId>
                <version>0.0.1</version>
            </dependency>
        </dependencies>
    ...
    </project>

The configuration file describes what browser you use by default and any profiles for additional browsers you wish to
test.  This file should be stored in your project under src/test/resources/application.conf.  For example:

    webdriver {
        browser:    htmlunit
        type:       local
    }

    //This profile would be triggered by setting the system property 'webtest.profile' to 'local-firefox'
    //For example: mvn verify -Dwebtest.profile=local-firefox
    local-firefox {
        webdriver {
            browser:    firefox
            type:       local
        }
    }

    //Remote browsers are run in sauce labs.  You'll need to have a couple of environment variables set for your credentials.
    //SAUCELABS_USER - your Sauce Labs user name
    //SAUCELABS_KEY - your Sauce Labs key
    remote-chrome {
        webdriver {
            browser:    chrome
            type:       remote
            version:    ""
            platform:   linux
        }
    }

To use the driver described in your configuration file, add it to your test class, like so:

    @Test
    public class TestWebDriverTestClass extends WebDriverForClass

The driver can then be used in your test class as a WebDriver, without any additional code to initialize or destroy it.
If you also wish to use Sauce Labs, there is a separate listener that adds logging and final status reporting in
Sauce Labs, which should be initialized with ```@listeners``` annotation.

    @Listeners({SauceTestListener.class})
    public class TestWebDriverTestClass extends WebDriverForClass