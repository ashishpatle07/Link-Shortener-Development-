import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class LinkShortener {
    private final HashMap<String, String> urlMap = new HashMap<>();
    private final HashMap<String, String> reverseMap = new HashMap<>();
    private final String domain = "https://short.ly/";
    private final String fileName = "url_mappings.txt";

    public LinkShortener() {
        loadMappings(); // Load mappings from file when the program starts
    }

    // Method to shorten a URL
    public String shortenURL(String longURL) {
        if (reverseMap.containsKey(longURL)) {
            return domain + reverseMap.get(longURL); // Return existing short URL if already present
        }
        String shortCode = Integer.toHexString(longURL.hashCode());
        while (urlMap.containsKey(shortCode)) { // Ensure uniqueness
            shortCode += "1"; // Modify short code if collision occurs
        }
        urlMap.put(shortCode, longURL);
        reverseMap.put(longURL, shortCode);
        saveMappings(); // Save mappings to file
        return domain + shortCode;
    }

    // Method to expand a short URL
    public String expandURL(String shortURL) {
        String shortCode = shortURL.replace(domain, ""); // Extract code after the domain
        if (urlMap.containsKey(shortCode)) {
            return urlMap.get(shortCode);
        } else {
            return "Error: Short URL not found.";
        }
    }

    // Load mappings from file
    private void loadMappings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    urlMap.put(parts[0], parts[1]);
                    reverseMap.put(parts[1], parts[0]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing mappings found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error loading mappings: " + e.getMessage());
        }
    }

    // Save mappings to file
    private void saveMappings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String shortCode : urlMap.keySet()) {
                writer.write(shortCode + " " + urlMap.get(shortCode));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving mappings: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        LinkShortener shortener = new LinkShortener();
        Scanner scanner = new Scanner(System.in);
        int choice;

        System.out.println("Welcome to the Persistent Link Shortener!");
        do {
            System.out.println("\nMenu:");
            System.out.println("1. Shorten a URL");
            System.out.println("2. Expand a Short URL");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter the long URL: ");
                    String longURL = scanner.nextLine();
                    String shortURL = shortener.shortenURL(longURL);
                    System.out.println("Shortened URL: " + shortURL);
                    break;
                case 2:
                    System.out.print("Enter the short URL: ");
                    String inputShortURL = scanner.nextLine();
                    String expandedURL = shortener.expandURL(inputShortURL);
                    System.out.println("Original URL: " + expandedURL);
                    break;
                case 3:
                    System.out.println("Exiting the application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);

        scanner.close();
    }
}
