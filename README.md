# 프로젝트 이름

<p align="center">
  <br>
  <img width="300px" src="https://github.com/java-is-coffee/FRONT-Groomy_IDE/assets/62227770/579e4274-af17-45a4-ab6f-e568a25446d4"/>
  <br>
</p>

## 프로젝트 소개
초보 개발자가 성장할 수 있는 web-IDE 백엔드 레포지토리
백엔드 dns = http://groomy-ide.duckdns.org:8080/

## ⚙ Stacks

### Development

<img src="https://img.shields.io/badge/spring-000000.svg?style=for-the-badge&logo=spring&logoColor=#6DB33F"/> <img src="https://img.shields.io/badge/Spring Boot-000000.svg?style=for-the-badge&logo=Spring Boot&logoColor=#6DB33F"/> <img src="https://img.shields.io/badge/springsecurity-000000.svg?style=for-the-badge&logo=springsecurity&logoColor=#6DB33F"/> <img src="https://img.shields.io/badge/mysql-FFFFFF.svg?style=for-the-badge&logo=mysql&logoColor=#4479A1"/>

### Environment

<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/intellijidea%20code-007ACC?style=for-the-badge&logo=intellijidea&logoColor=white"> <img src="https://img.shields.io/badge/jirasoftware-FFFFFF.svg?style=for-the-badge&logo=jirasoftware&logoColor=#0052CC"/> <img src="https://img.shields.io/badge/notion-000000.svg?style=for-the-badge&logo=notion&logoColor=#FFFFFF"/>

### Deploy

<img src="https://img.shields.io/badge/Amazon EC2-569A31?style=for-the-badge&logo=Amazon EC2&logoColor=white"> <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">

## 구현 기능

### 로그인
  - 이메일과 비밀번호로 로그인
  - 구글 계정으로 로그인

### 회원가입
  - 이메일, 비밀번호, 이름, 닉네임을 통해 회원가입 

### 메인 페이지
  - 프로젝트 목록
  - 메뉴바 (유저 정보, 프로젝트 탭, 게시판 탭)
  - 새로운 프로젝트 생성 버튼

### IDE
  - 파일 탐색기 구현
  - 코드 에디터 구현
  - 메뉴바 (파일 탐색기, 프로젝트 탭, 게시판 탭)

### 채팅
  - 프로젝트에 참여한 인원들끼리의 채팅 구현

### 게시판
  - 게시글 목록 보기
  - 게시글 작성 하기
  - 게시글 수정 하기
  - 게시글 삭제 하기

## 🤝 Convention

### Branch Naming Convention

```
 - <lable>/<jira issue number>
        |           |           
        |           └─⫸ (Your Issue Number)
        |
        └─⫸ (Docs|Feat|Fix|Refactor|Test)
```

```
< example >
   - Docs/#GI-11 - jira GI-11 기존 코드에 주석을 더하거나 설명 문서를 작성하기 위한 브랜치.
   - Feat/#GI-12 - jira GI-12 이슈에 대한 새로운 기능(feature) 추가를 위한 브랜치.
   - Fix/#GI-13 - jira GI-13 이슈의 버그를 수정하는 작업을 위한 브랜치.
   - Refactor/#GI-14 - jira GI-14 기존 코드의 리팩토링을 진행을 위한 브랜치.
   - Test/#GI-15 - jira GI-15 테스트 진행을 위한 브랜치.
```

### Commit message Convention

```
<type> : <jira issue key> <subject>

<body>

<footer>(생략 가능)
```

```
<Example>
Feat : OW-14 CI workflow 작성

PR 요청 시 빌드를 진행하도록 작성
- PR하는 브랜치가 main, develop 일 때, /back 폴더의 프로젝트를 빌드
```


## 👤 Contributor

| 문경미  | 박상현 |  윤지호   |
| :--------: | :--------: | :------: |
| <img src="https://contrib.rocks/image?repo=M-roaroa/Netflix-Clone" /> |  <img src="https://contrib.rocks/image?repo=cocohodo/react-netflix" /> | <img src="https://contrib.rocks/image?repo=j5i3h8o8/netflix" /> | 
| @M-roaroa  | @cocohodo |  @j5i3h8o8   | 
