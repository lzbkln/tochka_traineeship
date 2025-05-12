import java.util.*;


public class run {

    public static final String CHECK_IN = "check-in";
    public static final String CHECK_OUT = "check-out";
    public static final int INDEX_ZERO = 0;
    public static final int INDEX_ONE = 1;

    public static boolean checkCapacity(int maxCapacity, List<Map<String, String>> guests) {
        List<String[]> events = new ArrayList<>();

        for (Map<String, String> guest : guests) {
            events.add(new String[]{guest.get(CHECK_IN), CHECK_IN});
            events.add(new String[]{guest.get(CHECK_OUT), CHECK_OUT});
        }

        List<String[]> sortedEvents = events.stream()
                .sorted(Comparator.comparing((String[] event) -> event[INDEX_ZERO])
                        .thenComparing(event -> event[INDEX_ONE].equals(CHECK_IN) ? 1 : 0))
                .toList();

        int currentGuestCount = 0;
        for (String[] event : sortedEvents) {
            if (event[INDEX_ONE].equals(CHECK_IN)) {
                currentGuestCount++;
            } else {
                currentGuestCount--;
            }
            if (currentGuestCount > maxCapacity) {
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