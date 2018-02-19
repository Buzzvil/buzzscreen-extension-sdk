# BuzzScreen Extension SDK Guide

- 버즈스크린을 안드로이드 애플리케이션에 연동하기 위한 라이브러리인 [BuzzScreen SDK](https://github.com/Buzzvil/buzzscreen-sdk-publisher)의 동작을 돕는 익스텐션 SDK로, 잠금화면 전용 애플리케이션에서 **직접 로그인을 구현하지 않고 기존 미디어앱의 유저 정보를 사용하기 위한** 목적입니다.
    > 주의: 잠금화면 전용 애플리케이션에서 직접 로그인 기능을 구현하는 경우는 [BuzzScreen SDK 가이드](https://github.com/Buzzvil/buzzscreen-sdk-publisher/wiki)를 참고해 주세요.<br>
    주의: 새로 BuzzScreen을 연동하는 퍼블리셔가 아니라 기존에 미디어앱 내부에 BuzzScreen SDK를 연동했던 퍼블리셔들은 [마이그레이션 가이드](https://github.com/Buzzvil/buzzscreen-sdk-publisher-migration/wiki)를 참고해 주세요.
- 안드로이드 버전 지원 : Android 4.0.3(API Level 15) 이상
- 이하 로그인 기능이 포함된 미디어앱을 M(Main)앱으로 지칭하고, 별도 잠금화면 전용 애플리케이션을 L(LockScreen)앱으로 지칭합니다. <br>
    > - M앱내 연동: BuzzScreenHost SDK<br>
    > - L앱내 연동: BuzzScreenClient SDK + [BuzzScreen SDK](https://github.com/Buzzvil/buzzscreen-sdk-publisher/wiki)
- M앱과 L앱에 각 SDK를 연동하면 M앱에서 설정한 사용자 정보를 L앱으로 동기화하여 M앱 사용자가 L앱에서 버즈스크린을 사용하는데 필요한 사용자 정보를 가져올 수 있습니다.
    > 주의: 본 Extension은 M앱의 사용자 정보를 L앱에서 사용할 수 있도록 돕는 역할이며, 실제 잠금화면의 구동은 본 Extension과 별도의 SDK인 BuzzScreen SDK가 담당합니다. BuzzScreen SDK 연동을 별도로 확인하고 싶은 경우 [BuzzScreen SDK for Android 가이드](https://github.com/Buzzvil/buzzscreen-sdk-publisher/wiki)를 참조하세요.
- 익스텐션 동작을 위해 M앱과 L앱은 서로 정보를 주고 받는데, 다른 앱에서의 접근을 막기 위해 반드시 **동일한 서명으로 APK 생성**해야 합니다.
    > 다른 앱에서의 접근을 막기 위해 안드로이드의 [protectionLevel="signature"](https://developer.android.com/guide/topics/manifest/permission-element.html#plevel) 권한 사용을 위함
- **익스텐션 연동시 주의** : 익스텐션 연동 작업은 **반드시 미리 버즈빌 BD 팀과 협의 후 진행** 해야하며, 가이드 내용을 모두 반영한 M앱과 L앱의 APK 파일들은 마켓에 업로드하기 전에 버즈빌 BD 팀에 전달하여 **반드시 리뷰를 마친 후 마켓에 업로드** 해야 합니다.
- 익스텐션 연동 가이드의 모든 작업은 M앱 샘플인 `sample_host`와 L앱 샘플인 `sample_client`에서 확인할 수 있습니다.
    > 샘플 앱은 다음 단계를 통해 실행해볼 수 있습니다.
    > 1. GitHub에서 전체 프로젝트를 클론 혹은 다운로드하고 압축을 풉니다.
    > 2. Android Studio 3 이상에서 File>New>Import Project에서 해당 소스를 엽니다.
    > 3. `sample_client` 모듈의 AndroidManifest 상의 `android:value="<app_license>"`, `android:value="<plist>"` 를 발급받은 값으로 교체해주세요. 발급 값이 없이 샘플 앱만 빌드하고 싶을 경우 값을 "" 처럼 비워도 됩니다. 변환 후 Gradle project sync를 다시 해주세요.
    > 4. 필요한 모듈을 빌드합니다.
- M앱과 L앱 모두 익스텐션 적용을 위한 추가 연동 작업이 있으며 다음 링크들을 통해 확인할 수 있습니다. 

#### [M앱에 BuzzScreenHost 연동](https://github.com/Buzzvil/buzzscreen-sdk-extension-publisher/wiki/BUZZSCREENHOST-M)
#### [L앱에 BuzzScreenClient 연동](https://github.com/Buzzvil/buzzscreen-sdk-extension-publisher/wiki/BUZZSCREENCLIENT-L)

#### BuzzScreen SDK & BuzzScreen Extension SDK 작업 흐름
![Task Flow](https://github.com/Buzzvil/buzzscreen-sdk-extension-publisher/wiki/extension_workflow.png)