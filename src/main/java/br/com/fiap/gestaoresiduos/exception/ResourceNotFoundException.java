package br.com.fiap.gestaoresiduos.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }

    public ResourceNotFoundException(String recurso, Object id) {
        super(recurso + " com id " + id + " nao encontrado");
    }
}
