Election Administration Microservice
====================================

This microservice is part of our project FWPM Election and it handles everything about an election period.
This includes creating, updating, deleting and showing a period. It also sends emails to inform/remind about the current election period.
The project is developed for a docker-environment.

You can find our Dockerfiles under:

* [Dockerfiles OSX][osx]
* [Dockerfiles][linux]

API Documentation
-----------------

You can find the api documentation [here][api]

System Requirements
-------------------

* Running docker-environment described in the Dockerfile-Repos 
* [Maven][mvn]

Running
-------

* Add a Server 'tomcat-localhost' with the same User and Password you configured in your Tomcat
* $ mvn clean install
* $ mvn tomcat:redeploy

[osx]: https://github.com/marcelgross90/Tomcat-MYSQL-Docker
[linux]: https://github.com/marcelgross90/Tomcat-MYSQL-Docker-Linux
[mvn]: https://maven.apache.org/
[api]: https://confluence.student.fiw.fhws.de:8443/display/PSS16FWPMWAHL/API-Dokumentation