import java.util.Calendar;

public class Helper {
    public static boolean isEligible(String fuelType, int year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = currentYear - year;
        return age > 5 && (fuelType.equalsIgnoreCase("petrol") || fuelType.equalsIgnoreCase("diesel"));
    }

    public static double calculateExchangeValue(int year, int mileage) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = currentYear - year;
        double base = 10000.0;
        double agePenalty = Math.max(0, age) * 0.08 * base;
        double mileagePenalty = (mileage / 100000.0) * base * 0.2;
        double val = Math.max(500.0, base - agePenalty - mileagePenalty);
        return Math.round(val * 100.0) / 100.0;
    }

    public static double calculateSubsidyPercent(int year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = currentYear - year;
        double p = 10 + Math.min(40, age * 2);
        return Math.round(p * 100.0) / 100.0;
    }
}
