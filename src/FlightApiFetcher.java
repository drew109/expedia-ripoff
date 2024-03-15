import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FlightApiFetcher {

    public static void fetchFlightData() {
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = "65df8d6d7983b3f313bc584b"; // Replace with your actual API key
        String departureAirportCode = "MIA";
        String arrivalAirportCode = "BOS";
        String departureDate = "2024-05-20";
        String numberOfAdults = "1";
        String cabinClass = "Economy";
        String currency = "USD";

        String requestURL = String.format("https://api.flightapi.io/onewaytrip/%s/%s/%s/%s/%s/0/0/%s/%s",
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
                System.out.println("Starting JSON parsing...");
                parseJson(response.body());
            } else {
                System.out.println("Failed to fetch flight data.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during HTTP request or parsing:");
            e.printStackTrace();
        }
    }

    private static void parseJson(String responseBody) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(responseBody);
            System.out.println("Successfully parsed JSON object from response.");
            JSONArray itineraries = (JSONArray) json.get("itineraries");

            if (itineraries != null && !itineraries.isEmpty()) {
                System.out.println("Processing itineraries...");
                printTableHeader();

                for (Object itinerary : itineraries) {
                    processItinerary(itinerary);
                }

                System.out.println("+-------------------------------------------------------------------------------------------+");
            } else {
                System.out.println("No itineraries found or itineraries array is empty.");
            }
        } catch (ParseException e) {
            System.out.println("Failed to parse JSON response:");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during JSON processing:");
            e.printStackTrace();
        }
    }

    private static void printTableHeader() {
        System.out.println("+-------------------------------------------------------------------------------------------+");
        System.out.println("| Segment ID            | Booking Code | Fare Basis Code  | Price   | Fare Family         |");
        System.out.println("+-------------------------------------------------------------------------------------------+");
    }

    private static void processItinerary(Object itineraryObj) {
        JSONObject itinerary = (JSONObject) itineraryObj;
        JSONArray pricingOptions = (JSONArray) itinerary.get("pricing_options");

        if (pricingOptions != null && !pricingOptions.isEmpty()) {
            for (Object pricingOptionObj : pricingOptions) {
                JSONObject pricingOption = (JSONObject) pricingOptionObj;
                JSONArray items = (JSONArray) pricingOption.get("items");

                if (items != null) {
                    for (Object itemObj : items) {
                        JSONObject item = (JSONObject) itemObj;
                        JSONArray fares = (JSONArray) item.get("fares");

                        if (fares != null && !fares.isEmpty()) {
                            for (Object fareObj : fares) {
                                JSONObject fare = (JSONObject) fareObj;
                                printFareData(fare, pricingOption);
                            }
                        } else {
                            System.out.println("| No fare data available for this item within the itinerary                        |");
                        }
                    }
                }
            }
        } else {
            System.out.println("| No pricing options available for this itinerary                                   |");
        }
    }

    private static void printFareData(JSONObject fare, JSONObject pricingOption) {
        String segmentId = (String) fare.getOrDefault("segment_id", "N/A");
        String bookingCode = (String) fare.getOrDefault("booking_code", "N/A");
        String fareBasisCode = (String) fare.getOrDefault("fare_basis_code", "N/A");
        String fareFamily = (String) fare.getOrDefault("fare_family", "N/A");
        
        // Properly extract the price object and then the amount
        JSONObject priceObj = pricingOption.containsKey("price") ? (JSONObject) pricingOption.get("price") : new JSONObject();
        Number priceNumber = (Number) priceObj.getOrDefault("amount", 0); // Here, ensure priceObj is used, which is a JSONObject
        String priceStr = priceNumber != null ? String.format("$%-6.2f", priceNumber.doubleValue()) : "N/A";
    
        System.out.format("| %-22s | %-12s | %-17s | %-7s | %-20s |\n",
                          segmentId, bookingCode, fareBasisCode, priceStr, fareFamily);
    }
    
    public static void main(String[] args) {
        fetchFlightData();
    }
}
