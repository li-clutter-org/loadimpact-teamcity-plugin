[ ![Download](https://api.bintray.com/packages/loadimpact/loadimpact-sdk-java/loadimpact-sdk-java/images/download.svg) ](https://bintray.com/loadimpact//loadimpact-teamcity-plugin/_latestVersion)
Load Impact TeamCity Plugin 
==========================

A plugin for the [TeamCity CI Server](https://www.jetbrains.com/teamcity/), that run load-tests 
hosted on [Load Impact](https://loadimpact.com/), as a build step. 

Where to get it?
----

* At the [Load Impact BinTray repo](https://bintray.com/loadimpact//loadimpact-teamcity-plugin/_latestVersion)
* At the [Load Impact Developers page](http://developers.loadimpact.com/continuous-delivery/index.html#li-docs-cd-teamcity)
* At the [TeamCity plugins list](https://confluence.jetbrains.com/display/TW/TeamCity+Plugins)
* By cloning this [GitHub repo](https://github.com/loadimpact/loadimpact-teamcity-plugin) and building it using the instructions below


Building the plugin
====
You can clone this GitHub repo and build the plugin yourself using the instructions below.

Prerequisites
----
* Java JDK, version 6+
* Apache Maven, version 3+

Build
----
Run the following command (in the root directory of the cloned sources) to build the plugin ZIP

    cd path/to/loadimpact-teamcity-plugin
    mvn clean package

You can then find the plugin ZIP as the file `./target/LoadImpact-TeamCity-plugin.zip`

Install
----
Move the plugin ZIP into the `/plugins/` sub-directory the *Data Directory* of TeamCity. The default location 
of this directory is `$HOME/.BuildServer`. You can read more about this directory and how to configure it in the 
[TeamCity Documentation](https://confluence.jetbrains.com/display/TCD9/TeamCity+Data+Directory).

If you are updating TeamCity with a newer version of this plugin, it is recommended that you first delete the 
previous ZIP file `$TEAMCITY_DATA_PATH/plugins/LoadImpact-TeamCity-plugin.zip` and its unpacked 
directory `$TEAMCITY_DATA_PATH/plugins/.unpacked/LoadImpact-TeamCity-plugin/`.

Finally, restart the TeamCity server and agent(s). You can read more about this topic in the 
[TeamCity Documentation](https://confluence.jetbrains.com/display/TCD9/Installing+and+Configuring+the+TeamCity+Server#InstallingandConfiguringtheTeamCityServer-StartingTeamCityserver)

Run
----
### MS Windows
If you are running TeamCity on MS Windows, one possible way of running it is

    cd path/to/teamcity-install-dir
    bin\teamcity-server.bat start
    buildAgent\bin\agent.bat start
    # . . .
    buildAgent\bin\agent.bat stop
    bin\teamcity-server.bat stop

*N.B.*, sometimes the agent fail to stop. Just repeat the stop command several times. As a last resort, run

    buildAgent\bin\agent.bat stop force

### Unix
If you running TeamCity on Unix/Linux, one possible way of running it is in two terminal windows

Server terminal

    cd path/to/teamcity-install-dir
    ./bin/teamcity-server.sh start
    # . . .
    ./bin/teamcity-server.sh stop

Agent terminal

    cd path/to/teamcity-install-dir
    ./buildAgent/bin/agent.sh start
    # . . .
    ./buildAgent/bin/agent.sh stop

*N.B.*, sometimes the agent fail to stop. Just repeat the stop command several times. As a last resort, run

    buildAgent\bin\agent.bat stop kill

Configure
----
Navigate to the *Administration* pages of TeamCity and verify you can find the plugin listed in the plugins-list
under *External plugins*.

Register you *Load Impact API token*, at the `Load Impact` page under the side-bar `Integration` header. 

Create a new project under the side-bar `Project-related Settings` header. Then create a *build configuration* with 
a *build step* where *Runner type* is `Load Impact`. 

Choose a *Test Configuration* from the dynamically populated drop-list and adjust other settings as well. Then launch a build/load-test. 

*N.B.*, after server startup, it typically take 1-2 minutes before an agent is connected and available.

More info
====
* How to configure this plugin, see [Load Impact TeamCity Plugin](http://developers.loadimpact.com/continuous-delivery/index.html#li-docs-cd-teamcity)
* Overview of all developer support, see [Load Impact Developers](http://developers.loadimpact.com/)
* How to write load-test scripts, see [Load Script API Documentation](https://loadimpact.com/load-script-api)
* How to guides, see [Load Impact Knowledge Base](http://support.loadimpact.com/)

