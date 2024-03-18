import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;

public class FlightApiFetcher {

    private static final HashMap<String, String> airports = new HashMap<>();
    static {
        airports.put("ATL", "Hartsfield–Jackson Atlanta International Airport");
        airports.put("LAX", "Los Angeles International Airport");
        airports.put("ORD", "O'Hare International Airport");
        airports.put("DFW", "Dallas/Fort Worth International Airport");
        airports.put("JFK", "John F. Kennedy International Airport");
        airports.put("DEN", "Denver International Airport");
        airports.put("SFO", "San Francisco International Airport");
        airports.put("SEA", "Seattle-Tacoma International Airport");
        airports.put("LAS", "McCarran International Airport");
        airports.put("MIA", "Miami International Airport");
        airports.put("BOS", "Boston Logan International Airport");
        airports.put("MCO", "Orlando International Airport");
        airports.put("EWR", "Newark Liberty International Airport");
        airports.put("CLT", "Charlotte Douglas International Airport");
        airports.put("PHX", "Phoenix Sky Harbor International Airport");
        airports.put("IAH", "George Bush Intercontinental Airport");
        airports.put("LGA", "LaGuardia Airport");
        airports.put("HNL", "Daniel K. Inouye International Airport");
        airports.put("PHL", "Philadelphia International Airport");
        airports.put("SAN", "San Diego International Airport");
        airports.put("MDW", "Chicago Midway International Airport");
        airports.put("TPA", "Tampa International Airport");
        airports.put("PDX", "Portland International Airport");
        airports.put("DCA", "Ronald Reagan Washington National Airport");
        airports.put("DAL", "Dallas Love Field");
        airports.put("DTW", "Detroit Metropolitan Airport");
        airports.put("MSP", "Minneapolis–Saint Paul International Airport");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String departureAirportCode = "";
        while (true) {
            System.out.println("Select your departure airport:");
            airports.forEach((code, name) -> System.out.println(code + " - " + name));
            departureAirportCode = scanner.nextLine().toUpperCase();
            if (airports.containsKey(departureAirportCode)) {
                break;
            }
            System.out.println("Invalid departure airport code. Please ensure you select from the list.");
        }

        String arrivalAirportCode = "";
        while (true) {
            System.out.println("Select your arrival airport:");
            arrivalAirportCode = scanner.nextLine().toUpperCase();
            if (airports.containsKey(arrivalAirportCode)) {
                break;
            }
            System.out.println("Invalid arrival airport code. Please ensure you select from the list.");
        }

        fetchFlightData(departureAirportCode, arrivalAirportCode);
    }

    public static void fetchFlightData(String departureAirportCode, String arrivalAirportCode) {
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = "65df8d6d7983b3f313bc584b"; // Replace with your actual API key
        String departureDate = "2024-05-20"; // Example date
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

                ArrayList<JSONObject> fares = new ArrayList<>();

                for (Object itinerary : itineraries) {
                    JSONObject obj = (JSONObject) itinerary;
                    JSONArray pricingOptions = (JSONArray) obj.get("pricing_options");

                    if (pricingOptions != null && !pricingOptions.isEmpty()) {
                        for (Object pricingOptionObj : pricingOptions) {
                            JSONObject pricingOption = (JSONObject) pricingOptionObj;
                            JSONArray items = (JSONArray) pricingOption.get("items");

                            if (items != null) {
                                for (Object itemObj : items) {
                                    JSONObject item = (JSONObject) itemObj;
                                    JSONArray itemFares = (JSONArray) item.get("fares");

                                    if (itemFares != null && !itemFares.isEmpty()) {
                                        for (Object fareObj : itemFares) {
                                            JSONObject fare = (JSONObject) fareObj;
                                            fare.put("pricingOption", pricingOption);
                                            fares.add(fare);
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

                // Sort the fares by price in ascending order
                fares.sort(Comparator.comparingDouble(fare -> getFarePrice((JSONObject) fare.get("pricingOption"))));

                // Print the sorted fares
                for (JSONObject fare : fares) {
                    JSONObject pricingOption = (JSONObject) fare.get("pricingOption");
                    printFareData(fare, pricingOption);
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

    private static double getFarePrice(JSONObject pricingOption) {
        JSONObject priceObj = (JSONObject) pricingOption.get("price");
        return ((Number) priceObj.getOrDefault("amount", 0)).doubleValue();
    }

    private static void printTableHeader() {
        System.out.println("+-------------------------------------------------------------------------------------------+");
        System.out.println("| Segment ID            | Booking Code | Fare Basis Code  | Price   | Fare Family         |");
        System.out.println("+-------------------------------------------------------------------------------------------+");
    }

    private static void printFareData(JSONObject fare, JSONObject pricingOption) {
        String segmentId = (String) fare.getOrDefault("segment_id", "N/A");
        String bookingCode = (String) fare.getOrDefault("booking_code", "N/A");
        String fareBasisCode = (String) fare.getOrDefault("fare_basis_code", "N/A");
        String fareFamily = (String) fare.getOrDefault("fare_family", "N/A");
        double price = getFarePrice(pricingOption);
        System.out.format("| %-22s | %-12s | %-17s | $%-6.2f | %-20s |\n",
                          segmentId, bookingCode, fareBasisCode, price, fareFamily);
    }
}