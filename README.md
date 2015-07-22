## Transactional Container

Transactional-aware container for Grid
Compatible with: Vaadin 7.6+

## Online demo

Try the add-on demo at *TBD*

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to http://vaadin.com/addon/transactional-container

## Building and running demo

git clone https://github.com/el-mot/vaadin-transactional-container-addon
mvn clean install
cd demo
mvn jetty:run

To see the demo, navigate to http://localhost:8080/
## Workflow

To compile the entire project including addon and demo, run "mvn install".
To run the application, run "mvn jetty:run" from transactional-container-demo folder and open http://localhost:8080/ .

To develop the theme, simply update the relevant theme files and reload the application.
Pre-compiling a theme eliminates automatic theme updates at runtime - see below for more information.

## Release notes

### Version 1.0-SNAPSHOT

*TBD*

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated.

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

TranscactionalContainer is written by Ilya Motornyy<elmot@vaadin.com>, Teppo Kurki <teppo@vaadin.com>

## Features

*TBD*

## API

Transactional Container JavaDoc is available online at http://el-mot.github.io/vaadin-transactional-container-addon/