TeamCity.Meta
=============

This project provides easy-to-use access to TeamCity 
public meta-runners [repository](https://github.com/JetBrains/meta-runner-power-pack)

You can now install meta runners with one click from 
Administration | \<Project\> | Meta Runners page

The project uses [Kotlin](http://www.kotlilang.org)

License
-------
This plugin is provided under Apache 2.0 license. See LICENSE.txt for details

P.S. This is my (Eugene Petrenko) private home project
[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=AJRXZ9X6ZKXPJ)


Builds
------
Public builds are on [TeamCity](https://teamcity.jetbrains.com/viewType.html?buildTypeId=TeamCityThirdPartyPlugins_TeamCityMeta)

Installation and Configuration
------------------------------
Download the [latest build of the plugin](http://teamcity.jetbrains.com/guestAuth/repository/download/TeamCityThirdPartyPlugins_TeamCityMeta/lastest.lastSuccessful/teamcity.meta.zip), which is configured for continuous integration on TeamCity [here](http://teamcity.jetbrains.com/viewType.html?buildTypeId=TeamCityThirdPartyPlugins_TeamCityMeta&tab=buildTypeStatusDiv).

**NOTE** Ensure that your download of the `.zip` file is valid - you may be redirected to the login page when using `curl` or `wget`.

Next, put the downloaded `.zip` file into the `<TeamCity Data Directory>/plugins` folder and restart the TeamCity server. You can also upload the .zip directly by clicking "Upload plugin zip" in the Plugins List section of the Administration settings in TeamCity's web interface.

After restarting the server, the plugin should show up as an external plugin in the Plugins List section of the Administration settings.




