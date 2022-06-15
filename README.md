## Bookclub

IT 서적 스터디와 공유를 위한 플랫폼입니다.  
자신이 원하는 책을 주제로 팀원들과 스터디 할 수 있습니다.

## 기술 스택

- Java 11
- Spring Boot 2.5.x
- Gradle
- Spring Data Jpa
- QueryDSL
- Spring Security
- Rabbitmq
- Elastic Search
- Redis

## System Architecture

![image](https://user-images.githubusercontent.com/45138206/172886044-e773c28f-97bb-44ae-ba08-65a1df52e2c9.png)

## 주요 내용
- [도메인 중심으로 약 20개 가량 DB Modeling 설계 및 JPA 연관관계 작성](https://github.com/sunggyupaik/project-spring-1-sunggyupaik/wiki/ERD)

- TDD를 통해 작은 단위로 기능을 만들기(테스트 코드 약 160개)  
TDD로 코드의 안정성을 높이며, 좀 더 작은 단위로 메서드 작성 고민

- RabbitMQ, Elastic Search를 활용한 한 줄 게시판 저장/조회 성능 향상  
[RabbitMQ 적용](https://escapefromcoding.tistory.com/692), [Elastic Search로 저장소 구현](https://escapefromcoding.tistory.com/693?category=1258162), [artillery로 여러 환경에서 부하 테스트 및 성능 개선](https://escapefromcoding.tistory.com/664)
 
- Spring Rest Docs로 문서화 및 Java Docs 주석  
다른 사람이 내 코드를 볼 때 빠르게 이해하는 데 도움을 주고자 문서 및 주석과 네이밍 작성
 
- ec2 + jenkins + dockerhub 를 이용하여 AWS에 CI/CD 구축  
[jenkins와 dockerhub를 통해 AWS ec2에 빌드 및 배포가 되도록 구성](https://escapefromcoding.tistory.com/605)
