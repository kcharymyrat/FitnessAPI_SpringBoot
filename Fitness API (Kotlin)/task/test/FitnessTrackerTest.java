import com.google.gson.Gson;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.any;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isArray;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isString;

public class FitnessTrackerTest extends SpringTest {
    private final Gson gson = new Gson();
    private final String trackerUrl = "/api/tracker";
    private final String signupUrl = "/api/developers/signup";
    private final String registerUrl = "/api/applications/register";
    private final DevProfile alice = DevProfileMother.alice();
    private final DevProfile aliceCopy = DevProfileMother.alice();
    private final DevProfile bob = DevProfileMother.bob();
    private final AppProfile demo1 = AppProfileMother.demo1();
    private final AppProfile demo1Copy = AppProfileMother.demo1();
    private final AppProfile demo2 = AppProfileMother.demo2();

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

    CheckResult testRegisterApp(DevProfile devProfile,
                                AppProfile appProfile) {
        HttpResponse response = post(registerUrl, gson.toJson(appProfile))
                .basicAuth(devProfile.getEmail(), devProfile.getPassword())
                .send();

        checkStatusCode(response, 201);
        checkAppRegistrationResponseJson(response, appProfile);

        var apikey = response.getJson().getAsJsonObject().get("apikey").getAsString();
        appProfile.setApikey(apikey);
        if (devProfile.getApplications() == null) {
            devProfile.setApplications(new ArrayList<>());
        }
        devProfile.getApplications().add(appProfile);

        return CheckResult.correct();
    }

    CheckResult testRegisterInvalidApp(DevProfile devProfile,
                                       AppProfile appProfile) {
        HttpResponse response = post(registerUrl, gson.toJson(appProfile))
                .basicAuth(devProfile.getEmail(), devProfile.getPassword())
                .send();

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
        var applications = expectedData.getApplications();

        expect(response.getContent()).asJson().check(
                isObject()
                        .value("id", any())
                        .value("email", Pattern.compile(expectedData.getEmail(), Pattern.CASE_INSENSITIVE))
                        .value("applications", isArray(expectedData.getApplications().size())
                                .item(isObject()
                                        .value("id", any())
                                        .value("name", applications.get(1).getName())
                                        .value("description", applications.get(1).getDescription())
                                        .value("apikey", applications.get(1).getApikey())
                                )
                                .item(isObject()
                                        .value("id", any())
                                        .value("name", applications.get(0).getName())
                                        .value("description", applications.get(0).getDescription())
                                        .value("apikey", applications.get(0).getApikey())
                                )
                        )
        );
    }

    private void checkAppRegistrationResponseJson(HttpResponse response, AppProfile expectedData) {
        expect(response.getContent()).asJson().check(
                isObject()
                        .value("name", expectedData.getName())
                        .value("apikey", isString())
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
            () -> testRegisterApp(alice, demo1),
            () -> testRegisterApp(alice, demo2),
            () -> testRegisterInvalidApp(alice, demo1Copy),
            () -> testRegisterInvalidApp(alice, AppProfileMother.withBadName(null)),
            () -> testRegisterInvalidApp(alice, AppProfileMother.withBadName(" ")),
            () -> testRegisterInvalidApp(alice, AppProfileMother.withBadDescription(null)),
            () -> testGetProfile(alice, bob),
            this::reloadServer,
            () -> testGetTracker(records),
    };
}
