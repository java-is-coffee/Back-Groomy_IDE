package Javaiscoffee.Groomy.IDE.security;

// /api/member/refresh 요청을 보낼 때 refresh Token을 담아 보내는 DTO
public class RefreshTokenDto {
    private Data data;

    public static class Data {
        private String refreshToken;

        // Getters and Setters
        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    // Getters and Setters
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}

