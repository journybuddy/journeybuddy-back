package journeybuddy.spring.apiPayload.exception.handler;

import journeybuddy.spring.apiPayload.code.BaseErrorCode;
import journeybuddy.spring.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {
    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}