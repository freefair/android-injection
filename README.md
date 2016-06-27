# android-injection

Dependency Injection for Android

## How to include: [![](https://jitpack.io/v/io.freefair/android-injection.svg)](https://jitpack.io/#io.freefair/android-injection)

## Core features:

- `@InjectView(R.id.myView)`
  - Inject your views into Activities, Fragments and ViewGroups
- `@InjectResource(id, type)` and `@InjectAttribute(id, type)`
  - Inject Resources and Attributes into everything
- `@Inject`
  - Inject everything into everthing

## Available Modules

The modules enable the injection of certain classes:

- `logging`
  - `io.freefair.android.util.logging.Logger`
- `okhttp`
  - `com.squareup.okhttp.OkHttpClient`
- `okhttp3`
  - `okhttp3.OkHttpClient`
- `retrofit`
  - `retrofit.RestAdapter`
  - all your services
- `retrofit2`
 - `retrofit2.Retrofit`
  - all your services
- `realm`
  - `io.realm.Realm`
