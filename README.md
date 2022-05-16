# SAMPLE JAVA APPLICATION
### 1. Tech stack
- java 8
- Spring boot 2.6.4
- Spring boot security
- Spring boot jwt
- Spring boot jpa
- Spring boot actuator
- MySQL 8.0.28
- Swagger 3.0
- Docker
- Grafana & Prometheus

### 2. Getting started

2.1 build docker images
```
$ mvn clean install
$ docker-compose up -d --build
```

2.2 Test
```
$ curl --location --request POST http://localhost:8080/api/v1/auth/login --header Authorization:Basic b2F1dGgyQ2xpZW50Om9hdXRoMlNlY3JldA== --header Content-Type:application/x-www-form-urlencoded --data-urlencode username=sysadmin --data-urlencode password=password
```

2.3 View API information by Swagger

Visit Swagger:  [Swagger UI](http://localhost:8080/api/v1/swagger-ui.html)


2.4 Check Application Health
```
$ curl --location --request GET http://localhost:8099/actuator
```

2.5 Update Code and ReRun
```
$ mvn clean install
$ docker-compose up -d --build api-service
```
 
2.6 View api-service container log
```
$ docker-compose logs -tf api-service
```

2.7 Remote Debug
Connect port 5005

2.8 Log and Monitoring API-Service
    
- Monitoring and alert: [Prometheus Target](http://localhost:9090/targets)
- Prometheus web UI: [Prometheus Graph](http://localhost:9090/graph)
- Grafana web UI: [Grafana](http://localhost:3000)
  - default account: admin/admin
    
 

###3 Push Images to Docker Hub (Reverse to deploy on EC2)
**3.1 On Local**
- Create docker tag
```
$ docker tag sample-java-app_api-service luongquoctay87/sample-java-app:v1.0.0
```

- Push to Docker Hub
```
$ docker login
$ docker push luongquoctay87/sample-java-app:v1.0.0
```

**3.2 On EC2**

- Install docker
```
$ sudo su -
$ yum install -y docker
$ service docker start
$ systemctl enable docker
$ docker --version
```

- Install docker compose
```
$ curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
$ chmod +x /usr/local/bin/docker-compose
$ ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
$ docker-compose -v
```

- Create file `docker-compose.yml`
```
version: '3.8'

services:
  api-service:
      image: luongquoctay87/sample-java-app:v1.0.0
      container_name: api-service
      environment:
        - DB_URL=database-1.ctixzu74yr0p.ap-southeast-1.rds.amazonaws.com
        - DB_NAME=db_test
        - DB_USER=admin
        - DB_PASSWORD=12345678
      ports:
        - "8080:8080"
      volumes:
        - ./:/app
      networks:
        - backend

  prometheus:
    image: "prom/prometheus"
    container_name: prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"
    networks:
      - backend

  grafana:
    image: "grafana/grafana"
    container_name: grafana
    ports:
      - "3000:3000"
    networks:
      - backend

networks:
  backend:
    name: api-network

```

- Create file `prometheus.yml`
```
scrape_configs:
  - job_name: 'app-metrics'
    metrics_path: '/api/v1/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets:
          - 54.151.224.116:8080
```