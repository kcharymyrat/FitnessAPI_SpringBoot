import com.google.gson.Gson;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.any;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isArray;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;

public class FitnessTrackerTest extends SpringTest {
    private final Gson gson = new Gson();
    private final String trackerUrl = "/api/tracker";
    private final String signupUrl = "/api/developers/signup";

    private final DevProfile alice = DevProfileMother.alice();
    private final DevProfile aliceCopy = DevProfileMother.alice();
    private final DevProfile bob = DevProfileMother.bob();

    public FitnessTrackerTest() {
        super("../fitness_db.mv.db");
    }

    CheckResult testPostTracker(DataRecord[] data) {
        for (var item : data) {
            HttpResponse response = post(trackerUrl, gson.toJson(item)).send();
            checkStatusCode(response, 201);
        }
        return CheckResult.correct();
    }

    CheckResult testGetTracker(DataRecord[] data) {
        HttpResponse response = get(trackerUrl).send();

        checkStatusCode(response, 200);
        checkDataJson(response, data);

        return CheckResult.correct();
    }

    CheckResult testRegisterValidDev(DevProfile devProfile) {
        HttpResponse response = post(signupUrl, gson.toJson(devProfile)).send();

        checkStatusCode(response, 201);

        String location = response.getHeaders().get("Location");
        if (location == null || !location.matches("/api/developers/.+")) {
            return CheckResult.wrong(
                    "User registration response should contain the 'Location' header" +
                            " with the value '/api/developers/<id>'"
            );
        }

        devProfile.setId(location.replaceAll(".+/", ""));

        return CheckResult.correct();
    }

    CheckResult testRegisterInvalidDev(DevProfile devProfile) {
        HttpResponse response = post(signupUrl, gson.toJson(devProfile)).send();

        checkStatusCode(response, 400);

        return CheckResult.correct();
    }

    CheckResult testGetProfile(DevProfile devProfile,
                               DevProfile anotherDevProfile) {
        var profileUrl = "/api/developers/" + devProfile.getId();

        // no auth
        HttpResponse response = get(profileUrl).send();
        checkStatusCode(response, 401);

        // bad credentials
        response = get(profileUrl)
                .basicAuth(devProfile.getEmail(), devProfile.getPassword() + "12345")
                .send();
        checkStatusCode(response, 401);

        // wrong user
        response = get(profileUrl)
                .basicAuth(anotherDevProfile.getEmail(), anotherDevProfile.getPassword())
                .send();
        checkStatusCode(response, 403);

        // proper user
        response = get(profileUrl)
                .basicAuth(devProfile.getEmail(), devProfile.getPassword())
                .send();
        checkStatusCode(response, 200);
        checkProfileJson(response, devProfile);

        return CheckResult.correct();
    }

    private void checkStatusCode(HttpResponse response, int expected) {
        var actual = response.getStatusCode();
        var method = response.getRequest().getMethod();
        var endpoint = response.getRequest().getEndpoint();
        if (actual != expected) {
            throw new WrongAnswer("""
                    %s %s should respond with status code %d, responded with %d
                    \r
                    """.formatted(method, endpoint, expected, actual));
        }
    }

    private void checkDataJson(HttpResponse response, DataRecord[] expectedData) {
        expect(response.getContent()).asJson().check(
                isArray(expectedData.length)
                        .item(isObject()
                                .value("id", any())
                                .value("username", expectedData[3].getUsername())
                                .value("activity", expectedData[3].getActivity())
                                .value("duration", expectedData[3].getDuration())
                                .value("calories", expectedData[3].getCalories())
                        )
                        .item(isObject()
                                .value("id", any())
                                .value("username", expectedData[2].getUsername())
                                .value("activity", expectedData[2].getActivity())
                                .value("duration", expectedData[2].getDuration())
                                .value("calories", expectedData[2].getCalories())
                        )
                        .item(isObject()
                                .value("id", any())
                                .value("username", expectedData[1].getUsername())
                                .value("activity", expectedData[1].getActivity())
                                .value("duration", expectedData[1].getDuration())
                                .value("calories", expectedData[1].getCalories())
                        )
                        .item(isObject()
                                .value("id", any())
                                .value("username", expectedData[0].getUsername())
                                .value("activity", expectedData[0].getActivity())
                                .value("duration", expectedData[0].getDuration())
                                .value("calories", expectedData[0].getCalories())
                        )
        );
    }

    private void checkProfileJson(HttpResponse response, DevProfile expectedData) {
        expect(response.getContent()).asJson().check(
                isObject()
                        .value("id", any())
                        .value("email", Pattern.compile(expectedData.getEmail(), Pattern.CASE_INSENSITIVE))
        );
    }

    CheckResult reloadServer() {
        try {
            reloadSpring();
        } catch (Exception ex) {
            throw new WrongAnswer("Failed to restart application: " + ex.getMessage());
        }
        return CheckResult.correct();
    }

    DataRecord[] records = Stream
            .generate(DataRecordMother::createRecord)
            .limit(4)
            .toArray(DataRecord[]::new);

    @DynamicTest
    DynamicTesting[] dt = new DynamicTesting[]{
            () -> testRegisterValidDev(alice),
            () -> testRegisterValidDev(bob),
            () -> testRegisterInvalidDev(DevProfileMother.withBadEmail(null)),
            () -> testRegisterInvalidDev(DevProfileMother.withBadEmail("")),
            () -> testRegisterInvalidDev(DevProfileMother.withBadEmail("email")),
            () -> testRegisterInvalidDev(DevProfileMother.withBadPassword(null)),
            () -> testRegisterInvalidDev(DevProfileMother.withBadPassword("")),
            () -> testRegisterInvalidDev(aliceCopy),
            () -> testPostTracker(records),
            () -> testGetTracker(records),
            () -> testGetProfile(alice, bob),
            this::reloadServer,
            () -> testGetTracker(records),
    };
}
