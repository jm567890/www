### 프로젝트 셋팅
1. application-properties
   - Hibernate 초기화 전략 설정
      spring.jpa.hibernate.ddl-auto=***create***  
    'create' 또는 'validate' 만 사용  
    ***처음엔 create***, 테이블 생성 이후 validate로 수정  
    create 유지 시 테이블 초기화 다시 생성됨 -> 번거로움  
 
  - database 설정  
    mariadb 설정이 아닌 mysql 로컬 데이터 사용 바람
  
  
