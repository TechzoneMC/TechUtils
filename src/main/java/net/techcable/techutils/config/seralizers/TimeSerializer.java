package net.techcable.techutils.config.seralizers;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.techcable.techutils.config.ConfigSerializer;
import net.techcable.techutils.config.Time;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.InvalidConfigurationException;

import com.google.common.primitives.Primitives;

public class TimeSerializer implements ConfigSerializer {

    @Override
    public Object serialize(Object java, Annotation[] annotations) {
        Time annotation = null;
        for (Annotation declaredAnnotation : annotations) {
            if (declaredAnnotation instanceof Time) {
                annotation = (Time) declaredAnnotation;
                break;
            }
        }
        if (annotation == null) throw new RuntimeException("Unable to serialize types not annotated with @Time");
        Class seraizlieTo = java.getClass();
        seraizlieTo = Primitives.unwrap(seraizlieTo);
        long millis = seraizlieTo == int.class ? (Integer) java : (Long) java;
        TimeUnit defaultUnit = annotation.value();
        return toString(millis, defaultUnit);
    }

    @Override
    public Object deserialize(Object yaml, Class type, Annotation[] annotations) throws InvalidConfigurationException {
        Time annotation = null;
        for (Annotation declaredAnnotation : annotations) {
            if (declaredAnnotation instanceof Time) {
                annotation = (Time) declaredAnnotation;
                break;
            }
        }
        if (annotation == null) throw new InvalidConfigurationException("Unable to serialize types not annotated with @Time");
        String raw = yaml.toString();
        try {
            long parsed = parse(raw, annotation.value());
            parsed = annotation.as().convert(parsed, TimeUnit.MILLISECONDS);
            if (type == int.class) return (int) parsed;
            return parsed;
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException(e.getMessage());
        }
    }

    @Override
    public boolean canSerialize(Class type) {
        type = Primitives.unwrap(type);
        return type == int.class || type == long.class;
    }

    @Override
    public boolean canDeserialize(Class type) {
        return type == String.class || canSerialize(type); // Number or string
    }

    public static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)(?:\\s*(\\w+))?");

    public static long parse(String raw, TimeUnit defaultUnit) {
        long total = 0;
        Matcher m = TIME_PATTERN.matcher(raw);
        if (!m.find()) throw new IllegalArgumentException("Invalid time: " + raw);
        else m.reset();
        while (m.find()) {
            String rawValue = m.group(1);
            long value = Long.parseLong(rawValue);
            String rawUnit = m.group(2);
            TimeUnit unit = defaultUnit;
            if (rawUnit != null) {
                switch (rawUnit.toLowerCase()) {
                    case "d":
                    case "days":
                    case "day":
                        unit = TimeUnit.DAYS;
                        break;
                    case "h":
                    case "hr":
                    case "hrs":
                    case "hours":
                    case "hour":
                        unit = TimeUnit.HOURS;
                        break;
                    case "m":
                    case "min":
                    case "mins":
                    case "minutes":
                    case "minute":
                        unit = TimeUnit.MINUTES;
                        break;
                    case "s":
                    case "sec":
                    case "secs":
                    case "seconds":
                    case "second":
                        unit = TimeUnit.SECONDS;
                        break;
                }
            }
            value = unit.toMillis(value);
            total += value;
        }
        return total;
    }

    public static String toString(long millis, TimeUnit defaultUnit) {
        TimeUnit unit = calculateTimeUnit(millis, defaultUnit);
        if (unit == TimeUnit.MILLISECONDS || unit == TimeUnit.MICROSECONDS || unit == TimeUnit.NANOSECONDS) {
            unit = TimeUnit.SECONDS; // Unsupported unit
        }
        StringBuilder asString = new StringBuilder();
        long time = unit.convert(millis, TimeUnit.MILLISECONDS);
        asString.append(time);
        if (unit != defaultUnit) {
            asString.append(' ');
            switch (unit) {
                case DAYS :
                    asString.append("day");
                    if (time != 1) asString.append("s");
                    break;
                case HOURS:
                    asString.append("hour");
                    if (time != 1) asString.append("s");
                    break;
                case MINUTES :
                    asString.append("minute");
                    if (time != 1) asString.append("s");
                    break;
                case SECONDS:
                    asString.append("second");
                    if (time != 1) asString.append("s");
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported unit " + unit);
            }
        }
        return asString.toString();
    }

    public static TimeUnit calculateTimeUnit(long millis, TimeUnit defaultUnit) {
        TimeUnit[] values = TimeUnit.values();
        ArrayUtils.reverse(values);  // From greatest to smallest
        for (TimeUnit unit : values) {
            if (fitsUnit(millis, unit)) {
                return unit;
            }
        }
        return defaultUnit;
    }

    public static boolean fitsUnit(long millis, TimeUnit unit) {
        return equals(fromMillisExact(millis, unit), unit.convert(millis, TimeUnit.MILLISECONDS));
    }

    private static BigDecimal fromMillisExact(long millis, TimeUnit unit) {
        BigDecimal time = BigDecimal.valueOf(millis);
        return fromMillisExact(time, unit);
    }

    private static BigDecimal fromMillisExact(BigDecimal time, TimeUnit unit) {
        switch (unit) {
            case SECONDS:
                return divide(time, 1000);
            case MINUTES:
                time = fromMillisExact(time, TimeUnit.SECONDS);
                return divide(time, 60);
            case HOURS:
                time = fromMillisExact(time, TimeUnit.MINUTES);
                return divide(time, 60);
            case DAYS:
                time = fromMillisExact(time, TimeUnit.HOURS);
                return divide(time, 24);
            default:
                return time;
        }
    }

    private static BigDecimal divide(BigDecimal what, long by) {
        return what.divide(BigDecimal.valueOf(by));
    }

    private static boolean equals(BigDecimal first, long second) {
        return first.compareTo(BigDecimal.valueOf(second)) == 0;
    }
}
