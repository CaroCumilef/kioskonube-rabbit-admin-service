package cl.kioskonube.rabbitadmin.dto;

public final class ValidationRules {
    public static final String NAME_REGEX = "^(?!amq\\.)[a-zA-Z0-9._-]{3,100}$";
    public static final String NAME_MESSAGE =
            "Debe tener entre 3 y 100 caracteres (letras, números, '.', '_' o '-') y no puede empezar con 'amq.'";

    private ValidationRules() {
    }
}
