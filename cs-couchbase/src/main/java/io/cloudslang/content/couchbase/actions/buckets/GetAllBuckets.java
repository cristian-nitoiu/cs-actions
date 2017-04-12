/*******************************************************************************
 * (c) Copyright 2017 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package io.cloudslang.content.couchbase.actions.buckets;

import com.hp.oo.sdk.content.annotations.Action;
import com.hp.oo.sdk.content.annotations.Output;
import com.hp.oo.sdk.content.annotations.Param;
import com.hp.oo.sdk.content.annotations.Response;
import com.hp.oo.sdk.content.plugin.ActionMetadata.MatchType;
import com.hp.oo.sdk.content.plugin.ActionMetadata.ResponseType;
import io.cloudslang.content.constants.ReturnCodes;
import io.cloudslang.content.couchbase.entities.inputs.CommonInputs;
import io.cloudslang.content.couchbase.execute.CouchbaseService;
import io.cloudslang.content.httpclient.HttpClientInputs;

import java.util.Map;

import static io.cloudslang.content.constants.OutputNames.EXCEPTION;
import static io.cloudslang.content.constants.OutputNames.RETURN_CODE;
import static io.cloudslang.content.constants.OutputNames.RETURN_RESULT;
import static io.cloudslang.content.constants.ResponseNames.FAILURE;
import static io.cloudslang.content.constants.ResponseNames.SUCCESS;

import static io.cloudslang.content.couchbase.entities.constants.Constants.Api.BUCKETS;
import static io.cloudslang.content.couchbase.entities.constants.Constants.BucketActions.GET_ALL_BUCKETS;
import static io.cloudslang.content.couchbase.entities.constants.Inputs.CommonInputs.ENDPOINT;
import static io.cloudslang.content.couchbase.utils.InputsUtil.getHttpClientInputs;

import static io.cloudslang.content.httpclient.HttpClientInputs.USERNAME;
import static io.cloudslang.content.httpclient.HttpClientInputs.PASSWORD;
import static io.cloudslang.content.httpclient.HttpClientInputs.PROXY_HOST;
import static io.cloudslang.content.httpclient.HttpClientInputs.PROXY_PORT;
import static io.cloudslang.content.httpclient.HttpClientInputs.PROXY_USERNAME;
import static io.cloudslang.content.httpclient.HttpClientInputs.PROXY_PASSWORD;
import static io.cloudslang.content.httpclient.HttpClientInputs.TRUST_ALL_ROOTS;
import static io.cloudslang.content.httpclient.HttpClientInputs.X509_HOSTNAME_VERIFIER;
import static io.cloudslang.content.httpclient.HttpClientInputs.TRUST_KEYSTORE;
import static io.cloudslang.content.httpclient.HttpClientInputs.TRUST_PASSWORD;
import static io.cloudslang.content.httpclient.HttpClientInputs.KEYSTORE;
import static io.cloudslang.content.httpclient.HttpClientInputs.KEYSTORE_PASSWORD;
import static io.cloudslang.content.httpclient.HttpClientInputs.CONNECT_TIMEOUT;
import static io.cloudslang.content.httpclient.HttpClientInputs.SOCKET_TIMEOUT;
import static io.cloudslang.content.httpclient.HttpClientInputs.USE_COOKIES;
import static io.cloudslang.content.httpclient.HttpClientInputs.KEEP_ALIVE;
import static io.cloudslang.content.utils.OutputUtilities.getFailureResultsMap;
import static org.apache.http.client.methods.HttpGet.METHOD_NAME;

/**
 * Created by Mihai Tusa
 * 3/26/2017.
 */
public class GetAllBuckets {
    /**
     * Retrieve all bucket information for a cluster.
     * http://docs.couchbase.com/admin/admin/REST/rest-buckets-summary.html
     *
     * @param endpoint      Endpoint to which request will be sent. A valid endpoint will be formatted as it shows in
     *                      bellow example.
     *                      Example: "http://somewhere.couchbase.com:8091"
     * @param username      Username used in basic authentication.
     * @param password      Password associated with "username" input to be used in basic authentication.
     * @param proxyHost     Optional - proxy server used to connect to Couchbase API. If empty no proxy will be used.
     * @param proxyPort     Optional - proxy server port. You must either specify values for both proxyHost and proxyPort
     *                      inputs or leave them both empty.
     * @param proxyUsername Optional - proxy server user name.
     * @param proxyPassword Optional - proxy server password associated with the proxyUsername input value.
     * @return A map with strings as keys and strings as values that contains: outcome of the action (or failure message
     * and the exception if there is one), returnCode of the operation and the ID of the request
     */
    @Action(name = "Get All Buckets",
            outputs = {
                    @Output(RETURN_CODE),
                    @Output(RETURN_RESULT),
                    @Output(EXCEPTION)
            },
            responses = {
                    @Response(text = SUCCESS, field = RETURN_CODE, value = ReturnCodes.SUCCESS,
                            matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.RESOLVED),
                    @Response(text = FAILURE, field = RETURN_CODE, value = ReturnCodes.FAILURE,
                            matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.ERROR, isOnFail = true)
            })
    public Map<String, String> execute(@Param(value = ENDPOINT, required = true) String endpoint,
                                       @Param(value = USERNAME, required = true) String username,
                                       @Param(value = PASSWORD, required = true, encrypted = true) String password,
                                       @Param(value = PROXY_HOST) String proxyHost,
                                       @Param(value = PROXY_PORT) String proxyPort,
                                       @Param(value = PROXY_USERNAME) String proxyUsername,
                                       @Param(value = PROXY_PASSWORD, encrypted = true) String proxyPassword,
                                       @Param(value = TRUST_ALL_ROOTS) String trustAllRoots,
                                       @Param(value = X509_HOSTNAME_VERIFIER) String x509HostnameVerifier,
                                       @Param(value = TRUST_KEYSTORE) String trustKeystore,
                                       @Param(value = TRUST_PASSWORD, encrypted = true) String trustPassword,
                                       @Param(value = KEYSTORE) String keystore,
                                       @Param(value = KEYSTORE_PASSWORD, encrypted = true) String keystorePassword,
                                       @Param(value = CONNECT_TIMEOUT) String connectTimeout,
                                       @Param(value = SOCKET_TIMEOUT) String socketTimeout,
                                       @Param(value = USE_COOKIES) String useCookies,
                                       @Param(value = KEEP_ALIVE) String keepAlive) {
        try {
            final HttpClientInputs httpClientInputs = getHttpClientInputs(username, password, proxyHost, proxyPort,
                    proxyUsername, proxyPassword, trustAllRoots, x509HostnameVerifier, trustKeystore, trustPassword,
                    keystore, keystorePassword, connectTimeout, socketTimeout, useCookies, keepAlive, METHOD_NAME);

            final CommonInputs commonInputs = new CommonInputs.Builder()
                    .withAction(GET_ALL_BUCKETS)
                    .withApi(BUCKETS)
                    .withEndpoint(endpoint)
                    .build();

            return new CouchbaseService().execute(httpClientInputs, commonInputs);
        } catch (Exception exception) {
            return getFailureResultsMap(exception);
        }
    }
}