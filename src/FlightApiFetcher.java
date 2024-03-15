import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FlightApiFetcher {

    public static void fetchFlightData() {
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = "65f3ad03fa52f4000dc59842"; // Replace with your actual API key
        String departureAirportCode = "MIA";
        String arrivalAirportCode = "BOS";
        String departureDate = "2024-05-20";
        String numberOfAdults = "1";
        String cabinClass = "Economy";
        String currency = "USD";

        String requestURL = String.format("https://api.flightapi.io/onewaytrip/65f3ad03fa52f4000dc59842/BOS/MIA/2024-05-20/1/0/0/Economy/USD",
                                          apiKey, departureAirportCode, arrivalAirportCode, departureDate,
                                          numberOfAdults, cabinClass, currency);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response status code: " + response.statusCode());
            if (response.statusCode() == 200) {
                parseJson(response.body());
            } else {
                System.out.println("Failed to fetch flight data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseJson(String responseBody) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(responseBody);
            JSONArray itineraries = (JSONArray) json.get("itineraries");
            if (itineraries != null) {
                for (Object itinerary : itineraries) {
                    JSONObject obj = (JSONObject) itinerary;
                    System.out.println(obj.toJSONString());
                    // Further processing...
                }
            } else {
                System.out.println("No itineraries found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        fetchFlightData();
    }
}
