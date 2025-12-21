# yologram api 서버

## Architecture

### Infra architecture
![Infra architecture](/assets/images/infra_architecture.drawio.png)
- AWS Route 53
- AWS Elastic Load Balancing (ALB)
- AWS Elastic Beanstalk

### Application architecture

## CI/CD
![CI/CD](/assets/images/cicd.drawio.png)
- GitHub
- GitHub Actions ([.github/workflow/staging.yaml](.github/workflow/staging.yaml))

## Observability
- Grafana Cloud
  - Log: Grafana Loki + loki-logback-appender
  - Trace & APM: OpenTelemetry exporter + Tempo

## Todo List
- [x] 댓글 기능 구현
- [ ] 조회수 기능 구현
- [ ] Profile 지면의 내 게시글 조회 개선
  - [ ] 옵션1. Infinite scrolling + Cursor-based pagination
  - [ ] 옵션2. 별도 user_board_count 테이블 구성 + Redis cache
- [ ] count 관련 로직 redis에 캐싱
- [ ] Token 검증 시 MSA 방식으로 WebClient 사용하도록 개선
- [ ] Webflux + Coroutine 전환 검토 (yologram-api-v2)
- [ ] 테스트 코드 작성
- [ ] 조회수 집계 api → log 엔드포인트로 분리 → kinesis 또는 kafka 기반으로 전환
- [ ] 조회수 중복 검증 로직 개선