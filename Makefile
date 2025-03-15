run-db:
	docker-compose -f docker/docker-compose.yml up db pgadmin -d

down-db:
	docker-compose -f docker/docker-compose.yml down db pgadmin