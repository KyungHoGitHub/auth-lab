```mermaid
sequenceDiagram
    participant Client
    participant AuthServer(인증)
    participant ResourceServer

    Client->>AuthServer(인증): 로그인 요청 (ID/PW or OAuth2)
    AuthServer(인증)-->>Client: Authorization Code 발급
    Client->>AuthServer(인증): Access Token 요청
    AuthServer(인증)-->>Client: Access Token 발급
    Client->>ResourceServer: API 요청 (Access Token 포함)
    ResourceServer-->>Client: 응답 데이터
