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
* WebDrivers provided is thread local and thread safe, permitting parametrized and parallelled testing.
* Browsers provided can be local or remote, through sauce labs.

Usage
---------------

1. Add the dependency and repository to your project
    - To do this, use the following in your pom.xml:
    
        ```
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
        ```

2. Add a configuration file to your resources directory
    - The configuration file describes what browser you use by default and any profiles for additional browsers you wish to
test.  This file should be stored in your project under src/test/resources/application.conf.  For example:

        ```
        //This is the default profile and will run if no parameters are passed
        webdriver {
            browser:    firefox
            type:       local
        }

        //This profile would be triggered by setting the system property 'webtest.profile' to 'local-chrome'
        //For example: mvn verify -Dwebtest.profile=local-chrome
        local-chrome {
            webdriver {
                browser:    chrome
                type:       local
            }
        }

        //Remote browsers are run in sauce labs.  You'll need to have a couple of environment variables set for your credentials.
        //SAUCELABS_USER - your Sauce Labs user name
        //SAUCELABS_KEY - your Sauce Labs key
        remote-iexplore {
            webdriver {
                browser:    iexplore
                type:       remote
                version:    "8"
                platform:   linux
            }
        }
        ```

3. Add a @ClassDriver or @MethodDriver to a public WebDriver variable in your test class
    - A MethodDriver will be initialized before and cleaned up after each test method:

        ```
        @MethodDriver
        public WebDriver driver;
        ```
    - A ClassDriver will be initialized one time before the class and cleaned up after:

        ```
        @ClassDriver
        public WebDriver driver;
        ```

4. Add SeleniumWebDriver.class as a test listener

        @Listeners({SeleniumWebDriver.class})
        public class TestWebDriverTestClass


5. Example test class:


        @Test
        @Listeners({SeleniumWebDriver.class})
        public class TestMethodDriverAnnotation {

            @MethodDriver
            public WebDriver methodDriver;

            public String search = "https://www.google.com/";

            public String maps = "https://maps.google.com/";

            public String news = "https://news.google.com/";

            public void testMethod1() {
                methodDriver.get(search);
                Assert.assertTrue(methodDriver.getCurrentUrl().equals(search));
            }

            public void testMethod2() {
                methodDriver.get(maps);
                Assert.assertTrue(methodDriver.getCurrentUrl().equals(maps));
            }

            public void testMethod3() {
                methodDriver.get(news);
                Assert.assertTrue(methodDriver.getCurrentUrl().equals(news));
            }
        }
