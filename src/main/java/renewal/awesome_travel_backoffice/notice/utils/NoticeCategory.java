package renewal.awesome_travel_backoffice.notice.utils;

public enum NoticeCategory {
    EVENT("이벤트"),
    UPDATE("업데이트"),
    SYSTEM("점검"),
    GUIDE("안내");



    private final String displayName;

    NoticeCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
