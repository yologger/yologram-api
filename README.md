# yologram api 서버

## Todo List
- [x] 댓글 기능 구현
- [ ] 조회수 기능 구현
- [ ] Profile 지면의 내 게시글 조회 개선
  - [ ] 옵션1. Infinite scrolling + Cursor-based pagination
  - [ ] 옵션2. 별도 user_board_count 테이블 구성 + Redis cache
- [ ] Token 검증 시 MSA 방식으로 WebClient 사용하도록 개선
- [ ] Webflux + Coroutine 전환 검토 (yologram-api-v2)
- [ ] Fast API 전환 검토 (yologram-api-v3)
- [ ] 테스트 코드 작성
  