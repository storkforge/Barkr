package org.storkforge.barkr.exceptions;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {
  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    if (ex instanceof AccountNotFound || ex instanceof PostNotFound) {
     return GraphqlErrorBuilder.newError()
             .errorType(ErrorType.NOT_FOUND)
             .message(ex.getMessage())
             .path(env.getExecutionStepInfo().getPath())
             .location(env.getField().getSourceLocation())
             .build();
    } else if (ex instanceof ConstraintViolationException) {
      return GraphqlErrorBuilder.newError()
              .errorType(ErrorType.BAD_REQUEST)
              .message(ex.getMessage())
              .path(env.getExecutionStepInfo().getPath())
              .location(env.getField().getSourceLocation())
              .build();
    } else {
      return GraphqlErrorBuilder.newError()
              .errorType(ErrorType.INTERNAL_ERROR)
              .message("An unexpected error occurred.")
              .path(env.getExecutionStepInfo().getPath())
              .location(env.getField().getSourceLocation())
              .build();
    }
  }
}
