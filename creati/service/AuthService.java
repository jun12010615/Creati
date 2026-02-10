package creati.service;

public class AuthService {

    // TODO: DB 붙이면 여기만 바꾸면 됨.
    public boolean login(String id, String pwd) {
        // 더미: 비어있지 않으면 성공 처리
        return id != null && !id.trim().isEmpty() && pwd != null && !pwd.trim().isEmpty();
    }

    public boolean isDuplicateId(String id) {
        // 더미: "admin"은 중복
        return "admin".equalsIgnoreCase(id);
    }
}
