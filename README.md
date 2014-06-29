MyConbook
=========
MyConbook is a convention scheduling and guidebook application for your smartphone written by AndrewNeo.

[MyConbook website](http://myconbook.net)

[Download from the Play Store](https://play.google.com/store/apps/details?id=net.myconbook.android)

Building
--------
To build MyConbook, run `./gradlew assembleDebug` to debug your app.

MyConbook uses the testing data server by default. To use a custom [DataTool](https://github.com/MyConbook/datatool) output, you'll need to point the `DATA_PATH` in `MyConbook/build.gradle` at a different URL. Create a `MyConbook/myconbook.gradle` file to store your custom changes or signing configs, this file will not be committed to Git.

Crashlytics support is built in, make sure `CRASHLYTICS_ENABLED` is turned off (always off on debug builds) if you are not using it in your build.

License
-------
MyConbook for Android is licensed under the Apache license. See LICENSE.txt.

Redistributions **may not** use the official MyConbook data source without permission.
