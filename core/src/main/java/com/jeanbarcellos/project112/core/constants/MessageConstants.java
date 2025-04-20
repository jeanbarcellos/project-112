package com.jeanbarcellos.project112.core.constants;


/**
 * Class with generic system message constants
 *
 * @author Jean Silva de Barcellos (jeanbarcellos@hotmail.com)
 *
 * @see https://github.com/jeanbarcellos/project-101.backend-java/blob/dev/src/main/java/com/jeanbarcellos/project101/infra/constants/MessageConstants.java
 */
public class MessageConstants {

    // TODO Utilizar recurso de internacionalização, deixando as mensagens mutáveis conforme pais/linguagem

    public static final String MSG_ERROR_SERVICE = "Unexpected error in the service. If the problem persists, contact your administrator.";
    public static final String MSG_ERROR_REQUEST = "The request failed due to errors in the data provided..";
    public static final String MSG_ERROR_NOT_FOUND = "Resource not found.";
    public static final String MSG_ERROR_FORBIDDEN = "Unauthorized access.";

    public static final String MSG_ERROR_VALIDATION = "The data provided is invalid.";
    public static final String MSG_ERROR_VALIDATION_FIELD_LIST = "The field '%s' %s";

    public static final String MGG_ERROR_VALIDATION_JSON_MALFORMATED = "Malformed request or invalid data format."; // Mapping error. Malformed JSON or type mismatch.
    public static final String MGG_ERROR_VALIDATION_JSON_INVALID_FORMAT = "has invalid type or format";

    public static final String MSG_ERROR_ENTITY_NOT_FOUND = "There is no %s with the given ID '%s'.";
    public static final String MSG_ERROR_OPTIMISTIC_LOCKING = "The current state of the entity changed before you completed the operation. Please try again.";

    public static final String MSG_ERROR_REQUEST_IDEMPOTENT = "A request with ID (requestId) '%s' has already been submitted.";

    public static final String MSG_ERROR_HTTP_METHOD_NOT_SUPPORTED = "HTTP method not supported: %s";

    // not instantiable
    private MessageConstants() {
    }

}