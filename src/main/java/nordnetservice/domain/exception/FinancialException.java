package nordnetservice.domain.exception;

public class FinancialException extends  RuntimeException {
    private static final long serialVersionUID = 1L;

    public FinancialException() {
        super();
    }

    public FinancialException(String msg) {
        super(msg);
    }
}

