자바스크립트 관련 라이브러리 추가를 위해 npm을 사용했습니다.<br>

**JS library list** <br>
npm version - 6.14.4 <br>
1. @yaireo/tagify<br>
2. summernote<br>
3. @fontawesome-free<br>
4. jdention<br>
5. bootstrap<br>
6. jquery & jquery-form<br>
7. moment<br>
8. tooltipster<br>


프론트 설계 및 코드는 bootstrap을 사용했습니다.<br>
백엔드 설계 및 코드는 spring MVC를 기반으로 한 프레임 워크들을 사용했습니다.<br>

사용된 프레임워크<br>
1. Spring Web MVC Ver.5.2.2
2. Spring Boot Ver.2.2.6
3. Spring Data API(JPA)
4. Spring Security

git에는 npm으로 추가한 라이브러리가 첨부되지 않게 했습니다.

Video 영상 데이터들은 프로젝트 내 /static/video에 위치하게 작성하였습니다.

**_참고한 사이트_**<br>
https://www.inflearn.com/ 인프런

**발견된 버그들**
영상 업로드 후 바로 재생시 경로 못찾는 버그 (로그아웃 후 5분 경과시 정상 동작)
영상 업로드 후 강의 상세 페이지 접근시 값 변경을 인지 못함(재 로그인시 정상 동작)
테스트용 카카오페이 결제 시스템 결제 오류(카카오페이 API 오류)