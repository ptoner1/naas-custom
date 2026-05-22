Demo for an Email messaging service.

docker-compose up should start up all 4 services: Angular UI, Java (restAPI, DB management, MQ management), PostgresDB, IBM MQ.


AFTER STARTING THE DOCKER MQ, MAKE SURE TO RUN: 
docker exec -i QM1 runmqsc QM1 < init.mqsc
To set configuration.



