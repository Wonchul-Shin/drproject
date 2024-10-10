# DR(전력중개) 관리 플랫폼 
![도메인정보](images/domain_info.png)

## 도메인 설명
- **DR 제도 설명**: 전기 사용자가 전기 사용량을 감축하고 이를 전력시장에 판매하여 금전적인 보상을 받는 제도

- **KPX의 역할**: KPX(전력거래소)는 전력 감축이 필요한 상황이 발생하면, 수요관리사업자인 KT와 같은 사업자에게 DR 명령을 전달한다. 수요관리사업자는 이 명령을 바탕으로 즉시 고객에게 전력 감축 요청을 전달하며, KPX는 전력 감축 여부를 확인하고 이에 따라 정산금을 지급한다.


- **KT의 역할(수요관리업자)**: DR 참여 고객을 발굴하고, 전력 감축 요청이 있을 때 이를 고객에게 안내하는 역할을 수행한다. 또한, 전력 감축이 완료되면 KPX로부터 받은 금액을 고객에게 정산하는 업무를 처리한다.

- **관리 시스템 필요성**: KT는 현재 DR 관련 업무를 진행 중이나, 별도 관리 툴이 없어 오류와 지연 가능성이 있으며, 이를 개선하기 위해 전력 감축 안내 및 이행 여부를 확인하는 통합 관리 시스템이 필요함.

