import java.util.*;


public class run {

    public static boolean checkCapacity(int maxCapacity, List<Map<String, String>> guests) {
        List<String[]> events = new ArrayList<>();

        for (Map<String, String> guest : guests) {
            String checkIn = guest.get("check-in");
            String checkOut = guest.get("check-out");
            events.add(new String[]{checkIn, "check-in"});
            events.add(new String[]{checkOut, "check-out"});
        }

        List<String[]> sortedEvents = events.stream()
                .sorted(Comparator.comparing((String[] event) -> event[0])
                        .thenComparing(event -> event[1].equals("check-in") ? 1 : 0))
                .toList();

        int currentGuests = 0;
        for (String[] event : sortedEvents) {
            if (event[1].equals("check-in")) {
                currentGuests++;
            } else {
                currentGuests--;
            }
            if (currentGuests > maxCapacity) {
                return false;
            }
        }

        return true;
    }


    private static Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.substring(1, json.length() - 1);

        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            map.put(key, value);
        }

        return map;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int maxCapacity = Integer.parseInt(scanner.nextLine());

        int n = Integer.parseInt(scanner.nextLine());

        List<Map<String, String>> guests = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String jsonGuest = scanner.nextLine();
            Map<String, String> guest = parseJsonToMap(jsonGuest);
            guests.add(guest);
        }

        boolean result = checkCapacity(maxCapacity, guests);

        System.out.println(result ? "True" : "False");

        scanner.close();
    }
}