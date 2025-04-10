package org.storkforge.barkr.exceptions;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
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
    } else {
      return null;
    }
  }
}
