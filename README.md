# About

PoC to populate test database properties either using existing DB or spin up a Testcontainer without `@DynamicPropertySource`.

## Current Problem

The `@DynamicPropertySource` method requires the annotation is on the test class/method hierarchy, which enforces the inheritance for reusing the database settings among test classes.
Instead, I am looking for a composable solution using an annotation.

## Approach

This PoC enables annotating a spring test class with `@WithMySql` annotation.
Using the `ContextCustomizer` infrastructure from spring, the implementation determines a database, either reachable actual DB or spin up a Testcontainer, then populates a corresponding jdbc url to the  `spring.datasource.url` property.

## Misc

See https://github.com/spring-projects/spring-framework/issues/29729
