# How To Release

1. Prepare branch called `release/X.Y` (i.e. `release/2.1`)
1. Create release called `Supla GUI X.Y.Z` in GH releases
    1. Tag name should be `X.Y.Z`
    1. Tag source should be `release/X.Y` branch
    1. Body should contain `Enhancements`, `Bugs` and `Milestone` headers
1. Check out `release/X.Y` branch
1. Run `./gradlew clean prepareRelease`
1. Upload files:
    1. `buildDir/releaseApp/Supla-native-jre8.zip` - native library for JavaFx
    1. `buildDir/releaseApp/Supla-windows.zip` - Windows exe (only on windows)
    1. `buildDir/releaseApp/Supla-macos.zip` - MacOs pkg (only on macos)
1. Release