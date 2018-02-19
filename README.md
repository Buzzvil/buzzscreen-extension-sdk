## buzzscreen-sdk 배포 방법
mediation은 buzz-android-library 에서 관리 및 배포한다.
mediation을 업데이트하면 버즈스크린 sdk 도 맞춰서 업데이트해줘야 싱크가 맞는다.

1. 버전 변경 : 최상위 build.gradle 에서 buzzscreenVersionName 과 buzzscreenVersionCode 수정.
2. bintray 업로드 : ./gradlew -Puser=<my_user> -Pkey=<my_key> :buzzscreen:bintrayUpload :buzzscreen-multi-process:bintrayUpload
> <my_user>, <my_key> 는 bintray 가입후 자신의 계정 정보에서 확인.
3. bintray 업로드 확인 : https://bintray.com/buzzvil/buzzscreen
4. publish : 업로드된 상태는 아직 비공개 상태이기때문에 publish 선택을 통해 public 공개. 그 전에 아래 방법을 통해 테스트 가능.

#### unpublished buzzscreen sdk 적용하여 테스트.
기존 path 외에 인증 정보(<my_user>, <my_key>) 설정 필요.
```
maven {
    url "https://dl.bintray.com/buzzvil/buzzscreen/"
    credentials {
        username = "<my_user>"
        password = "<my_key>"
    }
}
```


## 버즈스크린 sdk 에 ContentProvider 적용
 
 - 목적 : 하나의 디바이스에서, 현재 버즈스크린이 활성화된 모든 앱 검색을 위함.
 => 현재 실행중인 서비스를 검색하여 어느정도 가능하지만 서비스가 잠시 중단된경우 검색이 안되는 문제 발생. 이를 해결하기위해 각 버즈스크린이 연동된 앱의 설정값을 읽어올 필요가 있음.

 - 왜 ContentProvider 인가? 
 	=> 잠금화면 활성화 설정값을 안드로이드에서 기본적으로 제공하는 key-value 스토리지인 SharedPreference 사용중.
 	과거에는 MODE_WORLD_READABLE 플래그를 제공하여 다른 앱에서 접근이 가능하였으나 deprecated 되었고, 안드로이드 7.0부터는 SecurityException 발생.
 	=> 안드로이드 가이드에서 대신 ContentProvider 사용 권장. 그외 Broadcast, Service 를 통한 IPC 등 생각할 수 있으나 가장 간단하고 가이드에서 추천하는 방식으로 고고.

- ContentProvider 사용 방법 : 하나의 앱에서 ContentProvider 를 구현하면, 다른 앱에서는 ContentResolver 를 통해 접근하고, query 를 통해 데이터 조회.

- 이슈
	1. 보안 : 버즈스크린에 적용시에는 권한 설정 및 마켓 서명을 통일을 할 수 없기때문에 퍼블릭하게 공개됨. 누구나 uri를 통해 데이터 접근 가능한 정보.
	=> 잠금화면 활성화/비활성화 여부만 조회하도록 함. 어차피 실행중인 서비스 조회를 통해 버즈스크린이 활성화된 앱 조회는 다른 앱에서 이미 알 수 있는 정보.
	추후 민감한 정보는 버즈스크린 ContentProvider 를 통해 제공하면 안됨. 추후 민감한 정보를 공유하고 싶으면 서버 인증을 통해 하도록 해야함.
	2. 라이프사이클 주의 : 안드로이드에서는 항상 프로세스 실행시 Application의 onCreate 이후에 모든 컴포넌트가 실행되지만 ContentProvider 만 예외.
	=> ContentProvider 의 모든 메소드는 Application의 onCreate 보다 먼저 호출될 수 있다.
