package it.unipi.lsmd.gastronomate.dao.exceptions;

import it.unipi.lsmd.gastronomate.dao.exceptions.enums.ErrorTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DAOException extends Exception {
    private ErrorTypeEnum errorType;
    public DAOException(Exception e, ErrorTypeEnum errorType) {
        super(e);
        this.errorType = errorType;
    }
    public DAOException(String message, ErrorTypeEnum errorType) {
        super(message);
        this.errorType = errorType;
    }
    public DAOException(String message, Throwable cause, ErrorTypeEnum errorType) {
        super(message, cause);
        this.errorType = errorType;
    }
}
