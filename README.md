# 심화 캡스톤 디자인 'WWW' 👋

### 🌠 캡스톤 구성원 🙇‍♀ 🙇‍♂  
김하경, 김민경, 김연화, 예지민

## 🌠 프로젝트 기능  
1. 관리자
- 상품등록 (태그, 카테고리 등록 가능)
- 상품관리

2. 사용자/관리자(공통)
- 메인 상품 리스트 노출
- 장바구니
- 주문/선물 기능
- 메인에 있는 카테고리 <a>태그 클릭 시 통합 검색 페이지로 이동
  태그, 카테고리 검색 가능
- 구매/선물 이력 확인 가능
- 등등..

## 🌠 세팅 사항  
***❗ Setting.md 참고 ❗***  
footer.html >> src/main/resource/fragments 

## 🌠 프로젝트 구조

### Back-end
- Entity : 테이블
- Dto : 엔티티에 필요한 정보들을 잠시 담는 곳
- Contoller : html과 연결 
- Service : 데이터 저장, 수정, 조회 등 서비스 하는 곳
- Repository : sql query문 활용하는 곳

### Front-end
- src/main/resource/templates
- layout : 공통 레이아웃
- fragment : 공통 header, footer 등.. 
- 나머지 package에 해당 html이 들어 있음.


## 🧶 프로젝트 개발환경  
### Language
- `Java`: OpenJDK 11

### IDE
- `STS`
- `Intellij`: Community 또는 Ultimate

### Framework 
- `Spring boot 2.5.5`

### Build
- `Gradle 7.2`

### Test 도구
- `Junit 5`
