package it.unipi.lsmd.gastronomate.service.exceptions;

import it.unipi.lsmd.gastronomate.service.exceptions.enums.BusinessTypeErrorsEnum;
import lombok.Getter;

public class BusinessException extends Exception{
    @Getter
    private BusinessTypeErrorsEnum errorType;
    public BusinessException(String message, BusinessTypeErrorsEnum errorType){
        super(message);
        this.errorType = errorType;
    }
    public BusinessException(Exception e, BusinessTypeErrorsEnum errorType){
        super(e);
        this.errorType = errorType;
    }

}
