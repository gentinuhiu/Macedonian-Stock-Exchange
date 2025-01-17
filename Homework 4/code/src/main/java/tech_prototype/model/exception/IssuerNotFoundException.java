package tech_prototype.model.exception;

public class IssuerNotFoundException extends RuntimeException{
    public IssuerNotFoundException(Long id) {
        super(id + " Not Found");
    }
}
