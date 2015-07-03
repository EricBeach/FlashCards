Flash Cards
==================

___What is Flash Cards___

A web application written with a backend in Java and a front-end in Web Components as well as JavaScript that enables approved individuals to:

- Practice memorizing information with an electronic flash card
- See past performance with flash cards

___Development Environment___

The development environment consists of three parts:

- [Eclipse](https://eclipse.org/) - While you don't technically need this, it integrates very nicely with the Google Plugin for Eclipse, thereby making development much easier than via command-line or another IDE.
- [Google Plugin for Eclipse](https://developers.google.com/eclipse/) - This plugin enables you to run the application locally, deploy to App engine in one-click, and other benefits.
- [Google App Engine SDK](https://cloud.google.com/appengine/downloads) - Download the SDK for Java.

You will need to compile your code for Java 7 by [changing the compliance level](https://developers.google.com/eclipse/docs/jdk_compliance). You can track the status of Google App Engine's support for Java 8 in [this bug](https://code.google.com/p/googleappengine/issues/detail?id=9537). Sadly Java 8 support does not look like it is coming anytime soon.

You will need to add Google App Engine SDK to the Eclipse Build path. Instructions are [here](https://developers.google.com/eclipse/docs/existingprojects).