package io.cloudslang.content.datetime.actions;

/**
 * Created by cadm on 4/25/2016.
 */

import com.hp.oo.sdk.content.annotations.Action;
import com.hp.oo.sdk.content.annotations.Output;
import com.hp.oo.sdk.content.annotations.Param;
import com.hp.oo.sdk.content.annotations.Response;
import com.hp.oo.sdk.content.plugin.ActionMetadata.MatchType;
import com.hp.oo.sdk.content.plugin.ActionMetadata.ResponseType;
import io.cloudslang.content.datetime.services.DateTimeService;
import io.cloudslang.content.datetime.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class OffsetTimeBy {

    /**
     * Changes the time represented by a date by the specified number of seconds.
     * If locale is specified, it will return the date and time string based on the locale.
     * Otherwise, default locale will be used.
     *
     * @param date          Current time.
     * @param offset        The offset value specified number of seconds.
     * @param localeLang    The locale language for date and time string. If localeLang is 'unix' the
     *                      localeCountry input is ignored and the result will be the current UNIX
     *                      timestamp. Examples:  en, ja, unix.
     * @param localeCountry The locale country for date and time string. For example, US or JP.
     *                      If localeLang is not specified, this input will be ignored.
     */
    @Action(name = "Offset Time By",
            description = "Changes the time represented by a date by the specified number of seconds",
            outputs = {
                    @Output(Constants.OutputNames.RETURN_RESULT),
                    @Output(Constants.OutputNames.RETURN_CODE),
                    @Output(Constants.OutputNames.EXCEPTION)
            },
            responses = {
                    @Response(text = Constants.ResponseNames.SUCCESS, field = Constants.OutputNames.RETURN_CODE,
                            value = Constants.ReturnCodes.RETURN_CODE_SUCCESS, matchType = MatchType.COMPARE_EQUAL,
                            responseType = ResponseType.RESOLVED),
                    @Response(text = Constants.ResponseNames.FAILURE, field = Constants.OutputNames.RETURN_CODE,
                            value = Constants.ReturnCodes.RETURN_CODE_FAILURE, matchType = MatchType.COMPARE_EQUAL,
                            responseType = ResponseType.ERROR, isOnFail = true)})

    public Map<String, String> execute(
            @Param(value = Constants.InputNames.LOCALE_DATE, required = true) String date,
            @Param(value = Constants.InputNames.LOCALE_OFFSET, required = true) String offset,
            @Param(Constants.InputNames.LOCALE_LANG) String localeLang,
            @Param(Constants.InputNames.LOCALE_COUNTRY) String localeCountry) {

        Map<String, String> returnResult = new HashMap<>();
        try {
            String returnValue = new DateTimeService().offsetTimeBy(date, offset, localeLang, localeCountry);
            returnResult.put(Constants.OutputNames.RETURN_CODE, Constants.ReturnCodes.RETURN_CODE_SUCCESS);
            returnResult.put(Constants.OutputNames.RETURN_RESULT, returnValue);
        } catch (Exception exception) {
            returnResult.put(Constants.OutputNames.EXCEPTION, exception.toString());
            returnResult.put(Constants.OutputNames.RETURN_RESULT, exception.getMessage());
            returnResult.put(Constants.OutputNames.RETURN_CODE, Constants.ReturnCodes.RETURN_CODE_FAILURE);
        }

        return returnResult;
    }
}