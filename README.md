# digitransitdemo
Demo for using Digitransit GraphQL API
This application displays nearby public transport stops and display it on a map, as well as a list view. Click on any of the stops to see the time table of each stops.

![Screenshot_480](https://github.com/bopmaz/digitransitdemo/assets/26660185/cdd9b17a-26dd-4c30-af35-8240cc6d0e8a)

## Technical feature
- This small application was built on Google Clean Architecture for Android
- Repository layer use Apollo to fetch from Digitransit's GraphQL API in combination with AndroidX Paging library to do pagination
- Presentation layer use Jetpack Compose to build and display the views
- Hilt was used to do dependency injection
- Version Catalogs was used to centralize and manage libraries dependency

## Class diagram:
![image](https://github.com/bopmaz/digitransitdemo/assets/26660185/714aa393-fde7-4371-873b-424001bf0d36)


