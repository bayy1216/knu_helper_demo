# KnuHelperDemo
## 📦 프로젝트 구조

```plaintext
KnuHelperDemo/
│
├── app/               # 애플리케이션 진입점 (MainActivity, AppState 등)
│
├── core/              # 핵심 유틸 및 디자인 시스템 관리
│   ├── design/        # 디자인 시스템 (테마, 색상, 타이포그래피, 크기 등)
│   ├── navigation/    # 앱 내 Navigation 대상 클래스 관리
│   └── system/        # 안드로이드 및 각종 유틸 (페이징, UI 상태 등)
│
├── data/              # 외부 의존성 계층 (API 호출, DB 등)
│
└── feature/           # 개별 UI 기능(화면)을 담당하는 모듈
```