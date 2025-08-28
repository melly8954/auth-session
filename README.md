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
  - `SecurityContext` + `HttpSession` 저장
  - 로그아웃 시 `SecurityContext` 초기화 + 세션 무효화 + `JSESSIONID` 쿠키 삭제
- OAuth2 로그인: Google 소셜 계정 연동 (환경변수로 클라이언트 ID/Secret 관리)
- 인증/인가 실패 예외 처리 (`401`, `403`)
- 테스트 커버리지
  - 회원가입, 로그인, 로그아웃, 소셜 로그인<br><br>
---
## 🚀 프로젝트 실행 방법

**1️⃣ 환경 변수 설정**
프로젝트 루트에 `.env` 파일 생성 후, 필요한 환경 변수를 설정합니다.
```bash
# Google OAuth2 설정
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# MySQL 설정
MYSQL_ROOT_PASSWORD=your_mysql_root_password
DB_NAME=your_database_name
DB_USER=your_database_user
DB_PASSWORD=your_database_password

# 로컬 개발용 포트
MYSQL_LOCAL_PORT=3306
REDIS_LOCAL_PORT=6379

# 도커 환경용 포트
MYSQL_HOST_PORT=3307
REDIS_HOST_PORT=6380
```

**2️⃣ Gradle 빌드**
```bash
# Windows(CMD) -> gradlew.bat build
./gradlew build
```

**3️⃣ Docker Compose로 컨테이너 실행**
```bash
docker-compose --env-file ../.env -p auth-session-app up -d
```
<hr>
