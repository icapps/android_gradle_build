### Android Gradle Build Script ###

**How To Use**

[ ![Download](https://api.bintray.com/packages/icapps/maven/icapps-build-gradle-plugin/images/download.svg) ](https://bintray.com/icapps/maven/icapps-build-gradle-plugin/_latestVersion)

Import in root gradle

    buildscript {
        repositories {
            ...
        	maven { url "https://dl.bintray.com/icapps/maven" }
            maven { url "https://dl.bintray.com/nicolaverbeeck/maven" }
            ...
        }
        dependencies {
            ...
            classpath "com.icapps.build.gradle:plugin:$icapps_build_gradle_plugin"
            ...
        }
    }

Apply the plugin by adding next command at the top of your app/build.gradle file

    apply plugin: 'icapps-build-gradle-plugin'


**DOES NOT WORK WITH ANDROID INSTANT RUN**


Add the `iCappsBuildConfig` at the bottom of you app/build.gradle file
<br/>
<tab/><tab/>- detekt will be enabled
<br/>
<tab/><tab/>- pr will be enabled with the correct `lineVariant` and `unitTestVariant`
<br/>
<tab/><tab/>- translations will be enabled with the given `apiKey`
<br/>
<tab/><tab/>- playStore will be enabled with the given `serviceAccountEmail` and `pk12File`


    iCappsBuildConfig {
        detekt {
        }

        pr {
            lintVariant = "your-variant-for-lint-check"
            unitTestVariant = "your-variant-for-unit-testing"
        }

        translations {
            apiKey = "your-icapps-translations-api-key"
        }

        appcenterAndroid {
            apiKey = 'your-api-key'
            appOwner = 'your-organization-name'
            applicationIdToAppName = { applicationId ->
                return 'your-appcenter-app-name'
            }
        }

        playStore {
            serviceAccountEmail = 'your-play-store-account-email'
            pk12File = file("${projectDir}/signing/key.p12")
        }

        bitbucket {
            user = "repoOwner"
            token = System.getenv('BITBUCKET_APP_KEY')
            tokenUser = System.getenv('BITBUCKET_USER')
        }
    }

**Configure Detekt**

_Default Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `detekt` for your project with the default detekt configuration
<br/>
No **required** params

    detekt {
    }

_Recommended Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `detekt` for your project with the default detekt configurations and will override all the given params

    detekt {
        input = files("src/main/java")                                  //Sources folder
        filters = ".*/resources/.*,.*/build/.*"                         //Files that match filter will not be tested
        config = files("$projectDir/../codecheck/detekt-config.yml")    //Detekt config that will be used
        baseline = file("$projectDir/../codecheck/detekt-baseline.xml") //Baseline for code or files that should not be checked.
        failFast = true                                                 //failfast when only 1 error occurs
    }

_More Info_

https://github.com/arturbosch/detekt

**Configure Pr**

_Default Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `pr` for your project with the default pr configuration
<br/>
No **required** params

    pr {
    }

_Recommended Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `pr` for your project with the default pr configurations and will override all the given params
<br>
By adding the correct `lintVariant`, `unitTestVariant` ,`deviceTestVariant` you will be sure the correct variant is used in every check/test. This will be **required** from the moment you use product flavors, flavor dimensions

    pr {
        lintVariant = "your-variant-for-lint-check"         //This build variant will be used for the lint check in your pr build. Default: ("release" if exists, otherwise the first debuggable build type)
        unitTestVariant = "your-variant-for-unit-testing"   //This build variant will be used for the unit testing in your pr build. Default: ("release" if exists, otherwise the first debuggable build type)
    }

_Full Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `pr` for your project with all the given params
<br>
By adding the correct `lintVariant`, `unitTestVariant` ,`deviceTestVariant` you will be sure the correct variant is used in every check/test

    pr {
        lint = true                                     //Will be used to enable lint for the pr builds. Possible options: (true / false). Default: true
        detekt = true                                   //Will be used to enable detekt for the pr builds. Possible options: (true / false). Default: true
        unitTest = true                                 //Will be used to enable unit testing for the pr builds. Possible options: (true / false). Default: true
        deviceTest = true                               //Will be used to enable device testing for the pr builds. Possible options: (true / false). Default: false
        lintVariant = "your-lint-variant"               //This build variant will be used for the lint check in your pr build. Default: ("release" if exists, otherwise the first debuggable build type)
        unitTestVariant = "your-unit-test-variant"      //This build variant will be used for the unit testing in your pr build. Default: ("release" if exists, otherwise the first debuggable build type)
        deviceTestVariant = "your-device-test-variant"  //This build variant will be used for the device testing in your pr build. Default: ("release" if exists, otherwise the first debuggable build type)
    }

**Configure AppCenter Publisher**

_Default Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `appCenter` for your project with the default AppCenter configuration
<br/>
This are all the **required** params


    appCenter{
        apiKey = 'your-api-key'
        appOwner = 'your-organization-name'
        applicationIdToAppName = { applicationId ->
            return 'your-appcenter-app-name'
        }
    }

_Full Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `appCenter` for your project with all the given params

    appCenter {
        apiKey = '<your api key here>'
        appOwner = '<your appcenter app owner name here>'
        notifyTesters = false
        releaseNotes = '<your release notes here, markdown is supported by appcenter>'

        //Provide one of variantToAppName, applicationIdToAppName, flavorToAppName.
        //These methods can return null if this variant/app name does not support uploading to appcenter
        //NOTE: If the appname could not be found, no corresponding task will be generated
        //The app name is resolverd in the following order: applicationIdToAppName -> variantToAppName -> flavorToAppName
        applicationIdToAppName = { applicationId ->
            //Map application ids to appcenter app name
            return '<appcenter app name for this application id>'
        }
        flavorToAppName = { flavorName ->
            //Map flavors to appcenter app name
            return '<appcenter app name for this flavor>'
        }
        variantToAppName = { variantName ->
            //Map variants to appcenter app name
            return '<appcenter app name for this variant>'
        }

        //Which testers or groups to distribute to. Defaults to 'collaborators'. Must contain at least 1 entry
        testers = ['collaborators', ...]
    }

_More Info_

https://github.com/Chimerapps/gradle-appcenter-android-plugin

**Configure iCapps Translations**

_Default Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `translations` for your project with the default iCapps Translations configuration
<br/>
This are all the **required** params

    translations {
        apiKey = "your-icapps-translations-api-key"     //The token that will be used to authenticate with iCapps Translations
    }

_Recommended Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `translations` for your project with the default iCapps Translations configurations and will override all the given params

    translations {
        apiKey = "your-icapps-translations-api-key"     //The token that will be used to authenticate with iCapps Translations
        fileName = "translations.xml"                   //Name of the translations file. By changing the name to translations you wont get conlficts with hardcoded strings Default: strings.xml
        defaultLanguage = "en"                          //English will be stored in values/{fileName} instead of values-en/{fileName}
    }

_Full Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `translations` for your project with all the given params

    translations {
        apiKey = "your-icapps-translations-api-key"     //The token that will be used to authenticate with iCapps Translations
        fileName = "strings.xml"                        //Name of the translations file. Default: strings.xml
        sourceRoot = "src/main/res"                     //The location where the translation files will be saved. Default: src/main/res
        defaultLanguage = "en"                          //English will be stored in values/{fileName} instead of values-en/{fileName}

        languageRename { languageCode ->                //Default is identity transformation (return languageCode)
            return languageCode
        }
        sourceRootProvider { languageCode ->            //Function that provides source root based on language code (this is the raw language code)
            def renamed = languageRename(languageCode)
            return sourceRoot.replace("{language}", (renamed == null) ? languageCode : renamed)
        }

        fileNameProvider { languageCode ->              //Function that can generate different file names based on language code (this is the raw language code)
            return fileName
        }

        folderProvider { languageCode ->                //Function that provides the folder based on the (raw) language code
            def renamed = languageRename(languageCode)
            return "values-"+(renamed == null) ? languageCode : renamed)
        }

        languageFilter { language ->                    //Function that filters languages. The language passed is the raw language code
            return true
        }
    }

_More Info_

https://github.com/Chimerapps/icapps-translations-gradle-plugin\

**Configure Play Store Publisher**

_Default Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `playStore` for your project with the default Play Store Publisher configuration
<br/>
This are all the **required** params

    playStore {
        serviceAccountEmail = 'your-play-store-account-email'   //Your email adress that will be used to authenticate with Google Play Console
        pk12File = file("${projectDir}/signing/key.p12")        //The file that will be used for authenticating with the Google Play Console
    }

_Recommended Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `playStore` for your project with the default Play Store Publisher configurations and will override all the given params

    playStore {
        serviceAccountEmail = 'your-play-store-account-email'   //Your email adress that will be used to authenticate with Google Play Console
        pk12File = file("${projectDir}/signing/key.p12")        //The file that will be used for authenticating with the Google Play Console
        track = "alpha"                                         //The track where your bill wil be deployed. Possible Options: (alpha, beta, rollout, production). Default: alpha
    }

_Full Setup_

By adding the following command to your `iCappsBuildConfig` you will enable `playStore` for your project with all the given params

    playStore {
        serviceAccountEmail = 'your-play-store-account-email'   //Your email adress that will be used to authenticate with Google Play Console
        pk12File = file("${projectDir}/signing/key.p12")        //The file that will be used for authenticating with the Google Play Console
        track = "alpha"                                         //The track where your bill wil be deployed. Possible Options: (alpha, beta, rollout, production). Default: alpha
        userFraction = 0.1                                      //The track where your bill wil be deployed. Possible Options: value from 0 to 1. Default: 0.1
        untrackOld = false                                      //The track where your bill wil be deployed. Possible Options: (true,false). Default: false
    }

_More Info_

https://github.com/Triple-T/gradle-play-publisher

**Configure Bitbucket**

Automatically create pull requests on bitbucket. This task will first run the _pullRequest_ task to ensure the repo is in a valid state

_Full Setup_

    bitbucket {
        user = "repoUser"
        token = System.getenv('BITBUCKET_APP_KEY')                 //Bitbucket username that is linked to the app key. If unspecified, the value of user is used
        tokenUser = System.getenv('BITBUCKET_USER')          //Bitbucket app key, see https://github.com/Chimerapps/bitbucketcloud-api
        prBranch = "develop"                                    //Branch to create the PR to. Defaluts to 'develop'
    }

_More info_

https://github.com/Chimerapps/bitbucketcloud-api

<br/>
<br/>

**Used Resoures**

_Building a Gradle plugin:_

https://github.com/jonathanhood/gradle-plugin-example
<br/>
https://gradle.org/docs/
<br/>
https://discuss.gradle.org/t/mavenlocal-how-does-gradle-resolve-the-directory-of-the-local-maven-repository/4407

_AppCenter:_
https://github.com/Chimerapps/gradle-appcenter-android-plugin

_Gradle Translations plugin:_

https://github.com/Chimerapps/icapps-translations-gradle-plugin

_Gradle Play Store Publisher:_

https://github.com/Triple-T/gradle-play-publisher

_Gradle detektCheck plugin:_

https://github.com/arturbosch/detekt

_Bitbucket cloud api:_

https://github.com/Chimerapps/bitbucketcloud-api
