import com.google.gson.Gson;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

import java.util.stream.Stream;

import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.any;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isArray;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;

public class FitnessTrackerTest extends SpringTest {
    private final Gson gson = new Gson();
    private final String trackerUrl = "/api/tracker";

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
            () -> testPostTracker(records),
            () -> testGetTracker(records),
            this::reloadServer,
            () -> testGetTracker(records),
    };
}
