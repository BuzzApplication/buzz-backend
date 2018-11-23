package com.buzz.exception;

import com.buzz.view.BaseView;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;

import static com.buzz.exception.GenericError.INTERNAL_ERROR;

/**
 * Created by toshikijahja on 10/30/18.
 */
@Provider
public class BuzzExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(final Exception exception) {
        final ResponseError responseError = getResponseError(exception);
        exception.printStackTrace();
        return buildResponse(responseError);
    }

    private ResponseError getResponseError(final Exception exception) {
        if (exception instanceof BuzzException) {
            return ((BuzzException) exception).getResponseError();
        } else if (exception instanceof ClientErrorException) {
            final Response response = ((ClientErrorException) exception).getResponse();
            return new ResponseError() {
                @Override
                public String getErrorKey() {
                    return response.getStatusInfo().getReasonPhrase().toUpperCase().replace(' ', '_');
                }

                @Override
                public String getErrorDescription() {
                    return response.getStatusInfo().getReasonPhrase().toUpperCase();
                }

                @Override
                public int getHttpResponseCode() {
                    return response.getStatus();
                }
            };
        }

        return INTERNAL_ERROR;
    }

    private static Response buildResponse(final ResponseError responseError) {
        return Response.status(responseError.getHttpResponseCode())
                .entity(buildEntity(responseError))
                .build();
    }

    private static BaseView buildEntity(final ResponseError responseError) {
        return new BaseView()
                .setError(buildError(responseError))
                .setCurrentTime(Instant.now().toEpochMilli());
    }

    private static BuzzError buildError(final ResponseError responseError) {
        return new BuzzError()
                .setCode(responseError.getHttpResponseCode())
                .setErrorKey(responseError.getErrorKey())
                .setDescription(responseError.getErrorDescription());
    }
}
