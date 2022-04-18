buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(BuildPlugins.kotlinGradlePlugin)
        classpath(BuildPlugins.hiltGradlePlugin)
        classpath(BuildPlugins.mapsSecretGradlePlugin)
        classpath (BuildPlugins.buildGradle)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}