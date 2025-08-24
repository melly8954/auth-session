# Auth-Session

Spring Security 기반 **세션 인증** 학습/실습 프로젝트입니다.  
커스텀 AuthenticationProvider, 세션 정책, OAuth2 로그인까지 직접 구현/분석했습니다.  

자세한 인증 흐름 및 코드 해설은 [Notion 문서](https://northern-mongoose-47b.notion.site/Auth-Project-257d351413c08020a87fe17fbf4dcae3?pvs=74) 에 정리되어 있습니다.

---

**Backend & Runtime**
- Java 17
- Spring Boot 3.4.9
- Spring Security
- Spring Data JPA
- MySQL 8.4
- Gradle

**Development Tools & Testing**
- Lombok
- JUnit 5
- AssertJ
- Spring Security Test
- p6spy (SQL 로그 확인용)
---

**주요 기능**
- 회원가입 / 로그인 / 로그아웃
  - 세션 기반 인증 (`UsernamePasswordAuthenticationToken`)
  - SecurityContext + HttpSession 저장
- OAuth2 로그인: Google, Kakao 등 소셜 계정 연동 (환경변수로 클라이언트 ID/Secret 관리)
- 인증/인가 실패 예외 처리 (`401`, `403`)
- 테스트 커버리지
  - 회원가입, 로그인, 로그아웃, 소셜 로그인<br><br>
---

**데이터베이스 초기 설정**
```sql
create database auth_session character set utf8mb4 collate utf8mb4_general_ci;
create user `psw_auth`@`%` identified by 'auth1234!';
grant all privileges on auth_session.* to `psw_auth`@`%` with grant option;
flush privileges;
