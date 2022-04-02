package model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class RollingUser implements Comparable<RollingUser> {
  private String name;
  private int number;

  @Override
  public String toString() {
    return String.format("%s = %s", this.name, this.number);
  }

  @Override
  public int compareTo(@NotNull RollingUser o) {
    return o.getNumber() - this.getNumber();
  }
}
