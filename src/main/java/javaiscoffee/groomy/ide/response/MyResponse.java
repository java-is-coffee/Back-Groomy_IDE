package javaiscoffee.groomy.ide.response;


import lombok.Getter;
import lombok.Setter;

//응답을 위한 커스텀 리스폰스 클래스
// 사용 안하기로 결정
@Getter @Setter
public class MyResponse<T> {
    private Status status;
    private T data;

    //응답 데이터가 있는 경우
    public MyResponse(Status status, T data) {
        this.status = status;
        this.data = data;
    }

    //status만 보내주는 경우
    public MyResponse(Status status) {
        this.status = status;
    }
}
