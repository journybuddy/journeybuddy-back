package journeybuddy.spring.apiPayload.code.status;

import journeybuddy.spring.apiPayload.code.BaseCode;
import journeybuddy.spring.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {


    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    _NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON204", "콘텐츠가 없습니다. 요청이 성공적으로 처리되었습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
