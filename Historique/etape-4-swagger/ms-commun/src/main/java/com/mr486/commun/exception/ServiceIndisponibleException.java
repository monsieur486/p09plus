package com.mr486.commun.exception;

public class ServiceIndisponibleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Le service %s est momentanément indisponible.";

    private final String serviceAppele;

    public ServiceIndisponibleException(String serviceAppele, Throwable cause) {
        super(String.format(MESSAGE, serviceAppele), cause);
        this.serviceAppele = serviceAppele;
    }

    public String getServiceAppele() {
        return serviceAppele;
    }
}
