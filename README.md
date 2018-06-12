# Runtrack

[![Build Status](https://travis-ci.org/danibranas/runtrack.svg?branch=master)](https://travis-ci.org/danibranas/runtrack)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Tracking app for runners for Android platform.

**Work in Progress!!**: this project is not suitable for use in production.

## About this project

_Runtrack_ is a tracking app for runners, oriented to competition profiles.
Organizers can publish their races and athletes can get info,
join them and track their results.

Please, see the [Wiki (Spanish)](https://github.com/danibranas/runtrack/wiki) to get more info.

## Development environment

_Runtrack_ is programmed in Kotlin and uses some external services
as Google Play Services, which require using API Keys to work. For
security reasons, keys are not published on the repo, but you can use
your owns. To do that, you can create a `res/values/secrets.xml` file,
excluded from VSC, and fill it with the values. There is a template
on `res/values/secrets.template.xml`.

## Backend

You can find the backend repository on GitHub's
[rcoedo/untrack-be](https://github.com/rcoedo/runtrack-be) repo.