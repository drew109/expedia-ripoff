# Flight Fetcher✈

## Overviewℹ
The Flight API Fetcher is a simple Java console application that allows users to fetch flight data between two airports. It uses the `java.net.http` package to make HTTP requests to a flight data API and parses the JSON response to display information about flight itineraries.

## Features⚙
- Fetch and display flight data for one-way trips.
- User input for departure and arrival airports.
- Parses and displays sorted fare information from the flight data API.

## Prerequisites📚
- Java JDK 11 or higher.
- `org.json.simple` library for JSON parsing.

## Setup and Execution🚩
1. Ensure Java JDK 11 or higher is installed on your system.
2. Download the `json-simple` library and include it in your project's classpath.
3. Clone this repository or download the source code.
4. Compile the `FlightApiFetcher.java` file: javac FlightApiFetcher.java
5. Run the compiled class: java FlightApiFetcher
6. Follow the on-screen prompts to select your departure and arrival airports.

## How It Works💭
1. The application prompts the user to select a departure and arrival airport from a predefined list.
2. It constructs a request URL using these inputs and sends a GET request to the flight data API.
3. The JSON response is parsed, and fare information is displayed in a sorted table format.

## Dependencies🏪
- Java JDK 11 or higher.
- `org.json.simple` library for JSON parsing.

## Note📝
Replace the placeholder API key in the `fetchFlightData` method with your actual API key to use the flight data API successfully.

## Contributing🤝
Feel free to fork this repository and submit pull requests to contribute to this project.

## License🚨
This project is licensed under the MIT License - see the LICENSE file for details.


