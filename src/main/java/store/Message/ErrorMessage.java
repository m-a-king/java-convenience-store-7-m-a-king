package store.Message;

public enum ErrorMessage {

    INVALID_FORMAT("올바르지 않은 형식으로 입력했습니다."),
    NON_EXISTENT_PRODUCT("존재하지 않는 상품입니다."),
    EXCEEDS_STOCK("재고 수량을 초과하여 구매할 수 없습니다."),
    GENERIC_ERROR("잘못된 입력입니다."),
    FILE_ERROR("잘못된 데이터 파일입니다:");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return String.format("[ERROR] %s 다시 입력해 주세요.", message);
    }

    public String getMessage(String detail) {
        return String.format("[ERROR] %s : %s", message, detail);
    }
}