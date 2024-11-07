package y2k;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class RT {

  public static Object[] into_array(Object xs) {
    var col = (List<Object>) xs;
    var result = (Object[]) Array.newInstance(col.get(0).getClass(), col.size());
    return col.toArray(result);
  }

  public static String str(Object... args) {
    if (args.length == 1) {
      return args[0].toString();
    }
    StringBuilder sb = new StringBuilder();
    for (Object arg : args) {
      sb.append(arg);
    }
    return sb.toString();
  }

  /**
   * @noinspection unchecked
   */
  public static <T> T get(Object source, Object key) {
    if (source instanceof java.util.Map) {
      return (T) ((java.util.Map<?, ?>) source).get(key);
    }
    if (source instanceof java.util.List) {
      return (T) ((java.util.List<?>) source).get((Integer) key);
    }
    throw new RuntimeException("Unsupported source: " + source);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void throwException(
      Throwable exception,
      Object dummy) throws T {
    throw (T) exception;
  }

  public static <T> T throw_(Object exception) {
    throwException((Throwable) exception, null);
    return null;
  }

  public static <T> T try_(Callable<T> f) {
    try {
      return f.call();
    } catch (Exception e) {
      RT.throwException(e, null);
      return null;
    }
  }

  public static Object println(Object... xs) {
    for (Object x : xs) {
      System.out.print(x);
      System.out.print(" ");
    }
    System.out.println();
    return null;
  }

  public static Runnable runnable(Supplier<Object> f) {
    return f::get;
  }
}
