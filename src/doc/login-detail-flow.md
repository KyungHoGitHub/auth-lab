### 로그인(auth/login) Flow

```mermaid
sequenceDiagram
    participant Client
    participant AuthController
    participant LoginService
    participant LoginStratiges
    participant DB

    Client ->> AuthController : 로그인 요청 (ID/PW, Google ...)
    AuthController-->AuthController: LoginType 에 따른 데이터 처리 및 생성
    AuthController ->> LoginService : login ( ) 호출
    LoginService --> LoginService : 로그인 타입에 해당하는 클래스 호출 처리
    LoginService ->> +LoginStratiges : 1. IdPasswordStrage 호출
    LoginStratiges --> LoginStratiges : 인증 및 토큰 발급
    LoginStratiges -->> -LoginService: AuthoRespone 반환
    LoginService ->> +LoginStratiges : 2. GoogleLoginStrategy 호출
    LoginStratiges ->> DB : 사용자 조회 (email)
    DB --> +DB : 사용자 조회
    DB --> -LoginStratiges : 사용자 정보 반환
    LoginStratiges --> LoginStratiges : 구글 토큰 인증 요청 토큰 발급
    LoginStratiges -->> -LoginService: AuthoRespone 반환
    LoginService -->> AuthController : 결과반환
    AuthController -->> Client :결과반환