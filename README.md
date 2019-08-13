### Android Gradle Build Plugin###

**Getting Started**

This readme is purly for using the basic config and how you should setup the plugin. Check the [Wiki](https://github.com/icapps/android_gradle_build/wiki) for a specific configuration

**Setup**

[ ![Download](https://api.bintray.com/packages/icapps/maven/icapps-build-gradle-plugin/images/download.svg) ](https://bintray.com/icapps/maven/icapps-build-gradle-plugin/_latestVersion)

Import in root gradle 

```
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
```
  
Apply the plugin by adding next command at the top of your app/build.gradle file

    apply plugin: 'icapps-build-gradle-plugin'

**Config**

Add the `iCappsBuildConfig` at the bottom of you `app/build.gradle` file 

- detekt will be enabled

- pr will be enabled with the correct `lineVariant` and `unitTestVariant`

- translations will be enabled with the given `apiKey`

- playStore will be enabled with the given `serviceAccountEmail` and `pk12File`

```
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
        
        appCenter {
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
```

**Wiki Pages**

We added wiki pages to make sure we have better documentation with a clean getting started README

Extra info about:

- [General Info](https://github.com/icapps/android_gradle_build/wiki)

- [Detekt](https://github.com/icapps/android_gradle_build/wiki/detekt)

- [Pr](https://github.com/icapps/android_gradle_build/wiki/pr)

- [Translations](https://github.com/icapps/android_gradle_build/wiki/Translations)

- [AppCenter](https://github.com/icapps/android_gradle_build/wiki/AppCenter)

- [PlayStore](https://github.com/icapps/android_gradle_build/wiki/PlayStore)

- [Bitbucket](https://github.com/icapps/android_gradle_build/wiki/Bitbucket)
