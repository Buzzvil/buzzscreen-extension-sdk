# Publisher CPA Campaign guide

- 퍼블리셔 자체 액션형 광고를 라이브하기 위한 가이드.
- 어드민에서의 광고 Extra 필드 등록 및 앱에서의 UI 변경 작업이 필요하다.
- 어드민에서는 액션형 광고를 참여했을 때 지급할 리워드의 값과, 해당 광고의 랜딩 아이콘(슬라이더의 좌측 아이콘)으로 쓸 이미지의 이름을 입력한다.
- 앱에서는 액션형 광고가 잠금화면에 보일 때 지정된 리워드의 값이 잠금화면의 랜딩 포인트(슬라이더의 좌측 포인트)에 반영되어 나타나도록 하고, 어드민에서 전달받은 이름에 매핑된 랜딩 아이콘으로 변경해야 한다.
- 랜딩 아이콘 이미지는 앱 내 resource 디렉토리 내에 저장이 되어 있어야 하며, 어드민에 입력된 값이 앱의 어느 이미지를 의미하는지에 대해 어드민과 앱 사이에 이름에 대한 약속이 되어 있어야 한다.

> 어드민에서 아이콘 이름에 INSTALL 이라고 입력하면 해당 광고는 앱 내의 res/drawable/install.xml 파일을 랜딩 아이콘으로 사용하도록 하는 식의 사전 약속이 필요하다.

## Admin
자체 액션형 광고를 운영하는 퍼블리셔에게는 어드민 내 Ads 탭에서 광고를 생성하는 페이지에 다음의 두 가지 Extra 필드가 추가된다.
- `Extra(action_points)` : 해당 액션형 광고를 참여했을 때 유저에게 지급할 리워드의 값을 입력한다. 입력란은 text box로 이루어져 있어서 숫자를 직접 입력해야 한다.
- `Extra(ic_type)` : 해당 액션형 광고가 화면에 나갈 때 사용할 랜딩 아이콘을 지정한다. 직접 이미지를 등록하는 것이 아니라 기존에 앱과 약속된 이미지의 이름을 지정하여 앱에서 이 이름을 가지고 앱 내 resource 에 저장된 이미지를 불러와 사용할 수 있게 해야 한다. 입력란은 기본적으로 text box로 이루어져 있으나, 이미지의 전체 목록 및 광고 생성 시 default로 설정될 이미지의 이름을 버즈스크린 담당자에게 전달할 경우 select box로 변경이 가능하다.

    > `ic_type list : LINK(default), INSTALL, EXECUTE` 로 지정한 경우. 어드민에서 Create ad 페이지에 들어오면 `Extra(ic_type)` 필드의 입력창에 LINK가 default로 선택된 3개의 아이템을 리스트로 가진 select box가 나오게 된다.

## App
퍼블리셔 자체 액션형 광고는 서버로부터 할당 받아 Campaign 객체로 생성될 때 `extra` 필드에 추가 정보가 담긴다. 이 extra 필드에는 어드민에서 설정한 값이 반영된다. 이 필드에 설정된 값에 따라 기존에 BaseLockerActivity를 상속받아 만든 잠금화면 액티비티 내의 onCurrentCampaignUpdated(Campaign campaign) 메소드에서 UI 변경 작업을 추가해야 한다.(참고 : [잠금화면 커스터마이징](https://github.com/Buzzvil/buzzscreen-sdk-publisher/blob/master/ADVANCED-USAGE.md#잠금화면-커스터마이징)) extra 필드와 관련된 Campaign 클래스의 메소드는 다음과 같이 정의되어 있다. 

- `String getExtra(String key)` : extra 필드는 Map<String, String> 으로 이루어져 있으며, 이 메소드를 통해 extra 필드 내 key 값을 인자로 받아서 여기에 매핑된 value 값을 얻을 수 있다. key 는 다음과 같이 두 종류가 있다.

    - `action_points` : 액션형 광고를 참여할 경우 지급할 리워드 값을 의미한다. 이 값은 어드민의 `Extra(action_points)` 에 입력한 값과 동일하다. 액션형 광고가 잠금화면에 나타났을 때 이 값이 랜딩 포인트에 반영되도록 처리해야 한다. 이 때, 유저에게 지급하는 `base_point` 를 반영해야 하기 때문에 랜딩 포인트의 값을 `action_points` 값으로만 지정하면 안되고, 반드시 `getLandingPoints()` 를 통해 얻은 `base_point` 와 더하여 랜딩 포인트로 지정해야 한다.

    > 참고 : Campaign 클래스의 getLandingPoints() 메소드는 기존 `base_point` 적립 내역을 확인하여 현재 시점에 `base_point`를 적립받을수 있는지 여부를 판단하여 0 혹은 2 포인트를 리턴한다.

    > 주의 : getExtra(“action_points”) 의 리턴 값은 String이므로 반드시 int 로 변환해야 한다.
    
    - `ic_type` : 액션형 광고가 잠금화면에 나타났을 때 변경해야 할 좌측 아이콘의 이미지 이름을 의미한다. 이 값은 어드민의 `Extra(ic_type)` 에 입력된 값과 동일하다. 사전 약속된 대로 해당 이름에 매핑된 이미지를 resource 에서 불러와 랜딩 아이콘으로 등록하는 로직을 추가해야 한다.

#### 코드 예제
아래의 예제에서는 앱의 res/drawable/ 내에 link, install, execute 라는 이름의 xml 파일이 존재한다고 가정한다.

```Java
    @Override
    protected void onCurrentCampaignUpdated(Campaign campaign) {
        // 현재 보여지고 있는 캠페인이 업데이트 될때 호출된다.

        int landingPoints = campaign.getLandingPoints();
        int unlockPoints = campaign.getUnlockPoints();

        String actionPoints = campaign.getExtra("action_points");
        if (actionPoints != null) {
            // 퍼블리셔 자체 캠페인 - action_points 를 기존 landingPoints 에 더한다.
            landingPoints += Integer.valueOf(actionPoints);
        }
        String icType = campaign.getExtra("ic_type");
        if (icType != null) {
            // 퍼블리셔 자체 캠페인 - 전달된 아이콘 이름에 맞는 이미지를 slider 의 LeftIcon으로 등록한다.
            if (icType.equals("LINK")) {
                slider.setLeftIcon(getResources().getDrawable(R.drawable.link));
            } else if (icType.equals("INSTALL")) {
                slider.setLeftIcon(getResources().getDrawable(R.drawable.install));
            } else if (icType.equals("EXECUTE")) {
                slider.setLeftIcon(getResources().getDrawable(R.drawable.execute));
            }
        } else {
            // Set Default Image. (suppose locker_landing was the default image)
            slider.setLeftIcon(getResources().getDrawable(R.drawable.locker_landing));
        }

        if (landingPoints > 0) {
            slider.setLeftText(String.format("+ %d", landingPoints));
        } else {
            slider.setLeftText("");
        }

        if (unlockPoints > 0) {
            slider.setRightText(String.format("+ %d", unlockPoints));
        } else {
            slider.setRightText("");
        }
    }
```
