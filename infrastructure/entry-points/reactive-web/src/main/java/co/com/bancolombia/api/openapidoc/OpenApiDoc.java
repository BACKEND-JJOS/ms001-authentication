package co.com.bancolombia.api.openapidoc;

import co.com.bancolombia.api.request.UserRequest;
import co.com.bancolombia.api.response.ApiResponse;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.http.HttpStatus;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.arrayschema.Builder.arraySchemaBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;

@UtilityClass
public class OpenApiDoc {

    private static  final  String MEDIA_TYPE_APPLICATION_JSON = "application/json";

    private static final String TAG_USER = "USUARIO";

    public Builder createService(Builder builder){
        return builder.operationId("createUser")
                .description("Created a new user")
                .requestBody(
                        requestBodyBuilder()
                                .required(true)
                                .content(
                                        contentBuilder()
                                                .mediaType(MEDIA_TYPE_APPLICATION_JSON)
                                                .schema(schemaBuilder().implementation(UserRequest.class))
                                )
                )
                .response(
                        responseBuilder()
                                .responseCode(HttpStatus.CREATED.toString())
                                .description("User created succesfully")
                                .content(
                                        contentBuilder()
                                                .mediaType(MEDIA_TYPE_APPLICATION_JSON)
                                                .schema(schemaBuilder().implementation(ApiResponse.class))
                                )

                )
                .tag(TAG_USER);
    }
}
