# WAS 서버 문서

> 프로젝트명: golden_shielders  
> 작성일: 2026-04-16

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [프로젝트 구조](#3-프로젝트-구조)
4. [환경 설정](#4-환경-설정)
5. [DB 설계](#5-db-설계)
6. [API 명세](#6-api-명세)
7. [인증 및 보안](#7-인증-및-보안)
8. [파일 업로드](#8-파일-업로드)
9. [에러 처리](#9-에러-처리)
10. [알려진 취약점](#10-알려진-취약점)

---

## 1. 프로젝트 개요

게시판 서비스의 비즈니스 로직을 구현함과 동시에 데이터베이스에 저장된 정보를 제공하는 기본적인 API 서버이다.  
프로젝트 목적에 맞게, 기본적인 인증/인가(JWT Token) 외에 다양한 취약점을 노출시켜 공격자가 쉽게 실습할 수 있게 구현하였다.

| 항목 | 내용 |
|------|------|
| 프로젝트명 | golden_shielders |
| 베이스 URL | `http://192.168.1.105/api/v1` |
| 인증 방식 | JWT (Bearer Token) |
| 파일 저장 | 스토리지 방식 (WAS 서버 OS에 저장) |

---

## 2. 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.13 |
| Security | Spring Security + JWT |
| DB | MySQL 8.0.45 |
| DB Access | JdbcTemplate |
| 파일 저장 | 스토리지 |

---

## 3. 프로젝트 구조

```
com.golden_shielders.server
├── ServerApplication.java          # 진입점
├── config/
│   ├── SecurityConfig.java         # Spring Security 설정
│   ├── JwtFilter.java              # JWT 인증 필터
│   ├── JwtUtil.java                # JWT 생성/검증 유틸
│   └── GlobalExceptionHandler.java # 전역 예외 처리
├── controller/
│   └── MainController.java         # REST API 엔드포인트
├── service/
│   ├── UserService.java
│   ├── PostService.java
│   └── LocalStorageService.java
├── repository/
│   ├── UserRepository.java
│   ├── PostRepository.java
│   └── LocalStorageRepository.java
├── entity/
│   ├── WebSiteUser.java
│   ├── Post.java
│   └── UploadFile.java
└── Dto/
    ├── LoginDTO.java / LoginResponse.java
    ├── PostDTO.java / PostSummary.java
    └── FileDTO.java
```

---

## 4. 환경 설정

### application.properties / yml

```properties
# JWT 설정
jwt.secret=<시크릿키>
jwt.expiration=<만료시간(ms)>

# 파일 업로드 경로
upload.local.dir=<업로드 디렉토리 경로>

# DB 설정
spring.datasource.url=jdbc:mysql://<host>:<port>/<db명>
spring.datasource.username=<유저명>
spring.datasource.password=<비밀번호>
```

---

## 5. DB 설계

### 5-1. web_site_user

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | INT (PK) | 유저 고유 ID (Incremental) |
| user_name | VARCHAR(50) | 로그인 아이디 |
| pw | VARCHAR(255) | 비밀번호 (**평문** 저장) |
| role | VARCHAR(20) | 권한 (예: ADMIN, USER) |

### 5-2. post

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | INT (PK) | 게시글 고유 ID |
| title | VARCHAR | 제목 |
| content | TEXT | 내용 |
| author_name | VARCHAR | 작성자 user_name |

### 5-3. upload_file

| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | INT (PK) | 파일 고유 ID |
| post_id | INT (FK, 미설정) | 연결된 게시글 ID |
| original_name | VARCHAR(255) | 원본 파일명 |
| stored_name | VARCHAR(255) | 저장된 파일명 (UUID) |
| file_path | VARCHAR(255) | 서버 내 절대 경로 |

---

## 6. API 명세

> 🔓 공개: 토큰 없이 접근 가능  
> 🔒 인증 필요: `Authorization: Bearer <token>` 헤더 필요

### 6-1. 로그인

| 항목 | 내용 |
|------|------|
| URL | `POST /api/v1/login` |
| 인증 | 🔓 공개 |
| Request Body | `{ "userName": "admin", "password": "1234" }` |
| Response 200 | JWT 토큰 문자열 |
| Response 401 | 로그인 실패 |

### 6-2. 토큰 검증

| 항목 | 내용 |
|------|------|
| URL | `POST /api/v1/token` |
| 인증 | 🔒 인증 필요 |
| Response 200 | Valid Token |
| Response 401 | Invalid Token |

### 6-3. 게시글 목록 조회

| 항목 | 내용 |
|------|------|
| URL | `GET /api/v1/posts` |
| 인증 | 🔓 공개 |
| Query Params | page(기본 0), size(기본 10), sort(기본 id) |
| Response | `[{"id":1,"title":"제목","authorName":"admin"}]` |

### 6-4. 게시글 상세 조회

| 항목 | 내용 |
|------|------|
| URL | `GET /api/v1/posts/{id}` |
| 인증 | 🔓 공개 |
| Response | id, title, content, authorName, attachments[] |

### 6-5. 게시글 생성

| 항목 | 내용 |
|------|------|
| URL | `POST /api/v1/posts` |
| 인증 | 🔒 인증 필요 |
| Content-Type | multipart/form-data |
| Params | title(필수), content(필수), files(선택, 복수 가능) |
| Response 200 | 생성된 게시글 ID (Integer) |

### 6-6. 게시글 수정

| 항목 | 내용 |
|------|------|
| URL | `PUT /api/v1/posts/{id}` |
| 인증 | 🔒 인증 필요 (본인만) |
| Request Body | `{"title": "수정된 제목", "content": "수정된 내용"}` |
| Response 200 | 수정된 Post 객체 |

### 6-7. 게시글 삭제

| 항목 | 내용 |
|------|------|
| URL | `DELETE /api/v1/posts/{id}` |
| 인증 | 🔒 인증 필요 (본인만) |
| Response | 204 No Content |

### 6-8. 파일 다운로드

| 항목 | 내용 |
|------|------|
| URL | `GET /api/v1/file` |
| 인증 | 🔓 공개 |
| Query Params | filePath (서버 내 파일 절대 경로) |
| Response 200 | 파일 바이너리 (application/octet-stream) |
| Response 404 | 파일 없음 |

---

## 7. 인증 및 보안

### JWT 흐름

| 단계 | 설명 |
|------|------|
| 1. 로그인 요청 | `POST /api/v1/login` 에 userName, password 전송 |
| 2. 토큰 발급 | DB에서 유저 조회 후 비밀번호 일치 시 JWT 반환 |
| 3. API 요청 | `Authorization: Bearer <token>` 헤더에 포함하여 요청 |
| 4. 필터 검증 | JwtFilter에서 토큰 파싱 후 SecurityContext에 인증 정보 저장 |

### 공개 엔드포인트 (토큰 불필요)

| 엔드포인트 | 메서드 |
|-----------|--------|
| /api/v1/login | POST |
| /api/v1/posts | GET |
| /api/v1/posts/** | GET |
| /api/v1/file | GET |
| /swagger-ui/** | GET |

---

## 8. 파일 업로드

| 항목 | 내용 |
|------|------|
| 저장 방식 | 로컬 디스크 (`upload.local.dir` 경로) |
| 저장 파일명 | UUID_원본파일명 |
| DB 기록 | 원본명, 저장명, 절대경로 저장 |
| 다운로드 | `/api/v1/file?filePath=<경로>` |

---

## 9. 에러 처리

`GlobalExceptionHandler`에서 컨트롤러 레이어의 모든 예외를 처리한다.

### 에러 응답 형식

```json
{
  "status": "500",
  "exception": "java.lang.RuntimeException",
  "message": "에러 메시지",
  "cause": "원인",
  "trace": "스택트레이스"
}
```

> ⚠️ 스택트레이스가 클라이언트에 노출되므로 운영 환경에서는 반드시 제거 필요

---

## 10. 알려진 취약점

| 번호 | 위치 | 취약점 | 위험도 |
|------|------|--------|--------|
| 1 | PostRepository | SQL Injection (sort, id 직접 삽입) | 🔴 높음 |
| 2 | UserRepository | SQL Injection (userName 직접 삽입) | 🔴 높음 |
| 3 | LocalStorageRepository | SQL Injection (파일명 직접 삽입) | 🔴 높음 |
| 4 | MainController /file | Path Traversal (filePath 검증 없음) | 🔴 높음 |
| 5 | UserService | 비밀번호 평문 저장 및 비교 | 🟠 중간 |
| 6 | GlobalExceptionHandler | 스택트레이스 클라이언트 노출 | 🟠 중간 |
| 7 | JwtUtil | Deprecated API 사용 (HS256 signWith) | 🟡 낮음 |

> ※ SQL Injection 방지를 위해 JdbcTemplate의 PreparedStatement (`?` 바인딩) 사용을 권장합니다.
