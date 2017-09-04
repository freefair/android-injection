# android-injection

Dependency Injection for Java and Android

## How to include: [![](https://jitpack.io/v/io.freefair/injection.svg)](https://jitpack.io/#io.freefair/injection)

## Core features:

- `@Inject`
  - Inject everything into everthing

## Android Features:

- `@InjectView(R.id.myView)`
  - Inject your views into Activities, Fragments and ViewGroups
- `@InjectResource(id, type)` and `@InjectAttribute(id, type)`
  - Inject Resources and Attributes into everything
- `@XmlLayout(R.layout.main)` and `@XmlMenu(R.menu.main)`
  - Set the Layout and Menu for your Activities and Fragments

## Available Modules

The modules enable the injection of certain classes:

- `okhttp3`
  - `okhttp3.OkHttpClient`
  - all your services
- `retrofit2`
 - `retrofit2.Retrofit`
  - all your services