## Model
[www.msaez.io/#/72932922/storming/drproject
](https://www.msaez.io/#/72932922/storming/drproject)

# 서비스 시나리오

### DR플랫폼

#### 기능적 요구사항 
1. 관리자가 DR명령을 시작/종료할 수 있다.
2. 고객이 현재 진행중인 멸영에 대하여 이행/불이행을 선택한다.
3. 고나리자는 실행중인 명령을 종료한다.
4. 명령이 종료되었을 때 한전에 전력사용량을 확인하고 실제 감축량만큼 유저의 포인트를 늘린다.

#### 비기능적 요구사항
1. 장애격리
    - 한전API등 각각의 서비스에 오류가 생겨도 핵심 서비스가 작동하도록 한다. 
    - 오류 발생 시 해당 마이크로서비스만 재시작하거나 복구할 수 있다.
    - 서비스를 컨테이너화 하여 오류 탐지 및 복구를 자동화하여 장애 대응 시간을 최소화한다. 
2. 성능
    - 관리자는 모든 유저의 정보와 현재 진행중인 DR 그리고 응답정보를 한눈에 확인할 수 있어야 한다. 

# 분석/설계

## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  www.msaez.io/#/72932922/storming/drplatproject


### 이벤트 도출
![이벤트 도출](images/event_storming_v1.png)


### 부적격 이벤트 탈락
![부적격 이벤트 탈락](images/event_storming_v2.png)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함

### 액터, 커맨드 부착하여 읽기 좋게
![엑터, 커맨드](images/actor+command.png)

### 어그리게잇으로 묶기
![어그리게이트](images/aggregate.png)

    - dr, user, response와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 묶음

### 바운디드 컨텍스트로 묶기

![바운디드컨텍스트](images/boundedcontext.png)

### 폴리시 부착

![폴리시부착](images/policy.png) 

### 완성된 모형

![폴리시부착](images/finished_model.png) 

### 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증
1. 관리자가 DR명령을 시작/종료할 수 있다. (ok)
2. 고객이 현재 진행중인 멸영에 대하여 이행/불이행을 선택한다. (ok)
3. 관리자는 실행중인 명령을 종료한다. (ok)
4. 명령이 종료되었을 때 한전에 전력사용량을 확인하고 실제 감축량만큼 유저의 포인트를 늘린다. (ok)
    
### 비기능 요구사항에 대한 검증
- 마이크로 서비스를 넘나드는 시나리오에 대한 트랜잭션 처리
- 고객 예약시 결제처리:  결제가 완료되지 않은 예약은 절대 받지 않는다고 결정하여, ACID 트랜잭션 적용. 예약 완료시 사전에 방 상태를 확인하는 것과 결제처리에 대해서는 Request-Response 방식 처리
- 결제 완료시 Host 연결 및 예약처리:  reservation 에서 room 마이크로서비스로 예약요청이 전달되는 과정에 있어서 room 마이크로 서비스가 별도의 배포주기를 가지기 때문에 Eventual Consistency 방식으로 트랜잭션 처리함.
- 나머지 모든 inter-microservice 트랜잭션: 예약상태, 후기처리 등 모든 이벤트에 대해 데이터 일관성의 시점이 크리티컬하지 않은 모든 경우가 대부분이라 판단, Eventual Consistency 를 기본으로 채택함.

# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
	cd dr
	mvn spring-boot:run
	
	cd frontend
	mvn spring-boot:run 
	
	cd gateway
	mvn spring-boot:run  
	
	cd kepco
	mvn spring-boot:run
	
	cd response
	mvn spring-boot:run

	cd user
	mvn spring-boot:run
```

## API 게이트웨이
Local 테스트 환경에서는 localhost:8080에서 Gateway API 가 작동.
Cloud 환경에서는 http://20.249.203.195:8080/ 에서 Gateway API가 작동.
application.yml 파일에 프로파일 별로 Gateway 설정.
```
server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
#<<< API Gateway / Routes
      routes:
        - id: dr
          uri: http://localhost:8082
          predicates:
            - Path=/drs/**, 
        - id: response
          uri: http://localhost:8083
          predicates:
            - Path=/responses/**, 
        - id: user
          uri: http://localhost:8084
          predicates:
            - Path=/users/**, 
        - id: kepco
          uri: http://localhost:8085
          predicates:
            - Path=/kepcos/**, 
        - id: frontend
          uri: http://localhost:8080
          predicates:
            - Path=/**
#>>> API Gateway / Routes
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true


---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: dr
          uri: http://dr:8080
          predicates:
            - Path=/drs/**, 
        - id: response
          uri: http://response:8080
          predicates:
            - Path=/responses/**, 
        - id: user
          uri: http://user:8080
          predicates:
            - Path=/users/**, 
        - id: kepco
          uri: http://kepco:8080
          predicates:
            - Path=/kepcos/**, 
        - id: frontend
          uri: http://frontend:8080
          predicates:
            - Path=/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080

```


## CI/CD 설정

아래 이미지는 azure의 pipeline에 각각의 서비스들을 올려, 코드가 업데이트 될때마다 자동으로 빌드/배포 하도록 하였다.
![docker hub](images/docker_hub.png)

그 결과 kubernetes cluster에 아래와 같이 서비스가 올라가있는 것을 확인할 수 있다.
![cluster](images/kubectl_get_pods.png)
![cluster](images/kubectl_get_svc.png)


먼저, 임의의 사용자를 등록한다.
![cluster](images/users_add_1.png)
![cluster](images/users_add_2.png)


## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다. (예시는 DR Project의 핵심 도메인인 room 마이크로 서비스).

```
@Entity
@Table(name = "Response_table")
@Data
//<<< DDD / Aggregate Root
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private String userName;
    private String drId;
    private String answer;
    private Integer userCapacity;

    @PostPersist
    public void onPostPersist() {
        Listmade listmade = new Listmade(this);
        listmade.publishAfterCommit();
        
        Listsaved listsaved = new Listsaved(this);
        listsaved.publishAfterCommit();

        if ("accept".equals(this.answer)) {
            AcceptChosen acceptChosen = new AcceptChosen(this);
            acceptChosen.publishAfterCommit();
        }

        if ("deny".equals(this.answer)) {
            DenyChosen denyChosen = new DenyChosen(this);
            denyChosen.publishAfterCommit();       
         }
    }

    public static ResponseRepository repository() {
        ResponseRepository responseRepository = ResponseApplication.applicationContext.getBean(
            ResponseRepository.class
        );
        return responseRepository;
    }

    public static void makelist(DRstarted dRstarted) {
        PagedModel<User> users = ResponseApplication.applicationContext
        .getBean(drproject.external.UserService.class)
        .getAllUsers();
            for (User user :  users.getContent()) {
                Response response = new Response();
                response.setDrId(String.valueOf(dRstarted.getId())); 
                response.setUserId(user.getId());
                response.setUserName(user.getName());
                response.setUserCapacity(user.getCapacity()); 
                response.setAnswer("ignore");     
                repository().save(response);
            }
    }

    public static void savelist(DrEnded drEnded) {
        List<Response> responses = repository().findByDrId(String.valueOf(drEnded.getId()));
        for (Response response : responses) {
            Listsaved listsaved = new Listsaved(response);
            listsaved.publishAfterCommit(); 
        }
    }
    public static void updateResponse(ReductionCheck reductionCheck) {
        if (!reductionCheck.getIsReal()) {
            repository().findById(reductionCheck.getId()).ifPresent(response -> {
                if (response.getId().equals(reductionCheck.getResponseId())) {
                    System.out.println(response.userName + "did not execute reduction");
                    response.setAnswer("deny");
                    repository().save(response);
                }
            });
        }
    }

}

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package drproject;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="responses", path="responses")
public interface ResponseRepository extends PagingAndSortingRepository<Response, Long> {
}
```
- 적용 후 REST API 의 테스트
```
# reservation 서비스의 예약 요청
http POST :8082/startdr name="kt" type="reliable" status="running" date="2024-10-08"
 - startdr이 실행되면 resposne가 각 user 별 하나의 response가 만들어진다 (초기값 ignore)

# response에 대한 승인처리 
http PUT :8083/responses/2/accept

# response 확인
http :8083/responses
```
## 동기식 호출(Sync) 과 Fallback 처리

분석 단계에서의 조건 중 하나로 예약 시 숙소(room) 간의 예약 가능 상태 확인 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 또한 예약(reservation) -> 결제(payment) 서비스도 동기식으로 처리하기로 하였다.

- 룸, 결제 서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# PaymentService.java

package airbnb.external;

<import문 생략>

@FeignClient(name="Payment", url="${prop.room.url}")
public interface PaymentService {

    @RequestMapping(method= RequestMethod.POST, path="/payments")
    public void approvePayment(@RequestBody Payment payment);

}

# RoomService.java

package airbnb.external;

<import문 생략>

@FeignClient(name="Room", url="${prop.room.url}")
public interface RoomService {

    @RequestMapping(method= RequestMethod.GET, path="/check/chkAndReqReserve")
    public boolean chkAndReqReserve(@RequestParam("roomId") long roomId);

}


```

- 예약 요청을 받은 직후(@PostPersist) 가능상태 확인 및 결제를 동기(Sync)로 요청하도록 처리
```
# Reservation.java (Entity)

    @PostPersist
    public void onPostPersist(){

        ////////////////////////////////
        // RESERVATION에 INSERT 된 경우 
        ////////////////////////////////

        ////////////////////////////////////
        // 예약 요청(reqReserve) 들어온 경우
        ////////////////////////////////////

        // 해당 ROOM이 Available한 상태인지 체크
        boolean result = ReservationApplication.applicationContext.getBean(airbnb.external.RoomService.class)
                        .chkAndReqReserve(this.getRoomId());
        System.out.println("######## Check Result : " + result);

        if(result) { 

            // 예약 가능한 상태인 경우(Available)

            //////////////////////////////
            // PAYMENT 결제 진행 (POST방식) - SYNC 호출
            //////////////////////////////
            airbnb.external.Payment payment = new airbnb.external.Payment();
            payment.setRsvId(this.getRsvId());
            payment.setRoomId(this.getRoomId());
            payment.setStatus("paid");
            ReservationApplication.applicationContext.getBean(airbnb.external.PaymentService.class)
                .approvePayment(payment);

            /////////////////////////////////////
            // 이벤트 발행 --> ReservationCreated
            /////////////////////////////////////
            ReservationCreated reservationCreated = new ReservationCreated();
            BeanUtils.copyProperties(this, reservationCreated);
            reservationCreated.publishAfterCommit();
        }
    }
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 주문도 못받는다는 것을 확인:


```
# 결제 (pay) 서비스를 잠시 내려놓음 (ctrl+c)

# 예약 요청
http POST http://localhost:8088/reservations roomId=1 status=reqReserve   #Fail

# 결제서비스 재기동
cd payment
mvn spring-boot:run

# 예약 요청
http POST http://localhost:8088/reservations roomId=1 status=reqReserve   #Success
```

- 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다. (서킷브레이커, 폴백 처리는 운영단계에서 설명한다.)




## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트


결제가 이루어진 후에 숙소 시스템의 상태가 업데이트 되고, 예약 시스템의 상태가 업데이트 되며, 예약 및 취소 메시지가 전송되는 시스템과의 통신 행위는 비동기식으로 처리한다.
 
- 이를 위하여 결제가 승인되면 결제가 승인 되었다는 이벤트를 카프카로 송출한다. (Publish)
 
```
# Payment.java

package airbnb;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name="Payment_table")
public class Payment {

    ....

    @PostPersist
    public void onPostPersist(){
        ////////////////////////////
        // 결제 승인 된 경우
        ////////////////////////////

        // 이벤트 발행 -> PaymentApproved
        PaymentApproved paymentApproved = new PaymentApproved();
        BeanUtils.copyProperties(this, paymentApproved);
        paymentApproved.publishAfterCommit();
    }
    
    ....
}
```

- 예약 시스템에서는 결제 승인 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
# Reservation.java

package airbnb;

    @PostUpdate
    public void onPostUpdate(){
    
        ....

        if(this.getStatus().equals("reserved")) {

            ////////////////////
            // 예약 확정된 경우
            ////////////////////

            // 이벤트 발생 --> ReservationConfirmed
            ReservationConfirmed reservationConfirmed = new ReservationConfirmed();
            BeanUtils.copyProperties(this, reservationConfirmed);
            reservationConfirmed.publishAfterCommit();
        }
        
        ....
        
    }

```

그 외 메시지 서비스는 예약/결제와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 메시지 서비스가 유지보수로 인해 잠시 내려간 상태 라도 예약을 받는데 문제가 없다.

```
# 메시지 서비스 (message) 를 잠시 내려놓음 (ctrl+c)

# 예약 요청
http POST http://localhost:8088/reservations roomId=1 status=reqReserve   #Success

# 예약 상태 확인
http GET localhost:8088/reservations    #메시지 서비스와 상관없이 예약 상태는 정상 확인

```

# 운영


## CI/CD 설정

각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD는 buildspec.yml을 이용한 AWS codebuild를 사용하였습니다.

- CodeBuild 프로젝트를 생성하고 AWS_ACCOUNT_ID, KUBE_URL, KUBE_TOKEN 환경 변수 세팅을 한다
```
SA 생성
kubectl apply -f eks-admin-service-account.yml
```
![codebuild(sa)](https://user-images.githubusercontent.com/38099203/119293259-ff52ec80-bc8c-11eb-8671-b9a226811762.PNG)
```
Role 생성
kubectl apply -f eks-admin-cluster-role-binding.yml
```
![codebuild(role)](https://user-images.githubusercontent.com/38099203/119293300-1abdf780-bc8d-11eb-9b07-ad173237efb1.PNG)
```
Token 확인
kubectl -n kube-system get secret
kubectl -n kube-system describe secret eks-admin-token-rjpmq
```
![codebuild(token)](https://user-images.githubusercontent.com/38099203/119293511-84d69c80-bc8d-11eb-99c7-e8929e6a41e4.PNG)
```
buildspec.yml 파일 
마이크로 서비스 room의 yml 파일 이용하도록 세팅
```
![codebuild(buildspec)](https://user-images.githubusercontent.com/38099203/119283849-30292680-bc79-11eb-9f86-cbb715e74846.PNG)

- codebuild 실행
```
codebuild 프로젝트 및 빌드 이력
```
![codebuild(프로젝트)](https://user-images.githubusercontent.com/38099203/119283851-315a5380-bc79-11eb-9b2a-b4522d22d009.PNG)
![codebuild(로그)](https://user-images.githubusercontent.com/38099203/119283850-30c1bd00-bc79-11eb-9547-1ff1f62e48a4.PNG)

- codebuild 빌드 내역 (Message 서비스 세부)

![image](https://user-images.githubusercontent.com/31723044/119385500-2b0fba00-bd01-11eb-861b-cc31910ff945.png)

- codebuild 빌드 내역 (전체 이력 조회)

![image](https://user-images.githubusercontent.com/31723044/119385401-087da100-bd01-11eb-8b69-ce222e6bb71e.png)




## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: istio 사용하여 구현함

시나리오는 예약(reservation)--> 룸(room) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 예약 요청이 과도할 경우 CB 를 통하여 장애격리.

- DestinationRule 를 생성하여 circuit break 가 발생할 수 있도록 설정
최소 connection pool 설정
```
# destination-rule.yml
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: dr-room
  namespace: airbnb
spec:
  host: room
  trafficPolicy:
    connectionPool:
      http:
        http1MaxPendingRequests: 1
        maxRequestsPerConnection: 1
#    outlierDetection:
#      interval: 1s
#      consecutiveErrors: 1
#      baseEjectionTime: 10s
#      maxEjectionPercent: 100
```

* istio-injection 활성화 및 room pod container 확인

```
kubectl get ns -L istio-injection
kubectl label namespace airbnb istio-injection=enabled 
```

![Circuit Breaker(istio-enjection)](https://user-images.githubusercontent.com/38099203/119295450-d6812600-bc91-11eb-8aad-46eeac968a41.PNG)

![Circuit Breaker(pod)](https://user-images.githubusercontent.com/38099203/119295568-0cbea580-bc92-11eb-9d2b-8580f3576b47.PNG)


* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:

siege 실행

```
kubectl run siege --image=apexacme/siege-nginx -n airbnb
kubectl exec -it siege -c siege -n airbnb -- /bin/bash
```


- 동시사용자 1로 부하 생성 시 모두 정상
```
siege -c1 -t10S -v --content-type "application/json" 'http://room:8080/rooms POST {"desc": "Beautiful House3"}'

** SIEGE 4.0.4
** Preparing 1 concurrent users for battle.
The server is now under siege...
HTTP/1.1 201     0.49 secs:     254 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.05 secs:     254 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     254 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.03 secs:     254 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     254 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     254 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.03 secs:     254 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.03 secs:     254 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.03 secs:     254 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.03 secs:     256 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.03 secs:     256 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     256 bytes ==> POST http://room:8080/rooms
```

- 동시사용자 2로 부하 생성 시 503 에러 168개 발생
```
siege -c2 -t10S -v --content-type "application/json" 'http://room:8080/rooms POST {"desc": "Beautiful House3"}'

** SIEGE 4.0.4
** Preparing 2 concurrent users for battle.
The server is now under siege...
HTTP/1.1 201     0.02 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 503     0.10 secs:      81 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.04 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.05 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.22 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.08 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.07 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 503     0.01 secs:      81 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.03 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 503     0.01 secs:      81 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     258 bytes ==> POST http://room:8080/rooms
HTTP/1.1 503     0.00 secs:      81 bytes ==> POST http://room:8080/rooms

Lifting the server siege...
Transactions:                   1904 hits
Availability:                  91.89 %
Elapsed time:                   9.89 secs
Data transferred:               0.48 MB
Response time:                  0.01 secs
Transaction rate:             192.52 trans/sec
Throughput:                     0.05 MB/sec
Concurrency:                    1.98
Successful transactions:        1904
Failed transactions:             168
Longest transaction:            0.03
Shortest transaction:           0.00
```

- kiali 화면에 서킷 브레이크 확인

![Circuit Breaker(kiali)](https://user-images.githubusercontent.com/38099203/119298194-7f7e4f80-bc97-11eb-8447-678eece29e5c.PNG)


- 다시 최소 Connection pool로 부하 다시 정상 확인

```
** SIEGE 4.0.4
** Preparing 1 concurrent users for battle.
The server is now under siege...
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.03 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.00 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.00 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.00 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms

:
:

Lifting the server siege...
Transactions:                   1139 hits
Availability:                 100.00 %
Elapsed time:                   9.19 secs
Data transferred:               0.28 MB
Response time:                  0.01 secs
Transaction rate:             123.94 trans/sec
Throughput:                     0.03 MB/sec
Concurrency:                    0.98
Successful transactions:        1139
Failed transactions:               0
Longest transaction:            0.04
Shortest transaction:           0.00

```

- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌.
  virtualhost 설정과 동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.


### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 

- room deployment.yml 파일에 resources 설정을 추가한다
![Autoscale (HPA)](https://user-images.githubusercontent.com/38099203/119283787-0a038680-bc79-11eb-8d9b-d8aed8847fef.PNG)

- room 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 50프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deployment room -n airbnb --cpu-percent=50 --min=1 --max=10
```
![Autoscale (HPA)(kubectl autoscale 명령어)](https://user-images.githubusercontent.com/38099203/119299474-ec92e480-bc99-11eb-9bc3-8c5246b02783.PNG)

- 부하를 동시사용자 100명, 1분 동안 걸어준다.
```
siege -c100 -t60S -v --content-type "application/json" 'http://room:8080/rooms POST {"desc": "Beautiful House3"}'
```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다
```
kubectl get deploy room -w -n airbnb 
```
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:
![Autoscale (HPA)(모니터링)](https://user-images.githubusercontent.com/38099203/119299704-6a56f000-bc9a-11eb-9ba8-55e5978f3739.PNG)

- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. 
```
Lifting the server siege...
Transactions:                  15615 hits
Availability:                 100.00 %
Elapsed time:                  59.44 secs
Data transferred:               3.90 MB
Response time:                  0.32 secs
Transaction rate:             262.70 trans/sec
Throughput:                     0.07 MB/sec
Concurrency:                   85.04
Successful transactions:       15675
Failed transactions:               0
Longest transaction:            2.55
Shortest transaction:           0.01
```

## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

```
kubectl delete destinationrules dr-room -n airbnb
kubectl label namespace airbnb istio-injection-
kubectl delete hpa room -n airbnb
```

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c100 -t60S -r10 -v --content-type "application/json" 'http://room:8080/rooms POST {"desc": "Beautiful House3"}'

** SIEGE 4.0.4
** Preparing 1 concurrent users for battle.
The server is now under siege...
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.03 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.00 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.02 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms
HTTP/1.1 201     0.01 secs:     260 bytes ==> POST http://room:8080/rooms

```

- 새버전으로의 배포 시작
```
kubectl set image ...
```

- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인

```
siege -c100 -t60S -r10 -v --content-type "application/json" 'http://room:8080/rooms POST {"desc": "Beautiful House3"}'


Transactions:                   7732 hits
Availability:                  87.32 %
Elapsed time:                  17.12 secs
Data transferred:               1.93 MB
Response time:                  0.18 secs
Transaction rate:             451.64 trans/sec
Throughput:                     0.11 MB/sec
Concurrency:                   81.21
Successful transactions:        7732
Failed transactions:            1123
Longest transaction:            0.94
Shortest transaction:           0.00

```
- 배포기간중 Availability 가 평소 100%에서 87% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기위해 Readiness Probe 를 설정함

```
# deployment.yaml 의 readiness probe 의 설정:
```

![probe설정](https://user-images.githubusercontent.com/38099203/119301424-71333200-bc9d-11eb-9f75-f8c98fce70a3.PNG)

```
kubectl apply -f kubernetes/deployment.yml
```

- 동일한 시나리오로 재배포 한 후 Availability 확인:
```
Lifting the server siege...
Transactions:                  27657 hits
Availability:                 100.00 %
Elapsed time:                  59.41 secs
Data transferred:               6.91 MB
Response time:                  0.21 secs
Transaction rate:             465.53 trans/sec
Throughput:                     0.12 MB/sec
Concurrency:                   99.60
Successful transactions:       27657
Failed transactions:               0
Longest transaction:            1.20
Shortest transaction:           0.00

```

배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.


# Self-healing (Liveness Probe)
- room deployment.yml 파일 수정 
```
콘테이너 실행 후 /tmp/healthy 파일을 만들고 
90초 후 삭제
livenessProbe에 'cat /tmp/healthy'으로 검증하도록 함
```
![deployment yml tmp healthy](https://user-images.githubusercontent.com/38099203/119318677-8ff0f300-bcb4-11eb-950a-e3c15feed325.PNG)

- kubectl describe pod room -n airbnb 실행으로 확인
```
컨테이너 실행 후 90초 동인은 정상이나 이후 /tmp/healthy 파일이 삭제되어 livenessProbe에서 실패를 리턴하게 됨
pod 정상 상태 일때 pod 진입하여 /tmp/healthy 파일 생성해주면 정상 상태 유지됨
```

![get pod tmp healthy](https://user-images.githubusercontent.com/38099203/119318781-a9923a80-bcb4-11eb-9783-65051ec0d6e8.PNG)
![touch tmp healthy](https://user-images.githubusercontent.com/38099203/119319050-f118c680-bcb4-11eb-8bca-aa135c1e067e.PNG)

# Config Map/ Persistence Volume
- Persistence Volume

1: EFS 생성
```
EFS 생성 시 클러스터의 VPC를 선택해야함
```
![클러스터의 VPC를 선택해야함](https://user-images.githubusercontent.com/38099203/119364089-85048580-bce9-11eb-8001-1c20a93b8e36.PNG)

![EFS생성](https://user-images.githubusercontent.com/38099203/119343415-60041880-bcd1-11eb-9c25-1695c858f6aa.PNG)

2. EFS 계정 생성 및 ROLE 바인딩
```
kubectl apply -f efs-sa.yml

apiVersion: v1
kind: ServiceAccount
metadata:
  name: efs-provisioner
  namespace: airbnb


kubectl get ServiceAccount efs-provisioner -n airbnb
NAME              SECRETS   AGE
efs-provisioner   1         9m1s  
  
  
  
kubectl apply -f efs-rbac.yaml

namespace를 반듯이 수정해야함

  
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: efs-provisioner-runner
  namespace: airbnb
rules:
  - apiGroups: [""]
    resources: ["persistentvolumes"]
    verbs: ["get", "list", "watch", "create", "delete"]
  - apiGroups: [""]
    resources: ["persistentvolumeclaims"]
    verbs: ["get", "list", "watch", "update"]
  - apiGroups: ["storage.k8s.io"]
    resources: ["storageclasses"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["events"]
    verbs: ["create", "update", "patch"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: run-efs-provisioner
  namespace: airbnb
subjects:
  - kind: ServiceAccount
    name: efs-provisioner
     # replace with namespace where provisioner is deployed
    namespace: airbnb
roleRef:
  kind: ClusterRole
  name: efs-provisioner-runner
  apiGroup: rbac.authorization.k8s.io
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-efs-provisioner
  namespace: airbnb
rules:
  - apiGroups: [""]
    resources: ["endpoints"]
    verbs: ["get", "list", "watch", "create", "update", "patch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-efs-provisioner
  namespace: airbnb
subjects:
  - kind: ServiceAccount
    name: efs-provisioner
    # replace with namespace where provisioner is deployed
    namespace: airbnb
roleRef:
  kind: Role
  name: leader-locking-efs-provisioner
  apiGroup: rbac.authorization.k8s.io


```

3. EFS Provisioner 배포
```
kubectl apply -f efs-provisioner-deploy.yml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: efs-provisioner
  namespace: airbnb
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: efs-provisioner
  template:
    metadata:
      labels:
        app: efs-provisioner
    spec:
      serviceAccount: efs-provisioner
      containers:
        - name: efs-provisioner
          image: quay.io/external_storage/efs-provisioner:latest
          env:
            - name: FILE_SYSTEM_ID
              value: fs-562f9c36
            - name: AWS_REGION
              value: ap-northeast-2
            - name: PROVISIONER_NAME
              value: my-aws.com/aws-efs
          volumeMounts:
            - name: pv-volume
              mountPath: /persistentvolumes
      volumes:
        - name: pv-volume
          nfs:
            server: fs-562f9c36.efs.ap-northeast-2.amazonaws.com
            path: /


kubectl get Deployment efs-provisioner -n airbnb
NAME              READY   UP-TO-DATE   AVAILABLE   AGE
efs-provisioner   1/1     1            1           11m

```

4. 설치한 Provisioner를 storageclass에 등록
```
kubectl apply -f efs-storageclass.yml


kind: StorageClass
apiVersion: storage.k8s.io/v1
metadata:
  name: aws-efs
  namespace: airbnb
provisioner: my-aws.com/aws-efs


kubectl get sc aws-efs -n airbnb
NAME            PROVISIONER             RECLAIMPOLICY   VOLUMEBINDINGMODE      ALLOWVOLUMEEXPANSION   AGE
aws-efs         my-aws.com/aws-efs      Delete          Immediate              false                  4s
```

5. PVC(PersistentVolumeClaim) 생성
```
kubectl apply -f volume-pvc.yml


apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: aws-efs
  namespace: airbnb
  labels:
    app: test-pvc
spec:
  accessModes:
  - ReadWriteMany
  resources:
    requests:
      storage: 6Ki
  storageClassName: aws-efs
  
  
kubectl get pvc aws-efs -n airbnb
NAME      STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
aws-efs   Bound    pvc-43f6fe12-b9f3-400c-ba20-b357c1639f00   6Ki        RWX            aws-efs        4m44s
```

6. room pod 적용
```
kubectl apply -f deployment.yml
```
![pod with pvc](https://user-images.githubusercontent.com/38099203/119349966-bd9c6300-bcd9-11eb-9f6d-08e4a3ec82f0.PNG)


7. A pod에서 마운트된 경로에 파일을 생성하고 B pod에서 파일을 확인함
```
NAME                              READY   STATUS    RESTARTS   AGE
efs-provisioner-f4f7b5d64-lt7rz   1/1     Running   0          14m
room-5df66d6674-n6b7n             1/1     Running   0          109s
room-5df66d6674-pl25l             1/1     Running   0          109s
siege                             1/1     Running   0          2d1h


kubectl exec -it pod/room-5df66d6674-n6b7n room -n airbnb -- /bin/sh
/ # cd /mnt/aws
/mnt/aws # touch intensive_course_work
```
![a pod에서 파일생성](https://user-images.githubusercontent.com/38099203/119372712-9736f180-bcf2-11eb-8e57-1d6e3f4273a5.PNG)

```
kubectl exec -it pod/room-5df66d6674-pl25l room -n airbnb -- /bin/sh
/ # cd /mnt/aws
/mnt/aws # ls -al
total 8
drwxrws--x    2 root     2000          6144 May 24 15:44 .
drwxr-xr-x    1 root     root            17 May 24 15:42 ..
-rw-r--r--    1 root     2000             0 May 24 15:44 intensive_course_work
```
![b pod에서 파일생성 확인](https://user-images.githubusercontent.com/38099203/119373196-204e2880-bcf3-11eb-88f0-a1e91a89088a.PNG)


- Config Map

1: cofingmap.yml 파일 생성
```
kubectl apply -f cofingmap.yml


apiVersion: v1
kind: ConfigMap
metadata:
  name: airbnb-config
  namespace: airbnb
data:
  # 단일 key-value
  max_reservation_per_person: "10"
  ui_properties_file_name: "user-interface.properties"
```

2. deployment.yml에 적용하기

```
kubectl apply -f deployment.yml


.......
          env:
			# cofingmap에 있는 단일 key-value
            - name: MAX_RESERVATION_PER_PERSION
              valueFrom:
                configMapKeyRef:
                  name: airbnb-config
                  key: max_reservation_per_person
           - name: UI_PROPERTIES_FILE_NAME
              valueFrom:
                configMapKeyRef:
                  name: airbnb-config
                  key: ui_properties_file_name
          volumeMounts:
          - mountPath: "/mnt/aws"
            name: volume
      volumes:
        - name: volume
          persistentVolumeClaim:
            claimName: aws-efs
```
